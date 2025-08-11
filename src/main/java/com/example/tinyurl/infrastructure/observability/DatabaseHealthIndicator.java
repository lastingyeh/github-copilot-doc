package com.example.tinyurl.infrastructure.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 資料庫健康檢查指標
 * 透過執行簡單查詢測試資料庫連線狀態
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHealthIndicator.class);
    private static final String HEALTH_CHECK_QUERY = "SELECT 1";
    private static final int EXPECTED_RESULT = 1;

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try {
            return performHealthCheck();
        } catch (SQLException e) {
            log.error("資料庫健康檢查失敗 - SQL 異常: {}", e.getMessage());
            return Health.down()
                    .withDetail("database", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .withDetail("sqlState", e.getSQLState())
                    .withDetail("errorCode", e.getErrorCode())
                    .build();
        } catch (Exception e) {
            log.error("資料庫健康檢查發生未預期異常", e);
            return Health.down()
                    .withDetail("database", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getSimpleName())
                    .build();
        }
    }

    private Health performHealthCheck() throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(HEALTH_CHECK_QUERY);
                ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next() && resultSet.getInt(1) == EXPECTED_RESULT) {
                log.debug("資料庫健康檢查通過");
                return Health.up()
                        .withDetail("database", "Available")
                        .withDetail("connection", "Active")
                        .withDetail("url", connection.getMetaData().getURL())
                        .withDetail("driver", connection.getMetaData().getDriverName())
                        .withDetail("validationQuery", HEALTH_CHECK_QUERY)
                        .build();
            } else {
                log.warn("資料庫健康檢查失敗 - 查詢結果不符預期");
                return Health.down()
                        .withDetail("database", "Query validation failed")
                        .withDetail("validationQuery", HEALTH_CHECK_QUERY)
                        .withDetail("expectedResult", EXPECTED_RESULT)
                        .build();
            }
        }
    }
}
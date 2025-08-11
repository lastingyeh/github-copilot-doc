package com.example.tinyurl.infrastructure.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis 健康檢查指標
 * 測試 Redis 連線是否正常運作
 */
@Component
public class RedisHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(RedisHealthIndicator.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHealthIndicator(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Health health() {
        try {
            // 生成測試用的唯一鍵值
            String testKey = "health:check:" + System.currentTimeMillis();
            String testValue = "OK";

            // 設定測試值，5秒後自動過期
            redisTemplate.opsForValue().set(testKey, testValue, Duration.ofSeconds(5));

            // 讀取測試值
            String result = (String) redisTemplate.opsForValue().get(testKey);

            // 清理測試鍵值
            redisTemplate.delete(testKey);

            if (testValue.equals(result)) {
                log.debug("Redis 健康檢查通過");
                return Health.up()
                        .withDetail("redis", "Available")
                        .withDetail("test", "Connection test successful")
                        .withDetail("operation", "SET/GET/DELETE")
                        .build();
            } else {
                log.warn("Redis 健康檢查失敗 - 測試值不符: 預期={}, 實際={}", testValue, result);
                return Health.down()
                        .withDetail("redis", "Failed")
                        .withDetail("test", "Value mismatch")
                        .withDetail("expected", testValue)
                        .withDetail("actual", result)
                        .build();
            }
        } catch (Exception e) {
            log.error("Redis 健康檢查發生例外", e);
            return Health.down()
                    .withDetail("redis", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getSimpleName())
                    .build();
        }
    }
}

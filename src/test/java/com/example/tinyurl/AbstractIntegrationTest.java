package com.example.tinyurl;

import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.repository.UrlRepository;
import com.example.tinyurl.infrastructure.persistence.jpa.UrlJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;

/**
 * 整合測試基類
 *
 * 提供共用的 Testcontainers 配置與測試工具
 * 使用真實的 PostgreSQL 與 Redis 環境進行測試
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@Transactional
public abstract class AbstractIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test")
      .withReuse(true);

  @Container
  static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
      .withExposedPorts(6379)
      .withReuse(true);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    // PostgreSQL 配置
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

    // Redis 配置
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }

  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  protected UrlRepository urlRepository;

  @Autowired
  protected UrlJpaRepository urlJpaRepository;

  @Autowired
  protected UrlCachePort cachePort;

  @Autowired
  protected EntityManager entityManager;

  @LocalServerPort
  protected int port;

  @BeforeEach
  void setUp() {
    // 清理測試資料
    urlJpaRepository.deleteAll();
    cachePort.evictAll();

    // 清理 JPA 快取
    if (entityManager != null) {
      entityManager.flush();
      entityManager.clear();
    }
  }

  /**
   * 取得測試伺服器的基礎 URL
   */
  protected String getBaseUrl() {
    return "http://localhost:" + port;
  }

  /**
   * 取得 API 基礎路徑
   */
  protected String getApiUrl() {
    return getBaseUrl() + "/api";
  }

  /**
   * 刷新 JPA 快取
   */
  protected void flushAndClear() {
    if (entityManager != null) {
      entityManager.flush();
      entityManager.clear();
    }
  }
}

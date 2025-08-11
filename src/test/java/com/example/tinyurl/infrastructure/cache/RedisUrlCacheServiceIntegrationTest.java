package com.example.tinyurl.infrastructure.cache;

import com.example.tinyurl.application.port.out.CacheStatistics;
import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis 快取服務整合測試
 *
 * 使用 Testcontainers 啟動真實的 Redis 環境
 * 測試快取的完整功能與 Cache-aside Pattern
 */
@SpringBootTest
@Testcontainers
@DisplayName("Redis 快取服務整合測試")
class RedisUrlCacheServiceIntegrationTest {

  @Container
  static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
      .withExposedPorts(6379)
      .withReuse(false);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }

  @Autowired
  private UrlCachePort cacheService;

  private Url testUrl;
  private ShortCode testShortCode;

  @BeforeEach
  void setUp() {
    // 清理快取
    cacheService.evictAll();

    // 準備測試資料
    testShortCode = new ShortCode("abc123");
    LongUrl longUrl = new LongUrl("https://www.example.com/very/long/url");
    testUrl = Url.create(longUrl, testShortCode);
  }

  @Test
  @DisplayName("應該能正確快取與檢索 URL")
  void shouldCacheAndRetrieveUrl() {
    // Given
    Url url = testUrl;

    // When
    cacheService.cache(url);
    Optional<Url> cached = cacheService.findByShortCode(url.getShortCode());

    // Then
    assertThat(cached).isPresent();
    assertThat(cached.get().getLongUrl()).isEqualTo(url.getLongUrl());
    assertThat(cached.get().getShortCode()).isEqualTo(url.getShortCode());
    assertThat(cached.get().getCreatedAt()).isEqualTo(url.getCreatedAt());
  }

  @Test
  @DisplayName("當快取中沒有資料時應該回傳空值")
  void shouldReturnEmptyWhenNotFound() {
    // Given
    ShortCode nonExistentCode = new ShortCode("notfound");

    // When
    Optional<Url> result = cacheService.findByShortCode(nonExistentCode);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("應該支援自訂 TTL 的快取")
  void shouldSupportCustomTtl() throws InterruptedException {
    // Given
    Url url = testUrl;
    Duration shortTtl = Duration.ofSeconds(1);

    // When
    cacheService.cache(url, shortTtl);

    // 立即檢查 - 應該存在
    Optional<Url> immediateResult = cacheService.findByShortCode(url.getShortCode());
    assertThat(immediateResult).isPresent();

    // 等待 TTL 過期
    Thread.sleep(1100);

    // 過期後檢查 - 應該不存在
    Optional<Url> expiredResult = cacheService.findByShortCode(url.getShortCode());

    // Then
    assertThat(expiredResult).isEmpty();
  }

  @Test
  @DisplayName("應該能清除指定的快取項目")
  void shouldEvictSpecificCacheItem() {
    // Given
    Url url = testUrl;
    cacheService.cache(url);

    // 驗證快取存在
    assertThat(cacheService.findByShortCode(url.getShortCode())).isPresent();

    // When
    cacheService.evict(url.getShortCode());

    // Then
    assertThat(cacheService.findByShortCode(url.getShortCode())).isEmpty();
  }

  @Test
  @DisplayName("應該能清除所有快取")
  void shouldEvictAllCache() {
    // Given
    Url url1 = testUrl;
    Url url2 = Url.create(new LongUrl("https://another.example.com"), new ShortCode("xyz789"));

    cacheService.cache(url1);
    cacheService.cache(url2);

    // 驗證快取存在
    assertThat(cacheService.findByShortCode(url1.getShortCode())).isPresent();
    assertThat(cacheService.findByShortCode(url2.getShortCode())).isPresent();

    // When
    cacheService.evictAll();

    // Then
    assertThat(cacheService.findByShortCode(url1.getShortCode())).isEmpty();
    assertThat(cacheService.findByShortCode(url2.getShortCode())).isEmpty();
  }

  @Test
  @DisplayName("應該提供快取統計資訊")
  void shouldProvideStatistics() {
    // Given
    Url url = testUrl;
    cacheService.cache(url);

    // When - 執行一些快取操作
    cacheService.findByShortCode(url.getShortCode()); // hit
    cacheService.findByShortCode(new ShortCode("notfound")); // miss

    // Then
    CacheStatistics stats = cacheService.getStatistics();
    assertThat(stats).isNotNull();
    assertThat(stats.getHitCount()).isGreaterThanOrEqualTo(1);
    assertThat(stats.getMissCount()).isGreaterThanOrEqualTo(1);
    assertThat(stats.getTotalRequests()).isGreaterThanOrEqualTo(2);
  }

  @Test
  @DisplayName("使用預設 TTL 時應該基於存取次數決定快取時間")
  void shouldUseOptimalTtlBasedOnAccessCount() {
    // Given - 建立一個高存取次數的 URL
    Url popularUrl = Url.restore(
        testShortCode,
        new LongUrl("https://popular.example.com"),
        testUrl.getCreatedAt(),
        testUrl.getAccessedAt(),
        150 // 超過熱門閾值
    );

    // When
    cacheService.cache(popularUrl); // 使用預設 TTL（會根據存取次數決定）

    // Then
    Optional<Url> cached = cacheService.findByShortCode(popularUrl.getShortCode());
    assertThat(cached).isPresent();
    assertThat(cached.get().getAccessCount()).isEqualTo(150);
  }

  @Test
  @DisplayName("應該能處理 JSON 序列化的複雜時間格式")
  void shouldHandleComplexTimeFormats() {
    // Given
    Url urlWithAccess = testUrl.recordAccess(); // 更新存取時間

    // When
    cacheService.cache(urlWithAccess);
    Optional<Url> cached = cacheService.findByShortCode(urlWithAccess.getShortCode());

    // Then
    assertThat(cached).isPresent();
    assertThat(cached.get().getAccessedAt()).isNotNull();
    assertThat(cached.get().getAccessCount()).isEqualTo(1);
    assertThat(cached.get().getCreatedAt()).isEqualTo(urlWithAccess.getCreatedAt());
  }
}

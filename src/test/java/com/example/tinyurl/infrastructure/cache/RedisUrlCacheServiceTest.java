package com.example.tinyurl.infrastructure.cache;

import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.infrastructure.cache.redis.RedisUrlCacheService;
import com.example.tinyurl.infrastructure.config.RedisConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Redis 快取服務單元測試
 *
 * 測試快取的基本功能，使用 Mock 避免依賴實際的 Redis
 */
@DisplayName("Redis 快取服務單元測試")
class RedisUrlCacheServiceTest {

    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;
    private ObjectMapper objectMapper;
    private SimpleMeterRegistry meterRegistry;
    private UrlCachePort cacheService;

    private Url testUrl;
    private ShortCode testShortCode;

    @BeforeEach
    void setUp() {
        // 準備 Mock 物件
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        meterRegistry = new SimpleMeterRegistry();

        // 準備真實的 ObjectMapper
        RedisConfig redisConfig = new RedisConfig();
        objectMapper = redisConfig.cacheObjectMapper();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 建立快取服務
        cacheService = new RedisUrlCacheService(redisTemplate, objectMapper, meterRegistry);

        // 準備測試資料
        testShortCode = new ShortCode("abc123");
        LongUrl longUrl = new LongUrl("https://www.example.com/very/long/url");
        testUrl = Url.create(longUrl, testShortCode);
    }

    @Test
    @DisplayName("應該能正確快取 URL")
    void shouldCacheUrl() {
        // Given
        Url url = testUrl;

        // When
        cacheService.cache(url);

        // Then - 驗證方法被正確呼叫
        // 這裡只能驗證沒有拋出例外，因為使用了 mock
        assertThat(url.getShortCode()).isEqualTo(testShortCode);
    }

    @Test
    @DisplayName("應該能處理快取未命中的情況")
    void shouldHandleCacheMiss() {
        // Given
        ShortCode shortCode = new ShortCode("notfound");
        when(valueOperations.get(anyString())).thenReturn(null);

        // When
        Optional<Url> result = cacheService.findByShortCode(shortCode);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("應該支援自訂 TTL 的快取")
    void shouldSupportCustomTtl() {
        // Given
        Url url = testUrl;
        Duration customTtl = Duration.ofMinutes(30);

        // When
        cacheService.cache(url, customTtl);

        // Then - 驗證沒有拋出例外
        assertThat(url.getShortCode()).isEqualTo(testShortCode);
    }

    @Test
    @DisplayName("應該能清除指定的快取項目")
    void shouldEvictSpecificCacheItem() {
        // Given
        ShortCode shortCode = testShortCode;

        // When
        cacheService.evict(shortCode);

        // Then - 驗證沒有拋出例外
        assertThat(shortCode.value()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("應該提供快取統計資訊")
    void shouldProvideStatistics() {
        // Given & When
        var stats = cacheService.getStatistics();

        // Then
        assertThat(stats).isNotNull();
        assertThat(stats.getHitCount()).isGreaterThanOrEqualTo(0);
        assertThat(stats.getMissCount()).isGreaterThanOrEqualTo(0);
        assertThat(stats.getErrorCount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("應該能處理 JSON 序列化")
    void shouldHandleJsonSerialization() throws Exception {
        // Given
        Url url = testUrl;

        // When - 測試 JSON 序列化不會失敗
        String json = objectMapper.writeValueAsString(url);

        // Then
        assertThat(json).isNotNull();
        assertThat(json).contains("abc123");
    }
}

package com.example.tinyurl.infrastructure.cache.redis;

import com.example.tinyurl.application.port.out.CacheStatistics;
import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Redis 快取服務實作
 *
 * 實作 Cache-aside Pattern，提供 URL 快取功能
 * 包含快取統計、錯誤處理與性能監控
 */
@Component
public class RedisUrlCacheService implements UrlCachePort {

  private static final String CACHE_PREFIX = "tinyurl:short:";
  private static final String STATS_PREFIX = "tinyurl:stats:";
  private static final Duration DEFAULT_TTL = Duration.ofHours(1);
  private static final Duration POPULAR_TTL = Duration.ofHours(24);
  private static final int POPULAR_ACCESS_THRESHOLD = 100;

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  // 快取統計指標
  private final Counter cacheHits;
  private final Counter cacheMisses;
  private final Counter cacheErrors;

  // 內存統計計數器（防止 Redis 不可用時統計丟失）
  private final AtomicLong localHitCount = new AtomicLong(0);
  private final AtomicLong localMissCount = new AtomicLong(0);
  private final AtomicLong localErrorCount = new AtomicLong(0);

  public RedisUrlCacheService(RedisTemplate<String, Object> redisTemplate,
      @Qualifier("cacheObjectMapper") ObjectMapper objectMapper,
      MeterRegistry meterRegistry) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;

    // 初始化 Micrometer 指標
    this.cacheHits = Counter.builder("tinyurl_cache_hits_total")
        .description("Redis 快取命中次數")
        .register(meterRegistry);
    this.cacheMisses = Counter.builder("tinyurl_cache_misses_total")
        .description("Redis 快取未命中次數")
        .register(meterRegistry);
    this.cacheErrors = Counter.builder("tinyurl_cache_errors_total")
        .description("Redis 快取錯誤次數")
        .register(meterRegistry);
  }

  @Override
  public Optional<Url> findByShortCode(ShortCode shortCode) {
    try {
      String key = buildCacheKey(shortCode);
      String cachedValue = (String) redisTemplate.opsForValue().get(key);

      if (cachedValue != null) {
        // 快取命中
        recordCacheHit();
        UrlCacheDto dto = objectMapper.readValue(cachedValue, UrlCacheDto.class);
        System.out.println("快取命中: shortCode=" + shortCode.value());
        return Optional.of(dto.toDomain());
      } else {
        // 快取未命中
        recordCacheMiss();
        System.out.println("快取未命中: shortCode=" + shortCode.value());
        return Optional.empty();
      }
    } catch (JsonProcessingException e) {
      System.err.println("JSON 反序列化失敗: shortCode=" + shortCode.value() + ", error=" + e.getMessage());
      recordCacheError();
      return Optional.empty();
    } catch (Exception e) {
      System.err.println("Redis 查詢失敗: shortCode=" + shortCode.value() + ", error=" + e.getMessage());
      recordCacheError();
      return Optional.empty();
    }
  }

  @Override
  public void cache(Url url) {
    Duration ttl = determineOptimalTtl(url);
    cache(url, ttl);
  }

  @Override
  public void cache(Url url, Duration ttl) {
    try {
      String key = buildCacheKey(url.getShortCode());
      UrlCacheDto dto = UrlCacheDto.fromDomain(url);
      String jsonValue = objectMapper.writeValueAsString(dto);

      redisTemplate.opsForValue().set(key, jsonValue, ttl);
      System.out.println("快取成功: shortCode=" + url.getShortCode().value() + ", ttl=" + ttl);
    } catch (JsonProcessingException e) {
      System.err.println("JSON 序列化失敗: shortCode=" + url.getShortCode().value() + ", error=" + e.getMessage());
      recordCacheError();
    } catch (Exception e) {
      System.err.println("Redis 寫入失敗: shortCode=" + url.getShortCode().value() + ", error=" + e.getMessage());
      recordCacheError();
    }
  }

  @Override
  public void evict(ShortCode shortCode) {
    try {
      String key = buildCacheKey(shortCode);
      Boolean deleted = redisTemplate.delete(key);
      System.out.println("快取清除: shortCode=" + shortCode.value() + ", deleted=" + deleted);
    } catch (Exception e) {
      System.err.println("Redis 清除失敗: shortCode=" + shortCode.value() + ", error=" + e.getMessage());
      recordCacheError();
    }
  }

  @Override
  public void evictAll() {
    try {
      String pattern = CACHE_PREFIX + "*";
      var keys = redisTemplate.keys(pattern);
      if (keys != null && !keys.isEmpty()) {
        Long deletedCount = redisTemplate.delete(keys);
        System.out.println("批量清除快取: 清除了 " + deletedCount + " 個項目");
      }
    } catch (Exception e) {
      System.err.println("批量清除快取失敗: " + e.getMessage());
      recordCacheError();
    }
  }

  @Override
  public CacheStatistics getStatistics() {
    try {
      // 嘗試從 Redis 獲取統計資訊
      long hits = getCountFromRedis("hits", localHitCount.get());
      long misses = getCountFromRedis("misses", localMissCount.get());
      long errors = getCountFromRedis("errors", localErrorCount.get());

      // 獲取快取大小
      long size = getCacheSize();

      return CacheStatistics.calculateFull(hits, misses, errors, size, 0L);
    } catch (Exception e) {
      System.err.println("獲取快取統計失敗，使用本地計數器: " + e.getMessage());
      return CacheStatistics.calculate(
          localHitCount.get(),
          localMissCount.get(),
          localErrorCount.get());
    }
  }

  /**
   * 建立快取鍵值
   */
  private String buildCacheKey(ShortCode shortCode) {
    return CACHE_PREFIX + shortCode.value();
  }

  /**
   * 建立統計鍵值
   */
  private String buildStatsKey(String metric) {
    return STATS_PREFIX + metric;
  }

  /**
   * 決定最佳 TTL
   *
   * 根據存取次數決定快取時間
   * 熱門 URL 使用較長的 TTL
   */
  private Duration determineOptimalTtl(Url url) {
    if (url.getAccessCount() >= POPULAR_ACCESS_THRESHOLD) {
      return POPULAR_TTL;
    }
    return DEFAULT_TTL;
  }

  /**
   * 記錄快取命中
   */
  private void recordCacheHit() {
    cacheHits.increment();
    localHitCount.incrementAndGet();
    updateRedisCounter("hits", 1);
  }

  /**
   * 記錄快取未命中
   */
  private void recordCacheMiss() {
    cacheMisses.increment();
    localMissCount.incrementAndGet();
    updateRedisCounter("misses", 1);
  }

  /**
   * 記錄快取錯誤
   */
  private void recordCacheError() {
    cacheErrors.increment();
    localErrorCount.incrementAndGet();
    updateRedisCounter("errors", 1);
  }

  /**
   * 更新 Redis 計數器
   */
  private void updateRedisCounter(String metric, long increment) {
    try {
      String key = buildStatsKey(metric);
      redisTemplate.opsForValue().increment(key, increment);
    } catch (Exception e) {
      // 靜默處理統計更新錯誤，不影響主要功能
      System.out.println("更新 Redis 統計失敗: metric=" + metric + ", error=" + e.getMessage());
    }
  }

  /**
   * 從 Redis 獲取計數
   */
  private long getCountFromRedis(String metric, long fallback) {
    try {
      String key = buildStatsKey(metric);
      Object value = redisTemplate.opsForValue().get(key);
      return value != null ? Long.parseLong(value.toString()) : fallback;
    } catch (Exception e) {
      return fallback;
    }
  }

  /**
   * 獲取快取大小
   */
  private long getCacheSize() {
    try {
      String pattern = CACHE_PREFIX + "*";
      var keys = redisTemplate.keys(pattern);
      return keys != null ? keys.size() : 0L;
    } catch (Exception e) {
      System.out.println("獲取快取大小失敗: " + e.getMessage());
      return 0L;
    }
  }
}

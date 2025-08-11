package com.example.tinyurl.infrastructure.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

/**
 * TinyURL 業務指標收集器
 * 負責收集與短網址服務相關的業務指標
 */
@Component
public class UrlMetrics {

  private final MeterRegistry meterRegistry;

  // 計數器
  private final Counter urlCreatedCounter;
  private final Counter urlAccessedCounter;
  private final Counter cacheHitCounter;
  private final Counter cacheMissCounter;

  // 計時器
  private final Timer urlCreationTimer;
  private final Timer urlLookupTimer;
  private final Timer redirectTimer;

  public UrlMetrics(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;

    // 建立計數器
    this.urlCreatedCounter = Counter.builder("tinyurl.urls.created.total")
        .description("總建立的短網址數量")
        .register(meterRegistry);

    this.urlAccessedCounter = Counter.builder("tinyurl.urls.accessed.total")
        .description("總存取的短網址數量")
        .register(meterRegistry);

    this.cacheHitCounter = Counter.builder("tinyurl.cache.hits.total")
        .description("快取命中次數")
        .register(meterRegistry);

    this.cacheMissCounter = Counter.builder("tinyurl.cache.misses.total")
        .description("快取未命中次數")
        .register(meterRegistry);

    // 建立計時器
    this.urlCreationTimer = Timer.builder("tinyurl.url.creation.duration")
        .description("建立短網址的處理時間")
        .register(meterRegistry);

    this.urlLookupTimer = Timer.builder("tinyurl.url.lookup.duration")
        .description("查詢短網址的處理時間")
        .register(meterRegistry);

    this.redirectTimer = Timer.builder("tinyurl.redirect.duration")
        .description("重定向處理時間")
        .register(meterRegistry);
  }

  /**
   * 增加 URL 建立計數
   */
  public void incrementUrlCreated() {
    urlCreatedCounter.increment();
  }

  /**
   * 增加 URL 存取計數
   */
  public void incrementUrlAccessed() {
    urlAccessedCounter.increment();
  }

  /**
   * 增加快取命中計數
   */
  public void incrementCacheHit() {
    cacheHitCounter.increment();
  }

  /**
   * 增加快取未命中計數
   */
  public void incrementCacheMiss() {
    cacheMissCounter.increment();
  }

  /**
   * 增加錯誤計數
   */
  public void incrementError(String errorType) {
    Counter.builder("tinyurl.errors.total")
        .description("總錯誤數量")
        .tag("type", errorType)
        .register(meterRegistry)
        .increment();
  }

  /**
   * 開始計時 URL 建立
   */
  public Timer.Sample startUrlCreationTimer() {
    return Timer.start(meterRegistry);
  }

  /**
   * 開始計時 URL 查詢
   */
  public Timer.Sample startUrlLookupTimer() {
    return Timer.start(meterRegistry);
  }

  /**
   * 開始計時重定向
   */
  public Timer.Sample startRedirectTimer() {
    return Timer.start(meterRegistry);
  }

  /**
   * 記錄 URL 建立時間
   */
  public void recordUrlCreationTime(Timer.Sample sample) {
    sample.stop(urlCreationTimer);
  }

  /**
   * 記錄 URL 查詢時間
   */
  public void recordUrlLookupTime(Timer.Sample sample) {
    sample.stop(urlLookupTimer);
  }

  /**
   * 記錄重定向時間
   */
  public void recordRedirectTime(Timer.Sample sample) {
    sample.stop(redirectTimer);
  }

  /**
   * 取得快取命中率
   */
  public double getCacheHitRatio() {
    double hits = cacheHitCounter.count();
    double misses = cacheMissCounter.count();
    double total = hits + misses;

    if (total == 0) {
      return 0.0;
    }

    return hits / total;
  }
}

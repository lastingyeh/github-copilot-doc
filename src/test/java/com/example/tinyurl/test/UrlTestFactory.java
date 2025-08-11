package com.example.tinyurl.test;

import com.example.tinyurl.adapters.web.dto.CreateUrlRequest;
import com.example.tinyurl.application.port.in.CreateShortUrlCommand;
import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 測試資料工廠
 *
 * 提供一致且可預測的測試資料建立方法
 * 確保測試間的資料不會衝突
 */
public class UrlTestFactory {

  private static final AtomicInteger COUNTER = new AtomicInteger(1);

  /**
   * 建立預設的 URL 測試資料
   */
  public static Url createUrl() {
    int num = COUNTER.getAndIncrement();
    return createUrl("https://example" + num + ".com", "url" + String.format("%03d", num));
  }

  /**
   * 建立指定長網址和短網址的 URL 測試資料
   */
  public static Url createUrl(String longUrl, String shortCode) {
    return Url.create(new LongUrl(longUrl), new ShortCode(shortCode));
  }

  /**
   * 建立指定時間的 URL 測試資料
   */
  public static Url createUrlWithTime(String longUrl, String shortCode, LocalDateTime createdAt) {
    // 使用 restore 方法建立歷史資料
    return Url.restore(
        new ShortCode(shortCode),
        new LongUrl(longUrl),
        createdAt,
        null,
        0);
  }

  /**
   * 建立指定存取次數的 URL 測試資料
   */
  public static Url createUrlWithAccessCount(String longUrl, String shortCode, int accessCount) {
    LocalDateTime now = LocalDateTime.now();
    return Url.restore(
        new ShortCode(shortCode),
        new LongUrl(longUrl),
        now.minusHours(1),
        accessCount > 0 ? now.minusMinutes(accessCount) : null,
        accessCount);
  }

  /**
   * 建立預設的 CreateShortUrlCommand
   */
  public static CreateShortUrlCommand createCommand() {
    return createCommand("https://example.com");
  }

  /**
   * 建立指定長網址的 CreateShortUrlCommand
   */
  public static CreateShortUrlCommand createCommand(String longUrl) {
    return new CreateShortUrlCommand(longUrl, Duration.ofHours(1));
  }

  /**
   * 建立指定長網址和 TTL 的 CreateShortUrlCommand
   */
  public static CreateShortUrlCommand createCommand(String longUrl, Duration ttl) {
    return new CreateShortUrlCommand(longUrl, ttl);
  }

  /**
   * 建立預設的 CreateUrlRequest
   */
  public static CreateUrlRequest createRequest() {
    return createRequest("https://example.com");
  }

  /**
   * 建立指定長網址的 CreateUrlRequest
   */
  public static CreateUrlRequest createRequest(String longUrl) {
    return new CreateUrlRequest(longUrl, 3600L);
  }

  /**
   * 建立指定長網址和 TTL 的 CreateUrlRequest
   */
  public static CreateUrlRequest createRequest(String longUrl, Long ttlSeconds) {
    return new CreateUrlRequest(longUrl, ttlSeconds);
  }

  /**
   * 建立不重複的長網址
   */
  public static String createUniqueLongUrl() {
    return "https://unique" + COUNTER.getAndIncrement() + ".example.com";
  }

  /**
   * 建立不重複的短網址碼
   */
  public static String createUniqueShortCode() {
    return "test" + String.format("%03d", COUNTER.getAndIncrement());
  }

  /**
   * 重設計數器（用於測試隔離）
   */
  public static void resetCounter() {
    COUNTER.set(1);
  }
}

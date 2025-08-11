package com.example.tinyurl.infrastructure.cache.redis;

import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * URL 快取 DTO
 *
 * 用於 Redis 快取的數據傳輸物件
 * 提供領域模型與快取數據之間的轉換
 */
public class UrlCacheDto {

  /** 短網址代碼 */
  private String shortCode;

  /** 長網址 */
  private String longUrl;

  /** 建立時間 */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /** 最後存取時間 */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime accessedAt;

  /** 存取次數 */
  private int accessCount;

  // 無參建構子 (JSON 反序列化需要)
  public UrlCacheDto() {
  }

  public UrlCacheDto(String shortCode, String longUrl, LocalDateTime createdAt,
      LocalDateTime accessedAt, int accessCount) {
    this.shortCode = shortCode;
    this.longUrl = longUrl;
    this.createdAt = createdAt;
    this.accessedAt = accessedAt;
    this.accessCount = accessCount;
  }

  /**
   * 從領域模型轉換為快取 DTO
   *
   * @param url 領域模型的 URL 物件
   * @return 快取 DTO
   */
  public static UrlCacheDto fromDomain(Url url) {
    return new UrlCacheDto(
        url.getShortCode().value(),
        url.getLongUrl().value(),
        url.getCreatedAt(),
        url.getAccessedAt(),
        url.getAccessCount());
  }

  /**
   * 從快取 DTO 轉換為領域模型
   *
   * @return 領域模型的 URL 物件
   */
  public Url toDomain() {
    return Url.restore(
        new ShortCode(shortCode),
        new LongUrl(longUrl),
        createdAt,
        accessedAt,
        accessCount);
  }

  // Getters and Setters
  public String getShortCode() {
    return shortCode;
  }

  public void setShortCode(String shortCode) {
    this.shortCode = shortCode;
  }

  public String getLongUrl() {
    return longUrl;
  }

  public void setLongUrl(String longUrl) {
    this.longUrl = longUrl;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getAccessedAt() {
    return accessedAt;
  }

  public void setAccessedAt(LocalDateTime accessedAt) {
    this.accessedAt = accessedAt;
  }

  public int getAccessCount() {
    return accessCount;
  }

  public void setAccessCount(int accessCount) {
    this.accessCount = accessCount;
  }
}

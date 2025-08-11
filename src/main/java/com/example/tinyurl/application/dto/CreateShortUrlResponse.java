package com.example.tinyurl.application.dto;

import com.example.tinyurl.domain.model.Url;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 建立短網址回應 DTO
 *
 * <p>
 * 封裝建立短網址操作的結果資料。
 * 包含新建立的短網址相關資訊。
 *
 * @param shortCode 短網址碼
 * @param longUrl   原始長網址
 * @param shortUrl  完整的短網址 URL
 * @param createdAt 建立時間
 * @param ttl       快取存活時間
 */
public record CreateShortUrlResponse(
    String shortCode,
    String longUrl,
    String shortUrl,
    LocalDateTime createdAt,
    Duration ttl) {

  /**
   * 從領域對象建立回應 DTO
   *
   * @param url     領域 URL 對象
   * @param baseUrl 基礎 URL，用於組成完整短網址
   * @return 建立短網址回應
   */
  public static CreateShortUrlResponse from(Url url, String baseUrl) {
    return new CreateShortUrlResponse(
        url.getShortCode().value(),
        url.getLongUrl().value(),
        baseUrl + "/" + url.getShortCode().value(),
        url.getCreatedAt(),
        null // TTL 由快取層管理
    );
  }

  /**
   * 從領域對象建立回應 DTO，包含 TTL 資訊
   *
   * @param url     領域 URL 對象
   * @param baseUrl 基礎 URL，用於組成完整短網址
   * @param ttl     快取存活時間
   * @return 建立短網址回應
   */
  public static CreateShortUrlResponse from(Url url, String baseUrl, Duration ttl) {
    return new CreateShortUrlResponse(
        url.getShortCode().value(),
        url.getLongUrl().value(),
        baseUrl + "/" + url.getShortCode().value(),
        url.getCreatedAt(),
        ttl);
  }
}

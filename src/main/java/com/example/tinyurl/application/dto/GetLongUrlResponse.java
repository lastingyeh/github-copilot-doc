package com.example.tinyurl.application.dto;

import com.example.tinyurl.domain.model.Url;

import java.time.LocalDateTime;

/**
 * 取得長網址回應 DTO
 *
 * <p>
 * 封裝查詢長網址操作的結果資料。
 * 包含查詢到的 URL 相關資訊與存取統計。
 *
 * @param shortCode   短網址碼
 * @param longUrl     原始長網址
 * @param createdAt   建立時間
 * @param accessedAt  最後存取時間
 * @param accessCount 存取次數
 */
public record GetLongUrlResponse(
    String shortCode,
    String longUrl,
    LocalDateTime createdAt,
    LocalDateTime accessedAt,
    int accessCount) {

  /**
   * 從領域對象建立回應 DTO
   *
   * @param url 領域 URL 對象
   * @return 取得長網址回應
   */
  public static GetLongUrlResponse from(Url url) {
    return new GetLongUrlResponse(
        url.getShortCode().value(),
        url.getLongUrl().value(),
        url.getCreatedAt(),
        url.getAccessedAt(),
        url.getAccessCount());
  }
}

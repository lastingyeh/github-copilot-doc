package com.example.tinyurl.application.dto;

import java.time.LocalDateTime;

/**
 * 重定向網址回應 DTO
 *
 * <p>
 * 封裝重定向操作的結果資料。
 * 包含重定向目標與重定向類型資訊。
 *
 * @param longUrl      目標長網址
 * @param redirectType 重定向類型
 * @param accessedAt   存取時間
 */
public record RedirectUrlResponse(
    String longUrl,
    RedirectType redirectType,
    LocalDateTime accessedAt) {

  /**
   * 重定向類型枚舉
   */
  public enum RedirectType {
    /**
     * 暫時重定向 (HTTP 302)
     * 適用於短網址服務，不會被瀏覽器快取
     */
    TEMPORARY,

    /**
     * 永久重定向 (HTTP 301)
     * 會被瀏覽器快取，適用於永久性重定向
     */
    PERMANENT
  }

  /**
   * 建立暫時重定向回應
   *
   * @param longUrl 目標長網址
   * @return 重定向回應
   */
  public static RedirectUrlResponse temporaryRedirect(String longUrl) {
    return new RedirectUrlResponse(
        longUrl,
        RedirectType.TEMPORARY,
        LocalDateTime.now());
  }

  /**
   * 建立永久重定向回應
   *
   * @param longUrl 目標長網址
   * @return 重定向回應
   */
  public static RedirectUrlResponse permanentRedirect(String longUrl) {
    return new RedirectUrlResponse(
        longUrl,
        RedirectType.PERMANENT,
        LocalDateTime.now());
  }
}

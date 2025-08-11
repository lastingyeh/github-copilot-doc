package com.example.tinyurl.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * 建立短網址指令
 *
 * <p>
 * 封裝建立短網址所需的輸入參數。
 * 包含 URL 驗證邏輯，確保輸入資料的正確性。
 *
 * @param longUrl 原始長網址，不可為空且長度限制在 2048 字元內
 * @param ttl     快取存活時間，可選參數
 */
public record CreateShortUrlCommand(
    @NotBlank(message = "長網址不可為空") @Size(max = 2048, message = "網址長度不可超過 2048 字元") String longUrl,

    @Nullable Duration ttl) {
  public CreateShortUrlCommand {
    if (longUrl != null && longUrl.trim().isEmpty()) {
      throw new IllegalArgumentException("長網址不可為空白");
    }

    if (longUrl != null && !isValidUrl(longUrl)) {
      throw new IllegalArgumentException("網址格式不正確");
    }
  }

  /**
   * 驗證 URL 格式
   *
   * @param url 待驗證的 URL
   * @return 是否為有效的 URL 格式
   */
  private boolean isValidUrl(String url) {
    return url.startsWith("http://") || url.startsWith("https://");
  }
}

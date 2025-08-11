package com.example.tinyurl.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Duration;

/**
 * 建立短網址請求 DTO
 *
 * <p>
 * 封裝 HTTP 請求中建立短網址所需的參數。
 * 提供完整的輸入驗證與 OpenAPI 文件。
 *
 * @param longUrl    原始長網址，必須是有效的 HTTP/HTTPS URL
 * @param ttlSeconds 快取存活時間（秒），可選參數
 */
@Schema(description = "建立短網址請求")
public record CreateUrlRequest(
    @Schema(description = "原始長網址", example = "https://example.com/very/long/path/to/resource") @NotBlank(message = "長網址不可為空") @Size(max = 2048, message = "網址長度不可超過 2048 字元") @Pattern(regexp = "^https?://.*", message = "網址必須以 http:// 或 https:// 開頭") String longUrl,

    @Schema(description = "快取存活時間（秒）", example = "3600", nullable = true) @PositiveOrZero(message = "TTL 秒數必須為正數") Long ttlSeconds) {
  /**
   * 轉換為 Duration 對象
   *
   * @return Duration 實例，如果 ttlSeconds 為 null 則返回 null
   */
  public Duration getTtl() {
    return ttlSeconds != null ? Duration.ofSeconds(ttlSeconds) : null;
  }
}

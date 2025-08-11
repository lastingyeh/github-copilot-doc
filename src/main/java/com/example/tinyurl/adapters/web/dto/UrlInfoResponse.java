package com.example.tinyurl.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * URL 資訊回應 DTO
 *
 * <p>
 * 封裝查詢 URL 資訊的回應資料。
 * 包含 URL 詳細資訊與存取統計。
 *
 * @param shortCode   短網址碼
 * @param longUrl     原始長網址
 * @param createdAt   建立時間
 * @param accessedAt  最後存取時間
 * @param accessCount 存取次數
 */
@Schema(description = "URL 資訊回應")
public record UrlInfoResponse(
    @Schema(description = "短網址碼", example = "abc123") @JsonProperty("short_code") String shortCode,

    @Schema(description = "原始長網址", example = "https://example.com/very/long/path/to/resource") @JsonProperty("long_url") String longUrl,

    @Schema(description = "建立時間", example = "2023-08-11T10:30:00") @JsonProperty("created_at") LocalDateTime createdAt,

    @Schema(description = "最後存取時間", example = "2023-08-11T11:45:00", nullable = true) @JsonProperty("accessed_at") LocalDateTime accessedAt,

    @Schema(description = "存取次數", example = "42") @JsonProperty("access_count") int accessCount) {
}

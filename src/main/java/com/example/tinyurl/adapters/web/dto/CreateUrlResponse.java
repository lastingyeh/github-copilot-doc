package com.example.tinyurl.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 建立短網址回應 DTO
 *
 * <p>
 * 封裝建立短網址操作的回應資料。
 * 包含新建立的短網址相關資訊。
 *
 * @param shortCode  短網址碼
 * @param longUrl    原始長網址
 * @param shortUrl   完整的短網址 URL
 * @param createdAt  建立時間
 * @param ttlSeconds 快取存活時間（秒）
 */
@Schema(description = "建立短網址回應")
public record CreateUrlResponse(
    @Schema(description = "短網址碼", example = "abc123") @JsonProperty("short_code") String shortCode,

    @Schema(description = "原始長網址", example = "https://example.com/very/long/path/to/resource") @JsonProperty("long_url") String longUrl,

    @Schema(description = "完整短網址", example = "http://localhost:8080/abc123") @JsonProperty("short_url") String shortUrl,

    @Schema(description = "建立時間", example = "2023-08-11T10:30:00") @JsonProperty("created_at") LocalDateTime createdAt,

    @Schema(description = "快取存活時間（秒）", example = "3600", nullable = true) @JsonProperty("ttl_seconds") Long ttlSeconds) {
}

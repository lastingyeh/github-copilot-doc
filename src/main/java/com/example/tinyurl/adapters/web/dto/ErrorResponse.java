package com.example.tinyurl.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 錯誤回應 DTO
 *
 * <p>
 * 統一的錯誤回應格式，包含錯誤代碼、訊息與詳細資訊。
 * 用於全域異常處理器的回應。
 *
 * @param code      錯誤代碼
 * @param message   錯誤訊息
 * @param details   錯誤詳情列表
 * @param traceId   請求追蹤 ID
 * @param timestamp 發生時間
 */
@Schema(description = "錯誤回應")
public record ErrorResponse(
    @Schema(description = "錯誤代碼", example = "VALIDATION_ERROR") String code,

    @Schema(description = "錯誤訊息", example = "請求參數驗證失敗") String message,

    @Schema(description = "錯誤詳情", example = "[\"longUrl: 長網址不可為空\", \"ttlSeconds: TTL 秒數必須為正數\"]") List<String> details,

    @Schema(description = "請求追蹤 ID", example = "req-123456") @JsonProperty("trace_id") String traceId,

    @Schema(description = "發生時間", example = "2023-08-11T10:30:00") LocalDateTime timestamp) {
}

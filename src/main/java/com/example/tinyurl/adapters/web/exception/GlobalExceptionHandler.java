package com.example.tinyurl.adapters.web.exception;

import com.example.tinyurl.adapters.web.dto.ErrorResponse;
import com.example.tinyurl.domain.exception.InvalidShortCodeException;
import com.example.tinyurl.domain.exception.InvalidUrlException;
import com.example.tinyurl.domain.exception.ShortCodeGenerationException;
import com.example.tinyurl.domain.exception.UrlNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 全域異常處理器
 *
 * <p>
 * 統一處理應用程式中的異常，提供一致的錯誤回應格式。
 * 支援多種異常類型的處理與適當的 HTTP 狀態碼映射。
 *
 * <p>
 * 處理的異常類型：
 * <ul>
 * <li>驗證異常：參數驗證失敗</li>
 * <li>領域異常：業務規則違反</li>
 * <li>技術異常：系統錯誤</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * 處理請求體驗證異常
   *
   * @param e 方法參數驗證異常
   * @return 錯誤回應
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException e) {
    String traceId = getOrCreateTraceId();

    List<String> errors = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.toList());

    log.warn("Validation error [{}]: {}", traceId, errors);

    ErrorResponse errorResponse = new ErrorResponse(
        "VALIDATION_ERROR",
        "請求參數驗證失敗",
        errors,
        traceId,
        LocalDateTime.now());

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * 處理路徑參數驗證異常
   *
   * @param e 約束違反異常
   * @return 錯誤回應
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
    String traceId = getOrCreateTraceId();

    List<String> errors = e.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());

    log.warn("Constraint violation [{}]: {}", traceId, errors);

    ErrorResponse errorResponse = new ErrorResponse(
        "VALIDATION_ERROR",
        "請求參數驗證失敗",
        errors,
        traceId,
        LocalDateTime.now());

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * 處理無效 URL 異常
   *
   * @param e 無效 URL 異常
   * @return 錯誤回應
   */
  @ExceptionHandler(InvalidUrlException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException e) {
    String traceId = getOrCreateTraceId();

    log.warn("Invalid URL [{}]: {}", traceId, e.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "INVALID_URL",
        "無效的 URL 格式",
        List.of(e.getMessage()),
        traceId,
        LocalDateTime.now());

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * 處理無效短網址碼異常
   *
   * @param e 無效短網址碼異常
   * @return 錯誤回應
   */
  @ExceptionHandler(InvalidShortCodeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleInvalidShortCode(InvalidShortCodeException e) {
    String traceId = getOrCreateTraceId();

    log.warn("Invalid short code [{}]: {}", traceId, e.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "INVALID_SHORT_CODE",
        "無效的短網址碼格式",
        List.of(e.getMessage()),
        traceId,
        LocalDateTime.now());

    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * 處理短網址碼生成異常
   *
   * @param e 短網址碼生成異常
   * @return 錯誤回應
   */
  @ExceptionHandler(ShortCodeGenerationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorResponse> handleShortCodeGeneration(ShortCodeGenerationException e) {
    String traceId = getOrCreateTraceId();

    log.error("Short code generation failed [{}]: {}", traceId, e.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "SHORT_CODE_GENERATION_FAILED",
        "短網址生成失敗，請稍後重試",
        List.of(e.getMessage()),
        traceId,
        LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  /**
   * 處理 URL 未找到異常
   *
   * @param e URL 未找到異常
   * @return 錯誤回應
   */
  @ExceptionHandler(UrlNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleUrlNotFound(UrlNotFoundException e) {
    String traceId = getOrCreateTraceId();

    log.debug("URL not found [{}]: {}", traceId, e.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "URL_NOT_FOUND",
        "找不到指定的短網址",
        List.of(e.getMessage()),
        traceId,
        LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * 處理一般異常
   *
   * @param e 異常
   * @return 錯誤回應
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleGenericError(Exception e) {
    String traceId = getOrCreateTraceId();

    log.error("Unexpected error occurred [{}]", traceId, e);

    ErrorResponse errorResponse = new ErrorResponse(
        "INTERNAL_SERVER_ERROR",
        "伺服器內部錯誤",
        List.of("請聯繫系統管理員"),
        traceId,
        LocalDateTime.now());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  /**
   * 取得或建立追蹤 ID
   *
   * @return 追蹤 ID
   */
  private String getOrCreateTraceId() {
    String traceId = MDC.get("traceId");
    if (traceId == null || traceId.isEmpty()) {
      traceId = "req-" + UUID.randomUUID().toString().substring(0, 8);
      MDC.put("traceId", traceId);
    }
    return traceId;
  }
}

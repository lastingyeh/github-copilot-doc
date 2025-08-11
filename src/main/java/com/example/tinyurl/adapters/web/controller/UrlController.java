package com.example.tinyurl.adapters.web.controller;

import com.example.tinyurl.adapters.web.dto.CreateUrlRequest;
import com.example.tinyurl.adapters.web.dto.CreateUrlResponse;
import com.example.tinyurl.adapters.web.dto.UrlInfoResponse;
import com.example.tinyurl.adapters.web.mapper.UrlWebMapper;
import com.example.tinyurl.application.dto.CreateShortUrlResponse;
import com.example.tinyurl.application.dto.GetLongUrlResponse;
import com.example.tinyurl.application.port.in.CreateShortUrlCommand;
import com.example.tinyurl.application.port.in.CreateShortUrlUseCase;
import com.example.tinyurl.application.port.in.GetLongUrlCommand;
import com.example.tinyurl.application.port.in.GetLongUrlQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * URL 管理控制器
 *
 * <p>
 * 提供短網址管理的 REST API 端點，包含：
 * <ul>
 * <li>建立短網址</li>
 * <li>查詢 URL 資訊</li>
 * <li>查詢統計資料</li>
 * </ul>
 *
 * <p>
 * API 設計遵循 RESTful 原則，提供標準的 HTTP 狀態碼與內容協商。
 */
@RestController
@RequestMapping("/api/urls")
@Validated
@Tag(name = "URL Management", description = "短網址管理 API")
public class UrlController {

  private static final Logger log = LoggerFactory.getLogger(UrlController.class);

  private final CreateShortUrlUseCase createShortUrlUseCase;
  private final GetLongUrlQuery getLongUrlQuery;
  private final UrlWebMapper mapper;

  public UrlController(CreateShortUrlUseCase createShortUrlUseCase,
      GetLongUrlQuery getLongUrlQuery,
      UrlWebMapper mapper) {
    this.createShortUrlUseCase = createShortUrlUseCase;
    this.getLongUrlQuery = getLongUrlQuery;
    this.mapper = mapper;
  }

  /**
   * 建立短網址
   *
   * @param request     建立請求
   * @param httpRequest HTTP 請求
   * @return 建立結果
   */
  @PostMapping
  @Operation(summary = "建立短網址", description = "根據長網址建立對應的短網址")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "短網址建立成功"),
      @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
      @ApiResponse(responseCode = "409", description = "短網址生成失敗")
  })
  public ResponseEntity<CreateUrlResponse> createShortUrl(
      @Valid @RequestBody CreateUrlRequest request,
      HttpServletRequest httpRequest) {
    log.info("Creating short URL for: {}", request.longUrl());

    CreateShortUrlCommand command = mapper.toCommand(request);
    CreateShortUrlResponse useCaseResponse = createShortUrlUseCase.execute(command);
    CreateUrlResponse response = mapper.toResponse(useCaseResponse);

    log.info("Short URL created: {} -> {}", response.shortCode(), request.longUrl());

    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Location", response.shortUrl())
        .body(response);
  }

  /**
   * 查詢 URL 資訊
   *
   * @param shortCode 短網址碼
   * @return URL 資訊
   */
  @GetMapping("/{shortCode}")
  @Operation(summary = "查詢長網址", description = "根據短網址代碼查詢對應的長網址資訊")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "查詢成功"),
      @ApiResponse(responseCode = "404", description = "短網址不存在")
  })
  public ResponseEntity<UrlInfoResponse> getUrlInfo(
      @PathVariable @Size(min = 4, max = 8, message = "短網址碼必須是 4-8 個字元") @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "短網址碼包含無效字元") String shortCode) {
    log.debug("Getting URL info for short code: {}", shortCode);

    GetLongUrlCommand command = mapper.toGetLongUrlCommand(shortCode);
    Optional<GetLongUrlResponse> useCaseResponse = getLongUrlQuery.execute(command);

    return useCaseResponse
        .map(mapper::toUrlInfoResponse)
        .map(response -> {
          log.debug("URL info found for: {}", shortCode);
          return ResponseEntity.ok(response);
        })
        .orElseGet(() -> {
          log.debug("URL info not found for: {}", shortCode);
          return ResponseEntity.notFound().build();
        });
  }
}

package com.example.tinyurl.adapters.web.controller;

import com.example.tinyurl.adapters.web.mapper.UrlWebMapper;
import com.example.tinyurl.application.dto.RedirectUrlResponse;
import com.example.tinyurl.application.port.in.RedirectUrlCommand;
import com.example.tinyurl.application.port.in.RedirectUrlQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

/**
 * 重定向控制器
 *
 * <p>
 * 處理短網址的重定向請求，提供核心的 URL 重定向功能。
 * 使用 HTTP 302 暫時重定向，避免瀏覽器永久快取。
 *
 * <p>
 * 功能包含：
 * <ul>
 * <li>短網址重定向</li>
 * <li>存取統計更新</li>
 * <li>客戶端 IP 記錄</li>
 * </ul>
 */
@RestController
@Validated
@Tag(name = "URL Redirect", description = "短網址重定向服務")
public class RedirectController {

  private static final Logger log = LoggerFactory.getLogger(RedirectController.class);

  private final RedirectUrlQuery redirectUrlQuery;
  private final UrlWebMapper mapper;

  public RedirectController(RedirectUrlQuery redirectUrlQuery, UrlWebMapper mapper) {
    this.redirectUrlQuery = redirectUrlQuery;
    this.mapper = mapper;
  }

  /**
   * 重定向到長網址
   *
   * @param shortCode 短網址碼
   * @param request   HTTP 請求
   * @param response  HTTP 回應
   * @return 重定向回應
   */
  @GetMapping("/{shortCode}")
  @Operation(summary = "重定向到長網址", description = "使用短網址代碼重定向到原始長網址")
  @ApiResponses({
      @ApiResponse(responseCode = "302", description = "重定向成功"),
      @ApiResponse(responseCode = "404", description = "短網址不存在")
  })
  public ResponseEntity<Void> redirect(
      @PathVariable @Size(min = 4, max = 8, message = "短網址碼必須是 4-8 個字元") @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "短網址碼包含無效字元") String shortCode,
      HttpServletRequest request,
      HttpServletResponse response) {
    String clientIp = getClientIpAddress(request);
    log.info("Redirect request for: {} from IP: {}", shortCode, clientIp);

    RedirectUrlCommand command = mapper.toRedirectCommand(shortCode);
    Optional<RedirectUrlResponse> redirectResponse = redirectUrlQuery.execute(command);

    if (redirectResponse.isPresent()) {
      String location = redirectResponse.get().longUrl();
      log.info("Redirecting {} to {} from IP: {}", shortCode, location, clientIp);
      return ResponseEntity.status(HttpStatus.FOUND)
          .location(URI.create(location))
          .build();
    } else {
      log.warn("Short code not found: {} from IP: {}", shortCode, clientIp);
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * 取得客戶端 IP 地址
   *
   * <p>
   * 支援代理與負載均衡器的 X-Forwarded-For 標頭。
   * 優先使用 X-Forwarded-For 中的第一個 IP。
   *
   * @param request HTTP 請求
   * @return 客戶端 IP 地址
   */
  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}

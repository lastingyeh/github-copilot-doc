package com.example.tinyurl.adapters.web.controller;

import com.example.tinyurl.AbstractIntegrationTest;
import com.example.tinyurl.adapters.web.dto.CreateUrlRequest;
import com.example.tinyurl.adapters.web.dto.CreateUrlResponse;
import com.example.tinyurl.adapters.web.dto.ErrorResponse;
import com.example.tinyurl.adapters.web.dto.UrlInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * URL 控制器整合測試
 *
 * <p>
 * 使用 Testcontainers 進行完整的整合測試，驗證：
 * <ul>
 * <li>API 端點的正確性</li>
 * <li>資料庫整合</li>
 * <li>快取整合</li>
 * <li>錯誤處理</li>
 * </ul>
 */
@DisplayName("URL 控制器整合測試")
class UrlControllerIntegrationTest extends AbstractIntegrationTest {

  @Test
  @DisplayName("應該能成功建立短網址")
  void shouldCreateShortUrl() {
    // Given
    CreateUrlRequest request = new CreateUrlRequest(
        "https://example.com/very/long/path/to/resource",
        3600L);

    // When
    ResponseEntity<CreateUrlResponse> response = restTemplate.postForEntity(
        "/api/urls", request, CreateUrlResponse.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    CreateUrlResponse body = response.getBody();
    assertThat(body.longUrl()).isEqualTo("https://example.com/very/long/path/to/resource");
    assertThat(body.shortCode()).hasSize(6);
    assertThat(body.shortUrl()).isEqualTo("http://localhost:" + port + "/" + body.shortCode());
    assertThat(body.ttlSeconds()).isEqualTo(3600L);
    assertThat(body.createdAt()).isNotNull();

    // 驗證 Location header
    String expectedLocation = "http://localhost:" + port + "/" + body.shortCode();
    assertThat(response.getHeaders().getLocation().toString()).isEqualTo(expectedLocation);
  }

  @Test
  @DisplayName("應該能查詢已建立的短網址資訊")
  void shouldGetUrlInfo() {
    // Given: 先建立一個短網址
    CreateUrlRequest createRequest = new CreateUrlRequest(
        "https://github.com/spring-projects/spring-boot",
        null);

    ResponseEntity<CreateUrlResponse> createResponse = restTemplate.postForEntity(
        "/api/urls", createRequest, CreateUrlResponse.class);

    String shortCode = createResponse.getBody().shortCode();

    // When: 查詢 URL 資訊
    ResponseEntity<UrlInfoResponse> response = restTemplate.getForEntity(
        "/api/urls/" + shortCode, UrlInfoResponse.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    UrlInfoResponse body = response.getBody();
    assertThat(body.shortCode()).isEqualTo(shortCode);
    assertThat(body.longUrl()).isEqualTo("https://github.com/spring-projects/spring-boot");
    assertThat(body.createdAt()).isNotNull();
    assertThat(body.accessCount()).isEqualTo(0);
    assertThat(body.accessedAt()).isNull();
  }

  @Test
  @DisplayName("應該能正確重定向到長網址")
  void shouldRedirectToLongUrl() {
    // Given: 先建立一個短網址
    CreateUrlRequest createRequest = new CreateUrlRequest(
        "https://www.google.com",
        null);

    ResponseEntity<CreateUrlResponse> createResponse = restTemplate.postForEntity(
        "/api/urls", createRequest, CreateUrlResponse.class);

    String shortCode = createResponse.getBody().shortCode();

    // When: 訪問短網址進行重定向
    ResponseEntity<Void> redirectResponse = restTemplate.getForEntity(
        "/" + shortCode, Void.class);

    // Then: 應該收到重定向回應
    assertThat(redirectResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    assertThat(redirectResponse.getHeaders().getLocation().toString())
        .isEqualTo("https://www.google.com");
  }

  @Test
  @DisplayName("當查詢不存在的短網址時應該返回 404")
  void shouldReturn404WhenUrlNotFound() {
    // When
    ResponseEntity<UrlInfoResponse> response = restTemplate.getForEntity(
        "/api/urls/notfound", UrlInfoResponse.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("當重定向不存在的短網址時應該返回 404")
  void shouldReturn404WhenRedirectNotFound() {
    // When
    ResponseEntity<Void> response = restTemplate.getForEntity(
        "/notfound", Void.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("當請求參數無效時應該返回驗證錯誤")
  void shouldReturnValidationErrorForInvalidRequest() {
    // Given: 無效的請求（空的長網址）
    CreateUrlRequest request = new CreateUrlRequest("", -1L);

    // When
    ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
        "/api/urls", request, ErrorResponse.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();

    ErrorResponse body = response.getBody();
    assertThat(body.code()).isEqualTo("VALIDATION_ERROR");
    assertThat(body.message()).isEqualTo("請求參數驗證失敗");
    assertThat(body.details()).isNotEmpty();
    assertThat(body.traceId()).isNotNull();
    assertThat(body.timestamp()).isNotNull();
  }

  @Test
  @DisplayName("當使用無效的短網址碼格式時應該返回驗證錯誤")
  void shouldReturnValidationErrorForInvalidShortCode() {
    // When: 使用包含無效字元的短網址碼
    ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
        "/api/urls/invalid-code!", ErrorResponse.class);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();

    ErrorResponse body = response.getBody();
    assertThat(body.code()).isEqualTo("VALIDATION_ERROR");
    assertThat(body.message()).isEqualTo("請求參數驗證失敗");
  }

  @Test
  @DisplayName("應該能處理重複的長網址建立請求")
  void shouldHandleDuplicateLongUrlCreation() {
    // Given: 同樣的長網址
    String longUrl = "https://docs.spring.io/spring-boot/docs/current/reference/html/";
    CreateUrlRequest request = new CreateUrlRequest(longUrl, null);

    // When: 建立兩次相同的長網址
    ResponseEntity<CreateUrlResponse> response1 = restTemplate.postForEntity(
        "/api/urls", request, CreateUrlResponse.class);
    ResponseEntity<CreateUrlResponse> response2 = restTemplate.postForEntity(
        "/api/urls", request, CreateUrlResponse.class);

    // Then: 兩次都應該成功，且返回相同的短網址碼
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    assertThat(response1.getBody().shortCode())
        .isEqualTo(response2.getBody().shortCode());
    assertThat(response1.getBody().longUrl())
        .isEqualTo(response2.getBody().longUrl());
  }
}

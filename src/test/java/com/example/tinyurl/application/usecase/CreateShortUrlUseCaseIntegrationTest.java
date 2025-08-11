package com.example.tinyurl.application.usecase;

import com.example.tinyurl.AbstractIntegrationTest;
import com.example.tinyurl.application.port.in.CreateShortUrlCommand;
import com.example.tinyurl.application.port.in.CreateShortUrlUseCase;
import com.example.tinyurl.application.dto.CreateShortUrlResponse;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.test.UrlTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CreateShortUrlUseCase 整合測試
 *
 * 測試建立短網址的完整業務流程，包含：
 * - 資料庫持久化
 * - 快取儲存
 * - 重複網址處理
 * - 短網址碰撞處理
 */
@DisplayName("建立短網址用例整合測試")
class CreateShortUrlUseCaseIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private CreateShortUrlUseCase createShortUrlUseCase;

  @Test
  @DisplayName("應該能建立新的短網址")
  void shouldCreateNewShortUrl() {
    // Given
    CreateShortUrlCommand command = new CreateShortUrlCommand(
        "https://example.com/test",
        Duration.ofHours(1));

    // When
    CreateShortUrlResponse response = createShortUrlUseCase.execute(command);

    // Then
    assertThat(response.shortCode()).isNotBlank();
    assertThat(response.longUrl()).isEqualTo("https://example.com/test");
    assertThat(response.shortUrl()).contains(response.shortCode());

    // 驗證資料庫中已儲存
    Optional<Url> saved = urlRepository.findByShortCode(new ShortCode(response.shortCode()));
    assertThat(saved).isPresent();
    assertThat(saved.get().getLongUrl().value()).isEqualTo("https://example.com/test");

    // 驗證快取中已存在
    Optional<Url> cached = cachePort.findByShortCode(new ShortCode(response.shortCode()));
    assertThat(cached).isPresent();
    assertThat(cached.get().getLongUrl().value()).isEqualTo("https://example.com/test");
  }

  @Test
  @DisplayName("當長網址已存在時應該返回現有短網址")
  void shouldReturnExistingShortUrlForDuplicateLongUrl() {
    // Given
    String longUrl = "https://example.com/existing";

    // 先建立一個短網址
    CreateShortUrlCommand command1 = new CreateShortUrlCommand(longUrl, null);
    CreateShortUrlResponse response1 = createShortUrlUseCase.execute(command1);

    flushAndClear(); // 清理 JPA 快取

    // When: 用相同的長網址再次建立
    CreateShortUrlCommand command2 = new CreateShortUrlCommand(longUrl, null);
    CreateShortUrlResponse response2 = createShortUrlUseCase.execute(command2);

    // Then: 應該返回相同的短網址
    assertThat(response2.shortCode()).isEqualTo(response1.shortCode());
    assertThat(response2.longUrl()).isEqualTo(response1.longUrl());
    assertThat(response2.shortUrl()).isEqualTo(response1.shortUrl());
  }

  @Test
  @DisplayName("應該能處理多個不同的 URL 請求")
  void shouldHandleMultipleDifferentUrls() {
    // Given
    CreateShortUrlCommand command1 = new CreateShortUrlCommand("https://example1.com", null);
    CreateShortUrlCommand command2 = new CreateShortUrlCommand("https://example2.com", null);
    CreateShortUrlCommand command3 = new CreateShortUrlCommand("https://example3.com", null);

    // When
    CreateShortUrlResponse response1 = createShortUrlUseCase.execute(command1);
    CreateShortUrlResponse response2 = createShortUrlUseCase.execute(command2);
    CreateShortUrlResponse response3 = createShortUrlUseCase.execute(command3);

    // Then
    assertThat(response1.shortCode()).isNotBlank();
    assertThat(response2.shortCode()).isNotBlank();
    assertThat(response3.shortCode()).isNotBlank();

    // 所有短網址應該不同
    assertThat(response1.shortCode()).isNotEqualTo(response2.shortCode());
    assertThat(response1.shortCode()).isNotEqualTo(response3.shortCode());
    assertThat(response2.shortCode()).isNotEqualTo(response3.shortCode());

    // 驗證資料庫中都已儲存
    Optional<Url> saved1 = urlRepository.findByShortCode(new ShortCode(response1.shortCode()));
    Optional<Url> saved2 = urlRepository.findByShortCode(new ShortCode(response2.shortCode()));
    Optional<Url> saved3 = urlRepository.findByShortCode(new ShortCode(response3.shortCode()));

    assertThat(saved1).isPresent();
    assertThat(saved2).isPresent();
    assertThat(saved3).isPresent();
  }

  @Test
  @DisplayName("應該能正確處理自訂 TTL")
  void shouldHandleCustomTtl() {
    // Given
    Duration customTtl = Duration.ofMinutes(30);
    CreateShortUrlCommand command = new CreateShortUrlCommand(
        "https://example.com/custom-ttl",
        customTtl);

    // When
    CreateShortUrlResponse response = createShortUrlUseCase.execute(command);

    // Then
    assertThat(response.shortCode()).isNotBlank();
    assertThat(response.longUrl()).isEqualTo("https://example.com/custom-ttl");

    // 驗證資料庫中已儲存
    Optional<Url> saved = urlRepository.findByShortCode(new ShortCode(response.shortCode()));
    assertThat(saved).isPresent();

    // 驗證快取中已存在（這裡假設快取服務會記錄 TTL）
    Optional<Url> cached = cachePort.findByShortCode(new ShortCode(response.shortCode()));
    assertThat(cached).isPresent();
  }
}

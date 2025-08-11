package com.example.tinyurl.application.usecase;

import com.example.tinyurl.application.dto.CreateShortUrlResponse;
import com.example.tinyurl.application.port.in.CreateShortUrlCommand;
import com.example.tinyurl.application.port.out.ShortCodeGenerator;
import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.exception.ShortCodeGenerationException;
import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.domain.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * CreateShortUrlUseCase 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("建立短網址 Use Case 測試")
class CreateShortUrlUseCaseTest {

  @Mock
  private UrlRepository urlRepository;

  @Mock
  private UrlCachePort cachePort;

  @Mock
  private ShortCodeGenerator shortCodeGenerator;

  @InjectMocks
  private CreateShortUrlUseCaseImpl useCase;

  private static final String BASE_URL = "http://localhost:8080";
  private static final String LONG_URL = "https://example.com";
  private static final String SHORT_CODE = "abc123";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(useCase, "baseUrl", BASE_URL);
  }

  @Test
  @DisplayName("應該成功建立新的短網址")
  void shouldCreateNewShortUrl() {
    // Given
    CreateShortUrlCommand command = new CreateShortUrlCommand(LONG_URL, null);
    ShortCode shortCode = new ShortCode(SHORT_CODE);

    when(urlRepository.findByLongUrl(any(LongUrl.class))).thenReturn(Optional.empty());
    when(shortCodeGenerator.generate()).thenReturn(shortCode);
    when(urlRepository.existsByShortCode(shortCode)).thenReturn(false);

    // When
    CreateShortUrlResponse response = useCase.execute(command);

    // Then
    assertThat(response.shortCode()).isEqualTo(SHORT_CODE);
    assertThat(response.longUrl()).isEqualTo(LONG_URL);
    assertThat(response.shortUrl()).isEqualTo(BASE_URL + "/" + SHORT_CODE);
    assertThat(response.createdAt()).isNotNull();

    verify(urlRepository).save(any(Url.class));
    verify(cachePort).cache(any(Url.class), any(Duration.class));
  }

  @Test
  @DisplayName("當長網址已存在時應該返回現有的短網址")
  void shouldReturnExistingShortUrlWhenLongUrlExists() {
    // Given
    CreateShortUrlCommand command = new CreateShortUrlCommand(LONG_URL, null);
    Url existingUrl = createTestUrl();

    when(urlRepository.findByLongUrl(any(LongUrl.class))).thenReturn(Optional.of(existingUrl));

    // When
    CreateShortUrlResponse response = useCase.execute(command);

    // Then
    assertThat(response.shortCode()).isEqualTo(existingUrl.getShortCode().value());
    assertThat(response.longUrl()).isEqualTo(existingUrl.getLongUrl().value());

    verify(urlRepository, never()).save(any());
    verify(cachePort).cache(eq(existingUrl), any(Duration.class));
  }

  @Test
  @DisplayName("當指定 TTL 時應該使用指定的 TTL")
  void shouldUseSpecifiedTtl() {
    // Given
    Duration customTtl = Duration.ofMinutes(30);
    CreateShortUrlCommand command = new CreateShortUrlCommand(LONG_URL, customTtl);
    ShortCode shortCode = new ShortCode(SHORT_CODE);

    when(urlRepository.findByLongUrl(any(LongUrl.class))).thenReturn(Optional.empty());
    when(shortCodeGenerator.generate()).thenReturn(shortCode);
    when(urlRepository.existsByShortCode(shortCode)).thenReturn(false);

    // When
    CreateShortUrlResponse response = useCase.execute(command);

    // Then
    assertThat(response.ttl()).isEqualTo(customTtl);
    verify(cachePort).cache(any(Url.class), eq(customTtl));
  }

  @Test
  @DisplayName("當碰撞發生時應該重試生成短網址碼")
  void shouldRetryWhenCollisionOccurs() {
    // Given
    CreateShortUrlCommand command = new CreateShortUrlCommand(LONG_URL, null);
    ShortCode collidingCode = new ShortCode("abc123"); // 使用有效的短網址碼
    ShortCode uniqueCode = new ShortCode("def456");

    when(urlRepository.findByLongUrl(any(LongUrl.class))).thenReturn(Optional.empty());
    when(shortCodeGenerator.generate())
        .thenReturn(collidingCode)
        .thenReturn(uniqueCode);
    when(urlRepository.existsByShortCode(collidingCode)).thenReturn(true);
    when(urlRepository.existsByShortCode(uniqueCode)).thenReturn(false);

    // When
    CreateShortUrlResponse response = useCase.execute(command);

    // Then
    assertThat(response.shortCode()).isEqualTo("def456");
    verify(shortCodeGenerator, times(2)).generate();
    verify(urlRepository).existsByShortCode(collidingCode);
    verify(urlRepository).existsByShortCode(uniqueCode);
  }

  @Test
  @DisplayName("當重試次數超過上限時應該拋出異常")
  void shouldThrowExceptionWhenMaxRetriesExceeded() {
    // Given
    CreateShortUrlCommand command = new CreateShortUrlCommand(LONG_URL, null);
    ShortCode collidingCode = new ShortCode("abc123"); // 使用有效的短網址碼

    when(urlRepository.findByLongUrl(any(LongUrl.class))).thenReturn(Optional.empty());
    when(shortCodeGenerator.generate()).thenReturn(collidingCode);
    when(urlRepository.existsByShortCode(collidingCode)).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(ShortCodeGenerationException.class)
        .hasMessage("Failed to generate unique short code after 5 attempts");

    verify(shortCodeGenerator, times(5)).generate();
    verify(urlRepository, never()).save(any());
  }

  @Test
  @DisplayName("當輸入無效的長網址時應該拋出異常")
  void shouldThrowExceptionForInvalidLongUrl() {
    // Given & When & Then
    assertThatThrownBy(() -> new CreateShortUrlCommand("invalid-url", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("網址格式不正確");
  }

  @Test
  @DisplayName("當長網址為空時應該拋出異常")
  void shouldThrowExceptionForEmptyLongUrl() {
    // Given & When & Then
    assertThatThrownBy(() -> new CreateShortUrlCommand("", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("長網址不可為空白");
  }

  /**
   * 建立測試用的 URL 實例
   */
  private Url createTestUrl() {
    LongUrl longUrl = new LongUrl(LONG_URL);
    ShortCode shortCode = new ShortCode(SHORT_CODE);
    return Url.create(longUrl, shortCode);
  }
}

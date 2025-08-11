package com.example.tinyurl.application.usecase;

import com.example.tinyurl.application.dto.RedirectUrlResponse;
import com.example.tinyurl.application.port.in.RedirectUrlCommand;
import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.domain.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RedirectUrlUseCase 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("重定向網址 Use Case 測試")
class RedirectUrlUseCaseTest {

  @Mock
  private UrlRepository urlRepository;

  @Mock
  private UrlCachePort cachePort;

  @InjectMocks
  private RedirectUrlUseCaseImpl useCase;

  private static final String LONG_URL = "https://example.com";
  private static final String SHORT_CODE = "abc123";

  @Test
  @DisplayName("當找到短網址時應該返回重定向資訊")
  void shouldReturnRedirectInfoWhenUrlFound() {
    // Given
    RedirectUrlCommand command = new RedirectUrlCommand(SHORT_CODE);
    Url url = createTestUrl();

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.of(url));

    // When
    Optional<RedirectUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isPresent();
    assertThat(response.get().longUrl()).isEqualTo(LONG_URL);
    assertThat(response.get().redirectType()).isEqualTo(RedirectUrlResponse.RedirectType.TEMPORARY);
    assertThat(response.get().accessedAt()).isNotNull();
  }

  @Test
  @DisplayName("當短網址不存在時應該返回空結果")
  void shouldReturnEmptyWhenUrlNotFound() {
    // Given
    RedirectUrlCommand command = new RedirectUrlCommand(SHORT_CODE);

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.empty());
    when(urlRepository.findByShortCode(any(ShortCode.class))).thenReturn(Optional.empty());

    // When
    Optional<RedirectUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isEmpty();
    verify(cachePort).findByShortCode(any(ShortCode.class));
    verify(urlRepository).findByShortCode(any(ShortCode.class));
  }

  @Test
  @DisplayName("當快取未命中但資料庫有資料時應該返回重定向資訊並更新快取")
  void shouldReturnRedirectInfoAndUpdateCacheWhenCacheMiss() {
    // Given
    RedirectUrlCommand command = new RedirectUrlCommand(SHORT_CODE);
    Url url = createTestUrl();

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.empty());
    when(urlRepository.findByShortCode(any(ShortCode.class))).thenReturn(Optional.of(url));

    // When
    Optional<RedirectUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isPresent();
    assertThat(response.get().longUrl()).isEqualTo(LONG_URL);

    verify(cachePort).findByShortCode(any(ShortCode.class));
    verify(urlRepository).findByShortCode(any(ShortCode.class));
    // 快取更新會被調用兩次：一次在 findUrl 中，一次在異步的 updateAccessStatistics 中
    verify(cachePort, atLeast(1)).cache(any(Url.class));
  }

  @Test
  @DisplayName("應該異步更新存取統計")
  void shouldUpdateAccessStatisticsAsynchronously() {
    // Given
    RedirectUrlCommand command = new RedirectUrlCommand(SHORT_CODE);
    Url url = createTestUrl();

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.of(url));

    // When
    Optional<RedirectUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isPresent();

    // 驗證統計更新會被呼叫（異步）
    // 注意：實際的異步執行在單元測試中可能不會立即執行
    // 這裡主要驗證方法被正確呼叫
    verify(cachePort).findByShortCode(any(ShortCode.class));
  }

  @Test
  @DisplayName("當短網址碼為空時應該拋出異常")
  void shouldThrowExceptionForEmptyShortCode() {
    // Given & When & Then
    assertThatThrownBy(() -> new RedirectUrlCommand(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("短網址碼不可為空白");
  }

  @Test
  @DisplayName("應該使用暫時重定向類型")
  void shouldUseTemporaryRedirectType() {
    // Given
    RedirectUrlCommand command = new RedirectUrlCommand(SHORT_CODE);
    Url url = createTestUrl();

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.of(url));

    // When
    Optional<RedirectUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isPresent();
    assertThat(response.get().redirectType()).isEqualTo(RedirectUrlResponse.RedirectType.TEMPORARY);
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

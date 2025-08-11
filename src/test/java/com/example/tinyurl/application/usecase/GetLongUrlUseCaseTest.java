package com.example.tinyurl.application.usecase;

import com.example.tinyurl.application.dto.GetLongUrlResponse;
import com.example.tinyurl.application.port.in.GetLongUrlCommand;
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
 * GetLongUrlUseCase 單元測試
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("取得長網址 Use Case 測試")
class GetLongUrlUseCaseTest {

  @Mock
  private UrlRepository urlRepository;

  @Mock
  private UrlCachePort cachePort;

  @InjectMocks
  private GetLongUrlUseCaseImpl useCase;

  private static final String LONG_URL = "https://example.com";
  private static final String SHORT_CODE = "abc123";

  @Test
  @DisplayName("當快取命中時應該返回快取中的資料")
  void shouldReturnCachedDataWhenCacheHit() {
    // Given
    GetLongUrlCommand command = new GetLongUrlCommand(SHORT_CODE);
    Url cachedUrl = createTestUrl();

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.of(cachedUrl));

    // When
    Optional<GetLongUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isPresent();
    assertThat(response.get().shortCode()).isEqualTo(SHORT_CODE);
    assertThat(response.get().longUrl()).isEqualTo(LONG_URL);

    verify(cachePort).findByShortCode(any(ShortCode.class));
    verify(urlRepository, never()).findByShortCode(any());
  }

  @Test
  @DisplayName("當快取未命中但資料庫有資料時應該返回資料庫中的資料並更新快取")
  void shouldReturnDatabaseDataAndUpdateCacheWhenCacheMiss() {
    // Given
    GetLongUrlCommand command = new GetLongUrlCommand(SHORT_CODE);
    Url dbUrl = createTestUrl();

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.empty());
    when(urlRepository.findByShortCode(any(ShortCode.class))).thenReturn(Optional.of(dbUrl));

    // When
    Optional<GetLongUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isPresent();
    assertThat(response.get().shortCode()).isEqualTo(SHORT_CODE);
    assertThat(response.get().longUrl()).isEqualTo(LONG_URL);

    verify(cachePort).findByShortCode(any(ShortCode.class));
    verify(urlRepository).findByShortCode(any(ShortCode.class));
    verify(cachePort).cache(dbUrl);
  }

  @Test
  @DisplayName("當快取和資料庫都沒有資料時應該返回空結果")
  void shouldReturnEmptyWhenNotFoundInCacheAndDatabase() {
    // Given
    GetLongUrlCommand command = new GetLongUrlCommand(SHORT_CODE);

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.empty());
    when(urlRepository.findByShortCode(any(ShortCode.class))).thenReturn(Optional.empty());

    // When
    Optional<GetLongUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isEmpty();

    verify(cachePort).findByShortCode(any(ShortCode.class));
    verify(urlRepository).findByShortCode(any(ShortCode.class));
    verify(cachePort, never()).cache(any());
  }

  @Test
  @DisplayName("當短網址碼為空時應該拋出異常")
  void shouldThrowExceptionForEmptyShortCode() {
    // Given & When & Then
    assertThatThrownBy(() -> new GetLongUrlCommand(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("短網址碼不可為空白");
  }

  @Test
  @DisplayName("應該正確轉換領域對象為 DTO")
  void shouldCorrectlyConvertDomainObjectToDto() {
    // Given
    GetLongUrlCommand command = new GetLongUrlCommand(SHORT_CODE);
    Url url = createTestUrl();
    url = url.recordAccess(); // 增加存取次數

    when(cachePort.findByShortCode(any(ShortCode.class))).thenReturn(Optional.of(url));

    // When
    Optional<GetLongUrlResponse> response = useCase.execute(command);

    // Then
    assertThat(response).isPresent();
    GetLongUrlResponse dto = response.get();
    assertThat(dto.shortCode()).isEqualTo(url.getShortCode().value());
    assertThat(dto.longUrl()).isEqualTo(url.getLongUrl().value());
    assertThat(dto.createdAt()).isEqualTo(url.getCreatedAt());
    assertThat(dto.accessedAt()).isEqualTo(url.getAccessedAt());
    assertThat(dto.accessCount()).isEqualTo(url.getAccessCount());
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

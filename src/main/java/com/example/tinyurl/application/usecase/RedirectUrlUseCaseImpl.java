package com.example.tinyurl.application.usecase;

import com.example.tinyurl.application.UseCase;
import com.example.tinyurl.application.dto.RedirectUrlResponse;
import com.example.tinyurl.application.port.in.RedirectUrlCommand;
import com.example.tinyurl.application.port.in.RedirectUrlQuery;
import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.domain.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 重定向網址 Use Case 實作
 *
 * <p>
 * 處理網址重定向的核心業務邏輯，包含：
 * <ul>
 * <li>目標網址查詢</li>
 * <li>存取統計更新（異步）</li>
 * <li>重定向資訊回傳</li>
 * </ul>
 *
 * <p>
 * 效能考慮：
 * <ul>
 * <li>統計更新採用異步處理，不阻塞重定向回應</li>
 * <li>優先查詢快取，提升查詢效能</li>
 * <li>使用暫時重定向 (302) 避免瀏覽器快取</li>
 * </ul>
 */
@UseCase
public class RedirectUrlUseCaseImpl implements RedirectUrlQuery {

  private final UrlRepository urlRepository;
  private final UrlCachePort cachePort;

  private static final Logger log = LoggerFactory.getLogger(RedirectUrlUseCaseImpl.class);

  public RedirectUrlUseCaseImpl(UrlRepository urlRepository, UrlCachePort cachePort) {
    this.urlRepository = urlRepository;
    this.cachePort = cachePort;
  }

  @Override
  @Transactional
  public Optional<RedirectUrlResponse> execute(RedirectUrlCommand command) {
    ShortCode shortCode = new ShortCode(command.shortCode());
    log.debug("Processing redirect for shortCode: {}", shortCode.value());

    // 1. 查詢 URL
    Optional<Url> urlOpt = findUrl(shortCode);
    if (urlOpt.isEmpty()) {
      log.debug("URL not found for shortCode: {}", shortCode.value());
      return Optional.empty();
    }

    Url url = urlOpt.get();

    // 2. 更新存取統計（異步處理以避免影響重定向效能）
    updateAccessStatistics(url);

    // 3. 返回重定向資訊
    RedirectUrlResponse response = RedirectUrlResponse.temporaryRedirect(url.getLongUrl().value());

    log.info("Redirecting {} to {}", shortCode.value(), url.getLongUrl().value());
    return Optional.of(response);
  }

  /**
   * 查詢 URL，優先使用快取
   *
   * @param shortCode 短網址碼
   * @return URL 實例，若不存在則返回空 Optional
   */
  private Optional<Url> findUrl(ShortCode shortCode) {
    // 先查快取
    Optional<Url> cached = cachePort.findByShortCode(shortCode);
    if (cached.isPresent()) {
      log.debug("Cache hit for redirect: {}", shortCode.value());
      return cached;
    }

    // 再查資料庫
    Optional<Url> fromDb = urlRepository.findByShortCode(shortCode);
    if (fromDb.isPresent()) {
      // 更新快取
      cachePort.cache(fromDb.get());
      log.debug("Cache miss for redirect, loaded from DB: {}", shortCode.value());
    }

    return fromDb;
  }

  /**
   * 異步更新存取統計
   *
   * <p>
   * 使用異步處理避免統計更新影響重定向的回應時間。
   * 即使統計更新失敗，也不會影響重定向功能。
   *
   * @param url URL 實例
   */
  @Async
  void updateAccessStatistics(Url url) {
    try {
      Url updatedUrl = url.recordAccess();
      urlRepository.save(updatedUrl);

      // 更新快取中的統計資料
      cachePort.cache(updatedUrl);

      log.debug("Updated access statistics for: {}", url.getShortCode().value());
    } catch (Exception e) {
      log.warn("Failed to update access statistics for: {}", url.getShortCode().value(), e);
    }
  }
}

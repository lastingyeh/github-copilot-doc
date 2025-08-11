package com.example.tinyurl.application.usecase;

import com.example.tinyurl.application.UseCase;
import com.example.tinyurl.application.dto.GetLongUrlResponse;
import com.example.tinyurl.application.port.in.GetLongUrlCommand;
import com.example.tinyurl.application.port.in.GetLongUrlQuery;
import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.domain.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 取得長網址 Use Case 實作
 *
 * <p>
 * 處理長網址查詢的核心業務邏輯，包含：
 * <ul>
 * <li>快取查詢優先</li>
 * <li>資料庫回退查詢</li>
 * <li>快取更新</li>
 * <li>結果轉換</li>
 * </ul>
 *
 * <p>
 * 查詢策略：
 * <ol>
 * <li>先查詢快取，如果命中直接返回</li>
 * <li>快取未命中時查詢資料庫</li>
 * <li>如果資料庫有資料，更新快取並返回</li>
 * <li>如果都沒有資料，返回空結果</li>
 * </ol>
 */
@UseCase
public class GetLongUrlUseCaseImpl implements GetLongUrlQuery {

  private final UrlRepository urlRepository;
  private final UrlCachePort cachePort;

  private static final Logger log = LoggerFactory.getLogger(GetLongUrlUseCaseImpl.class);

  public GetLongUrlUseCaseImpl(UrlRepository urlRepository, UrlCachePort cachePort) {
    this.urlRepository = urlRepository;
    this.cachePort = cachePort;
  }

  @Override
  public Optional<GetLongUrlResponse> execute(GetLongUrlCommand command) {
    ShortCode shortCode = new ShortCode(command.shortCode());
    log.debug("Querying long URL for shortCode: {}", shortCode.value());

    // 1. 先查快取
    Optional<Url> cached = cachePort.findByShortCode(shortCode);
    if (cached.isPresent()) {
      log.debug("Cache hit for shortCode: {}", shortCode.value());
      return cached.map(GetLongUrlResponse::from);
    }

    // 2. 快取未命中，查資料庫
    Optional<Url> fromDb = urlRepository.findByShortCode(shortCode);
    if (fromDb.isPresent()) {
      // 3. 更新快取
      Url url = fromDb.get();
      cachePort.cache(url);
      log.debug("Cache miss, loaded from DB and cached: {}", shortCode.value());
      return Optional.of(GetLongUrlResponse.from(url));
    }

    log.debug("URL not found for shortCode: {}", shortCode.value());
    return Optional.empty();
  }
}

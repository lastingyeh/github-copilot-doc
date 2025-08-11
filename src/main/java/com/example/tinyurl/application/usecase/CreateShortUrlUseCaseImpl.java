package com.example.tinyurl.application.usecase;

import com.example.tinyurl.application.UseCase;
import com.example.tinyurl.application.dto.CreateShortUrlResponse;
import com.example.tinyurl.application.port.in.CreateShortUrlCommand;
import com.example.tinyurl.application.port.in.CreateShortUrlUseCase;
import com.example.tinyurl.application.port.out.ShortCodeGenerator;
import com.example.tinyurl.application.port.out.UrlCachePort;
import com.example.tinyurl.domain.exception.ShortCodeGenerationException;
import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.domain.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

/**
 * 建立短網址 Use Case 實作
 *
 * <p>
 * 處理短網址建立的核心業務邏輯，包含：
 * <ul>
 * <li>長網址驗證</li>
 * <li>重複性檢查</li>
 * <li>短網址碼生成</li>
 * <li>唯一性驗證與碰撞重試</li>
 * <li>資料持久化</li>
 * <li>快取更新</li>
 * </ul>
 */
@UseCase
public class CreateShortUrlUseCaseImpl implements CreateShortUrlUseCase {

  /**
   * 碰撞重試最大次數
   */
  private static final int MAX_RETRY_ATTEMPTS = 5;

  /**
   * 預設快取存活時間
   */
  private static final Duration DEFAULT_TTL = Duration.ofHours(1);

  private static final Logger log = LoggerFactory.getLogger(CreateShortUrlUseCaseImpl.class);

  private final UrlRepository urlRepository;
  private final UrlCachePort cachePort;
  private final ShortCodeGenerator shortCodeGenerator;

  public CreateShortUrlUseCaseImpl(UrlRepository urlRepository,
      UrlCachePort cachePort,
      ShortCodeGenerator shortCodeGenerator) {
    this.urlRepository = urlRepository;
    this.cachePort = cachePort;
    this.shortCodeGenerator = shortCodeGenerator;
  }

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  @Override
  @Transactional
  public CreateShortUrlResponse execute(CreateShortUrlCommand command) {
    log.info("Creating short URL for: {}", command.longUrl());

    LongUrl longUrl = new LongUrl(command.longUrl());

    // 檢查是否已存在相同的長網址
    Optional<Url> existing = urlRepository.findByLongUrl(longUrl);
    if (existing.isPresent()) {
      log.debug("Found existing URL mapping for: {}", longUrl.value());
      Url existingUrl = existing.get();

      // 更新快取
      Duration ttl = command.ttl() != null ? command.ttl() : DEFAULT_TTL;
      cachePort.cache(existingUrl, ttl);

      return CreateShortUrlResponse.from(existingUrl, baseUrl, ttl);
    }

    // 生成唯一的短網址碼
    ShortCode shortCode = generateUniqueShortCode();

    // 建立新的 URL 映射
    Url url = Url.create(longUrl, shortCode);
    urlRepository.save(url);

    // 更新快取
    Duration ttl = command.ttl() != null ? command.ttl() : DEFAULT_TTL;
    cachePort.cache(url, ttl);

    log.info("Created short URL: {} -> {}", shortCode.value(), longUrl.value());
    return CreateShortUrlResponse.from(url, baseUrl, ttl);
  }

  /**
   * 生成唯一的短網址碼
   *
   * <p>
   * 使用重試機制處理碰撞情況，最多重試 {@link #MAX_RETRY_ATTEMPTS} 次。
   * 如果仍無法生成唯一碼，則拋出異常。
   *
   * @return 唯一的短網址碼
   * @throws ShortCodeGenerationException 當無法生成唯一短網址碼時
   */
  private ShortCode generateUniqueShortCode() {
    for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
      ShortCode candidate = shortCodeGenerator.generate();

      if (!urlRepository.existsByShortCode(candidate)) {
        log.debug("Generated unique short code: {} (attempt: {})", candidate.value(), attempt);
        return candidate;
      }

      log.debug("Short code collision detected: {}, retrying... (attempt: {}/{})",
          candidate.value(), attempt, MAX_RETRY_ATTEMPTS);
    }

    throw new ShortCodeGenerationException(
        "Failed to generate unique short code after " + MAX_RETRY_ATTEMPTS + " attempts");
  }
}

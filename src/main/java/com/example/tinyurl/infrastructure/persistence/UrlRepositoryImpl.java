package com.example.tinyurl.infrastructure.persistence;

import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.domain.repository.UrlRepository;
import com.example.tinyurl.infrastructure.persistence.jpa.UrlJpaRepository;
import com.example.tinyurl.infrastructure.persistence.mapper.UrlEntityMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * URL 資料庫存取實作
 * <p>
 * 這個類實作了領域層的 {@link UrlRepository} 介面，
 * 作為基礎設施層與領域層之間的適配器，將領域物件與 JPA 實體之間進行轉換。
 * </p>
 * <p>
 * 使用 JPA Repository 進行實際的資料庫操作，並透過 Mapper 處理物件轉換。
 * </p>
 *
 * @author TinyURL Team
 * @since 1.0.0
 * @see UrlRepository
 * @see UrlJpaRepository
 * @see UrlEntityMapper
 */
@Component
public class UrlRepositoryImpl implements UrlRepository {

  private final UrlJpaRepository jpaRepository;
  private final UrlEntityMapper mapper;

  /**
   * 建構函式
   *
   * @param jpaRepository JPA 資料庫存取物件
   * @param mapper        領域物件與實體之間的映射器
   */
  public UrlRepositoryImpl(UrlJpaRepository jpaRepository, UrlEntityMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Optional<Url> findByShortCode(ShortCode shortCode) {
    return jpaRepository.findById(shortCode.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Url> findByLongUrl(LongUrl longUrl) {
    return jpaRepository.findByLongUrlHash(hashUrl(longUrl.value()))
        .map(mapper::toDomain);
  }

  @Override
  public boolean existsByShortCode(ShortCode shortCode) {
    return jpaRepository.existsByShortCode(shortCode.value());
  }

  @Override
  public void save(Url url) {
    var entity = mapper.toEntity(url);
    jpaRepository.save(entity);
  }

  @Override
  public boolean deleteByShortCode(ShortCode shortCode) {
    if (jpaRepository.existsByShortCode(shortCode.value())) {
      jpaRepository.deleteById(shortCode.value());
      return true;
    }
    return false;
  }

  @Override
  public List<Url> findTopAccessedUrls(int limit) {
    return jpaRepository.findTopAccessedUrls(limit)
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Url> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime) {
    return jpaRepository.findByCreatedAtBetween(startTime, endTime)
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Url> findByAccessedAtAfter(LocalDateTime since) {
    // 由於沒有專門的 findByAccessedAtAfter 方法，我們使用最近建立的 URL
    return jpaRepository.findRecentUrls(100)
        .stream()
        .filter(entity -> entity.getUpdatedAt() != null && entity.getUpdatedAt().isAfter(since))
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public long countByCreatedAtAfter(LocalDateTime since) {
    return jpaRepository.countByCreatedAtAfter(since);
  }

  @Override
  public long count() {
    return jpaRepository.count();
  }

  @Override
  public long totalAccessCount() {
    return jpaRepository.getTotalAccessCount();
  }

  /**
   * 計算 URL 的雜湊值
   * <p>
   * 為了提高查詢效能，我們對 URL 進行雜湊處理並建立索引。
   * 這樣可以避免對長字串欄位進行全文檢索。
   * </p>
   *
   * @param url 要雜湊的 URL
   * @return 雜湊值（16進制字串）
   */
  private String hashUrl(String url) {
    try {
      var digest = java.security.MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(url.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      var hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 演算法不可用", e);
    }
  }
}

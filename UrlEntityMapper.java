package com.example.tinyurl.infrastructure.persistence.mapper;

import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.infrastructure.persistence.jpa.UrlEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * URL 領域模型與 JPA 實體映射器
 *
 * <p>
 * 負責 Domain Model 與 JPA Entity 之間的雙向轉換，
 * 確保領域邏輯與持久化技術的分離。
 *
 * <p>
 * 主要職責：
 * <ul>
 * <li>Domain Model → JPA Entity 轉換</li>
 * <li>JPA Entity → Domain Model 轉換</li>
 * <li>長網址雜湊值計算</li>
 * <li>資料完整性驗證</li>
 * </ul>
 *
 * <p>
 * 設計原則：
 * <ul>
 * <li>無狀態：映射器本身不保存任何狀態</li>
 * <li>純函數：相同輸入總是產生相同輸出</li>
 * <li>異常安全：處理所有可能的轉換異常</li>
 * <li>效能優化：避免不必要的物件建立</li>
 * </ul>
 */
@Component
public class UrlEntityMapper {

  private static final String HASH_ALGORITHM = "SHA-256";
  private static final Logger log = LoggerFactory.getLogger(UrlEntityMapper.class);

  /**
   * 將領域模型轉換為 JPA 實體
   *
   * @param domain Url 領域模型
   * @return UrlEntity JPA 實體
   * @throws IllegalArgumentException 當 domain 為 null 時
   */
  public UrlEntity toEntity(Url domain) {
    Objects.requireNonNull(domain, "領域模型不能為 null");

    try {
      return UrlEntity.builder()
          .shortCode(domain.getShortCode().value())
          .longUrl(domain.getLongUrl().value())
          .longUrlHash(calculateHash(domain.getLongUrl()))
          .accessCount(domain.getAccessCount())
          .build();
    } catch (Exception e) {
      log.error("轉換領域模型到實體時發生錯誤: domain={}", domain, e);
      throw new IllegalArgumentException("無法轉換領域模型到實體", e);
    }
  }

  /**
   * 將 JPA 實體轉換為領域模型
   *
   * @param entity UrlEntity JPA 實體
   * @return Url 領域模型
   * @throws IllegalArgumentException 當 entity 為 null 或資料無效時
   */
  public Url toDomain(UrlEntity entity) {
    Objects.requireNonNull(entity, "JPA 實體不能為 null");

    try {
      // 驗證必要欄位
      validateEntity(entity);

      // 建立值對象
      ShortCode shortCode = new ShortCode(entity.getShortCode());
      LongUrl longUrl = new LongUrl(entity.getLongUrl());

      // 使用 Url 的重建構子（package-private）或反射
      // 這裡使用直接建構的方式，需要在 Url 類別中添加對應的建構子
      return Url.restore(
          shortCode,
          longUrl,
          entity.getCreatedAt(),
          null, // 領域模型可能沒有 accessedAt，使用 null
          entity.getAccessCount());

    } catch (Exception e) {
      log.error("轉換實體到領域模型時發生錯誤: entity={}", entity, e);
      throw new IllegalArgumentException("無法轉換實體到領域模型", e);
    }
  }

  /**
   * 計算長網址的 SHA-256 雜湊值
   *
   * @param longUrl 長網址值對象
   * @return SHA-256 雜湊值的十六進制字符串
   */
  public String calculateHash(LongUrl longUrl) {
    Objects.requireNonNull(longUrl, "長網址不能為 null");

    try {
      MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
      byte[] hashBytes = digest.digest(longUrl.value().getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      log.error("雜湊演算法不可用: {}", HASH_ALGORITHM, e);
      throw new RuntimeException("雜湊計算失敗", e);
    }
  }

  /**
   * 驗證 JPA 實體的資料完整性
   *
   * @param entity 要驗證的實體
   * @throws IllegalArgumentException 當資料無效時
   */
  private void validateEntity(UrlEntity entity) {
    if (entity.getShortCode() == null || entity.getShortCode().trim().isEmpty()) {
      throw new IllegalArgumentException("短網址碼不能為空");
    }

    if (entity.getLongUrl() == null || entity.getLongUrl().trim().isEmpty()) {
      throw new IllegalArgumentException("長網址不能為空");
    }

    if (entity.getCreatedAt() == null) {
      throw new IllegalArgumentException("建立時間不能為空");
    }

    if (entity.getAccessCount() == null || entity.getAccessCount() < 0) {
      throw new IllegalArgumentException("存取次數必須為非負數");
    }
  }

  /**
   * 將位元組陣列轉換為十六進制字符串
   *
   * @param bytes 位元組陣列
   * @return 十六進制字符串
   */
  private String bytesToHex(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte b : bytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }
}

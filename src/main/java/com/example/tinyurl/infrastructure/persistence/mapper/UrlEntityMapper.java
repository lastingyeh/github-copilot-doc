package com.example.tinyurl.infrastructure.persistence.mapper;

import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;
import com.example.tinyurl.infrastructure.persistence.jpa.UrlEntity;
import org.springframework.stereotype.Component;

/**
 * URL 領域物件與資料庫實體間的映射器
 * <p>
 * 這個類負責在領域層的 {@link Url} 物件與基礎設施層的 {@link UrlEntity} 實體之間進行轉換。
 * 確保領域層不會因為持久化技術的改變而受到影響。
 * </p>
 * <p>
 * 映射規則：
 * <ul>
 * <li>領域物件的值物件會被轉換為原始資料型別儲存</li>
 * <li>時間欄位在兩個方向都保持 LocalDateTime 型別</li>
 * <li>存取計數等統計資料直接映射</li>
 * </ul>
 * </p>
 *
 * @author TinyURL Team
 * @since 1.0.0
 * @see Url
 * @see UrlEntity
 */
@Component
public class UrlEntityMapper {

  /**
   * 將領域物件轉換為資料庫實體
   *
   * @param url 領域層 URL 物件
   * @return 對應的資料庫實體
   * @throws IllegalArgumentException 當 url 為 null 時
   */
  public UrlEntity toEntity(Url url) {
    if (url == null) {
      throw new IllegalArgumentException("URL 不能為 null");
    }

    var entity = new UrlEntity();
    entity.setShortCode(url.getShortCode().value());
    entity.setLongUrl(url.getLongUrl().value());
    entity.setLongUrlHash(calculateHash(url.getLongUrl().value()));
    entity.setAccessCount(url.getAccessCount());
    entity.setCreatedAt(url.getCreatedAt());
    entity.setUpdatedAt(url.getAccessedAt()); // 使用 accessedAt 作為 updatedAt

    return entity;
  }

  /**
   * 將資料庫實體轉換為領域物件
   *
   * @param entity 資料庫實體
   * @return 對應的領域物件
   * @throws IllegalArgumentException 當 entity 為 null 時
   */
  public Url toDomain(UrlEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("實體不能為 null");
    }

    return Url.restore(
        ShortCode.of(entity.getShortCode()),
        LongUrl.of(entity.getLongUrl()),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getAccessCount());
  }

  /**
   * 計算 URL 的 SHA-256 雜湊值
   * <p>
   * 為了提高查詢效能，我們對長網址進行雜湊處理並建立索引。
   * 這樣可以避免對長字串欄位進行全文檢索。
   * </p>
   *
   * @param url 要雜湊的 URL
   * @return 雜湊值（64字元的16進制字串）
   */
  private String calculateHash(String url) {
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

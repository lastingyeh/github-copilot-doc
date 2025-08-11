package com.example.tinyurl.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA 實體，對應短網址資料表。
 */
@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_urls_long_url_hash", columnList = "long_url_hash"),
    @Index(name = "idx_urls_created_at", columnList = "created_at"),
    @Index(name = "idx_urls_access_count", columnList = "access_count DESC")
})
public class UrlEntity {

  @Id
  @Column(name = "short_code", length = 10)
  private String shortCode;

  @Column(name = "long_url", columnDefinition = "TEXT", nullable = false)
  private String longUrl;

  @Column(name = "long_url_hash", length = 64, nullable = false)
  private String longUrlHash;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "access_count", nullable = false)
  private Integer accessCount = 0;

  // 建構函式
  public UrlEntity() {
  }

  public UrlEntity(String shortCode, String longUrl, String longUrlHash,
      LocalDateTime createdAt, LocalDateTime updatedAt, Integer accessCount) {
    this.shortCode = shortCode;
    this.longUrl = longUrl;
    this.longUrlHash = longUrlHash;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.accessCount = accessCount;
  }

  // Getter 和 Setter 方法
  public String getShortCode() {
    return shortCode;
  }

  public void setShortCode(String shortCode) {
    this.shortCode = shortCode;
  }

  public String getLongUrl() {
    return longUrl;
  }

  public void setLongUrl(String longUrl) {
    this.longUrl = longUrl;
  }

  public String getLongUrlHash() {
    return longUrlHash;
  }

  public void setLongUrlHash(String longUrlHash) {
    this.longUrlHash = longUrlHash;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Integer getAccessCount() {
    return accessCount;
  }

  public void setAccessCount(Integer accessCount) {
    this.accessCount = accessCount;
  }

  @PrePersist
  protected void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
    if (this.accessCount == null) {
      this.accessCount = 0;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UrlEntity urlEntity = (UrlEntity) o;
    return Objects.equals(shortCode, urlEntity.shortCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shortCode);
  }

  @Override
  public String toString() {
    return "UrlEntity{" +
        "shortCode='" + shortCode + '\'' +
        ", longUrl='" + longUrl + '\'' +
        ", longUrlHash='" + longUrlHash + '\'' +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", accessCount=" + accessCount +
        '}';
  }
}

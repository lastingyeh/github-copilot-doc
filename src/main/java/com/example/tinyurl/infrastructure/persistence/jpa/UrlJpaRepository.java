package com.example.tinyurl.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * URL JPA Repository 介面
 *
 * <p>
 * 提供 UrlEntity 的資料存取操作，擴展 Spring Data JPA 的基本功能。
 * 包含自訂查詢方法，支援 TinyURL 服務的特定業務需求。
 *
 * <p>
 * 主要功能：
 * <ul>
 * <li>基本 CRUD 操作（繼承自 JpaRepository）</li>
 * <li>根據長網址雜湊查詢重複 URL</li>
 * <li>統計查詢：計數、熱門 URL</li>
 * <li>批次操作：更新存取次數</li>
 * </ul>
 *
 * @see UrlEntity
 */
@Repository
public interface UrlJpaRepository extends JpaRepository<UrlEntity, String> {

    /**
     * 根據長網址雜湊查詢 URL
     * 用於檢查是否已存在相同的長網址
     *
     * @param longUrlHash 長網址的 SHA-256 雜湊值
     * @return URL 實體，如果不存在則返回 empty
     */
    Optional<UrlEntity> findByLongUrlHash(String longUrlHash);

    /**
     * 檢查短網址碼是否已存在
     * 用於驗證短網址碼的唯一性
     *
     * @param shortCode 短網址碼
     * @return 是否已存在
     */
    boolean existsByShortCode(String shortCode);

    /**
     * 統計指定時間之後建立的 URL 數量
     * 用於統計最近的 URL 建立活動
     *
     * @param since 時間點
     * @return URL 數量
     */
    @Query("SELECT COUNT(u) FROM UrlEntity u WHERE u.createdAt > :since")
    long countByCreatedAtAfter(@Param("since") LocalDateTime since);

    /**
     * 查詢存取次數最高的 URL 列表
     * 用於分析熱門 URL 趨勢
     *
     * @param limit 返回的最大記錄數
     * @return 按存取次數降序排列的 URL 列表
     */
    @Query("SELECT u FROM UrlEntity u ORDER BY u.accessCount DESC, u.createdAt DESC LIMIT :limit")
    List<UrlEntity> findTopAccessedUrls(@Param("limit") int limit);

    /**
     * 查詢最近建立的 URL 列表
     * 用於顯示最新的 URL 映射
     *
     * @param limit 返回的最大記錄數
     * @return 按建立時間降序排列的 URL 列表
     */
    @Query("SELECT u FROM UrlEntity u ORDER BY u.createdAt DESC LIMIT :limit")
    List<UrlEntity> findRecentUrls(@Param("limit") int limit);

    /**
     * 統計總存取次數
     * 用於系統統計資訊
     *
     * @return 所有 URL 的總存取次數
     */
    @Query("SELECT COALESCE(SUM(u.accessCount), 0) FROM UrlEntity u")
    long getTotalAccessCount();

    /**
     * 統計從未被存取的 URL 數量
     * 用於分析 URL 使用情況
     *
     * @return 存取次數為 0 的 URL 數量
     */
    @Query("SELECT COUNT(u) FROM UrlEntity u WHERE u.accessCount = 0")
    long countUnusedUrls();

    /**
     * 批次更新 URL 的存取資訊
     * 提升更新效能，避免逐筆更新
     *
     * @param shortCode   短網址碼
     * @param updatedAt   更新時間
     * @param accessCount 新的存取次數
     * @return 受影響的記錄數
     */
    @Modifying
    @Query("UPDATE UrlEntity u SET u.updatedAt = :updatedAt, u.accessCount = :accessCount " +
            "WHERE u.shortCode = :shortCode")
    int updateAccessInfo(@Param("shortCode") String shortCode,
            @Param("updatedAt") LocalDateTime updatedAt,
            @Param("accessCount") Integer accessCount);

    /**
     * 查詢指定時間範圍內建立的 URL
     * 用於時間範圍統計和分析
     *
     * @param startTime 開始時間
     * @param endTime   結束時間
     * @return URL 實體列表
     */
    @Query("SELECT u FROM UrlEntity u WHERE u.createdAt BETWEEN :startTime AND :endTime " +
            "ORDER BY u.createdAt DESC")
    List<UrlEntity> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 查詢存取次數超過指定閾值的 URL
     * 用於識別高頻存取的 URL
     *
     * @param threshold 存取次數閾值
     * @return URL 實體列表
     */
    @Query("SELECT u FROM UrlEntity u WHERE u.accessCount >= :threshold " +
            "ORDER BY u.accessCount DESC")
    List<UrlEntity> findByAccessCountGreaterThanEqual(@Param("threshold") int threshold);
}

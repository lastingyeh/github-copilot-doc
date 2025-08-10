package com.example.tinyurl.domain.repository;

import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import com.example.tinyurl.domain.model.Url;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * URL 儲存庫介面
 *
 * <p>定義 URL 聚合根的持久化操作契約。
 * 此介面屬於領域層，由基礎設施層實作。
 *
 * <p>主要職責：
 * <ul>
 *   <li>提供 URL 的 CRUD 操作</li>
 *   <li>支援唯一性檢查</li>
 *   <li>提供統計查詢功能</li>
 *   <li>支援複雜的業務查詢</li>
 * </ul>
 *
 * <p>注意事項：
 * <ul>
 *   <li>介面不依賴任何框架</li>
 *   <li>只使用領域對象作為參數和返回值</li>
 *   <li>不包含事務管理邏輯</li>
 *   <li>專注於資料存取，不處理業務邏輯</li>
 * </ul>
 */
public interface UrlRepository {

    /**
     * 根據短網址碼查詢 URL
     *
     * @param shortCode 短網址碼
     * @return URL 實例，如果不存在則返回 empty
     * @throws IllegalArgumentException 當 shortCode 為 null 時
     */
    Optional<Url> findByShortCode(ShortCode shortCode);

    /**
     * 根據長網址查詢 URL
     *
     * <p>用於檢查是否已有相同的長網址被縮短過，
     * 可以避免為同一個 URL 建立多個短網址。
     *
     * @param longUrl 長網址
     * @return URL 實例，如果不存在則返回 empty
     * @throws IllegalArgumentException 當 longUrl 為 null 時
     */
    Optional<Url> findByLongUrl(LongUrl longUrl);

    /**
     * 儲存 URL
     *
     * <p>如果 URL 已存在（基於短網址碼），則更新；否則新增。
     *
     * @param url 要儲存的 URL 實例
     * @throws IllegalArgumentException 當 url 為 null 時
     */
    void save(Url url);

    /**
     * 檢查短網址碼是否已存在
     *
     * @param shortCode 要檢查的短網址碼
     * @return 是否已存在
     * @throws IllegalArgumentException 當 shortCode 為 null 時
     */
    boolean existsByShortCode(ShortCode shortCode);

    /**
     * 統計指定時間之後建立的 URL 數量
     *
     * @param since 起始時間
     * @return URL 數量
     * @throws IllegalArgumentException 當 since 為 null 時
     */
    long countByCreatedAtAfter(LocalDateTime since);

    /**
     * 查詢存取次數最多的 URL 列表
     *
     * @param limit 返回結果的最大數量
     * @return 按存取次數降序排列的 URL 列表
     * @throws IllegalArgumentException 當 limit 小於等於 0 時
     */
    List<Url> findTopAccessedUrls(int limit);

    /**
     * 查詢指定時間範圍內建立的 URL 列表
     *
     * @param startTime 開始時間（包含）
     * @param endTime 結束時間（包含）
     * @return 在指定時間範圍內建立的 URL 列表
     * @throws IllegalArgumentException 當時間參數為 null 或 startTime 晚於 endTime 時
     */
    List<Url> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查詢指定時間之後存取過的 URL 列表
     *
     * @param since 起始時間
     * @return 在指定時間之後有存取記錄的 URL 列表
     * @throws IllegalArgumentException 當 since 為 null 時
     */
    List<Url> findByAccessedAtAfter(LocalDateTime since);

    /**
     * 刪除指定的 URL
     *
     * @param shortCode 要刪除的短網址碼
     * @return 是否成功刪除（true: 存在且已刪除，false: 不存在）
     * @throws IllegalArgumentException 當 shortCode 為 null 時
     */
    boolean deleteByShortCode(ShortCode shortCode);

    /**
     * 統計總 URL 數量
     *
     * @return 總數量
     */
    long count();

    /**
     * 統計總存取次數
     *
     * @return 所有 URL 的總存取次數
     */
    long totalAccessCount();
}

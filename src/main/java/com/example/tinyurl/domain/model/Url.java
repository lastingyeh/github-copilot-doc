package com.example.tinyurl.domain.model;

import com.example.tinyurl.domain.event.DomainEvent;
import com.example.tinyurl.domain.event.UrlAccessedEvent;
import com.example.tinyurl.domain.event.UrlCreatedEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * URL 聚合根
 *
 * <p>
 * 代表短網址與長網址的映射關係，是 TinyURL 領域的核心實體。
 * 負責維護以下業務不變量：
 * <ul>
 * <li>短網址碼的唯一性</li>
 * <li>存取次數的一致性</li>
 * <li>時間戳的正確性</li>
 * <li>業務事件的觸發</li>
 * </ul>
 *
 * <p>
 * 聚合根特性：
 * <ul>
 * <li>不可變性：所有修改都返回新實例</li>
 * <li>封裝性：內部狀態只能透過業務方法修改</li>
 * <li>一致性：維護聚合內的業務規則</li>
 * <li>事件驅動：重要狀態變更觸發領域事件</li>
 * </ul>
 */
public class Url {

    private final ShortCode shortCode;
    private final LongUrl longUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime accessedAt;
    private final int accessCount;
    private final List<DomainEvent> domainEvents;

    /**
     * 私有建構子，只能透過工廠方法建立
     */
    private Url(ShortCode shortCode, LongUrl longUrl, LocalDateTime createdAt,
            LocalDateTime accessedAt, int accessCount, List<DomainEvent> domainEvents) {
        this.shortCode = Objects.requireNonNull(shortCode, "短網址碼不能為 null");
        this.longUrl = Objects.requireNonNull(longUrl, "長網址不能為 null");
        this.createdAt = Objects.requireNonNull(createdAt, "建立時間不能為 null");
        this.accessedAt = accessedAt; // 可以為 null，表示未曾被存取
        this.accessCount = Math.max(0, accessCount); // 存取次數不能為負數
        this.domainEvents = new ArrayList<>(Objects.requireNonNull(domainEvents, "領域事件列表不能為 null"));
    }

    /**
     * 建立新的 URL 映射
     *
     * @param longUrl   長網址
     * @param shortCode 短網址碼
     * @return 新的 Url 實例
     * @throws IllegalArgumentException 當參數無效時
     */
    public static Url create(LongUrl longUrl, ShortCode shortCode) {
        Objects.requireNonNull(longUrl, "長網址不能為 null");
        Objects.requireNonNull(shortCode, "短網址碼不能為 null");

        LocalDateTime now = LocalDateTime.now();
        List<DomainEvent> events = new ArrayList<>();
        events.add(UrlCreatedEvent.now(shortCode, longUrl));

        return new Url(shortCode, longUrl, now, null, 0, events);
    }

    /**
     * 重建已存在的 URL 實例（用於從持久化儲存載入）
     *
     * @param shortCode   短網址碼
     * @param longUrl     長網址
     * @param createdAt   建立時間
     * @param accessedAt  最後存取時間
     * @param accessCount 存取次數
     * @return 重建的 Url 實例
     * @throws IllegalArgumentException 當參數無效時
     */
    public static Url restore(ShortCode shortCode, LongUrl longUrl, LocalDateTime createdAt,
            LocalDateTime accessedAt, int accessCount) {
        Objects.requireNonNull(shortCode, "短網址碼不能為 null");
        Objects.requireNonNull(longUrl, "長網址不能為 null");
        Objects.requireNonNull(createdAt, "建立時間不能為 null");

        // 重建時不包含領域事件，因為這些是歷史資料
        return new Url(shortCode, longUrl, createdAt, accessedAt, accessCount, new ArrayList<>());
    }

    /**
     * 記錄 URL 存取，返回新的實例
     *
     * @return 更新存取資訊後的新 Url 實例
     */
    public Url recordAccess() {
        LocalDateTime now = LocalDateTime.now();
        int newAccessCount = accessCount + 1;
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        events.add(UrlAccessedEvent.now(shortCode, newAccessCount));

        return new Url(shortCode, longUrl, createdAt, now, newAccessCount, events);
    }

    /**
     * 檢查 URL 是否過期
     *
     * @param ttl 存活時間
     * @return 是否已過期
     */
    public boolean isExpired(Duration ttl) {
        Objects.requireNonNull(ttl, "TTL 不能為 null");
        if (ttl.isNegative() || ttl.isZero()) {
            return false; // TTL 為 0 或負數表示永不過期
        }

        return LocalDateTime.now().isAfter(createdAt.plus(ttl));
    }

    /**
     * 檢查是否曾被存取過
     *
     * @return 是否曾被存取
     */
    public boolean hasBeenAccessed() {
        return accessedAt != null && accessCount > 0;
    }

    /**
     * 計算自建立以來的存活時間
     *
     * @return 存活時間
     */
    public Duration age() {
        return Duration.between(createdAt, LocalDateTime.now());
    }

    /**
     * 計算自上次存取以來的時間
     *
     * @return 自上次存取的時間，如果未曾存取則返回自建立的時間
     */
    public Duration timeSinceLastAccess() {
        LocalDateTime reference = accessedAt != null ? accessedAt : createdAt;
        return Duration.between(reference, LocalDateTime.now());
    }

    /**
     * 清除並返回領域事件
     *
     * @return 累積的領域事件列表
     */
    public List<DomainEvent> clearDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    // Getters

    public ShortCode getShortCode() {
        return shortCode;
    }

    public LongUrl getLongUrl() {
        return longUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAccessedAt() {
        return accessedAt;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    // equals, hashCode, toString

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Url url = (Url) obj;
        return Objects.equals(shortCode, url.shortCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortCode);
    }

    @Override
    public String toString() {
        return "Url{" +
                "shortCode=" + shortCode +
                ", longUrl=" + longUrl +
                ", createdAt=" + createdAt +
                ", accessedAt=" + accessedAt +
                ", accessCount=" + accessCount +
                '}';
    }
}

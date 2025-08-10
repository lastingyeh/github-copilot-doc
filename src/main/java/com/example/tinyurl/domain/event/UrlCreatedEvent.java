package com.example.tinyurl.domain.event;

import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;

import java.time.LocalDateTime;

/**
 * URL 建立事件
 *
 * <p>當新的短網址映射被成功建立時觸發。
 * 此事件可用於：
 * <ul>
 *   <li>記錄建立日誌</li>
 *   <li>更新統計資料</li>
 *   <li>發送通知</li>
 *   <li>觸發快取預熱</li>
 * </ul>
 */
public record UrlCreatedEvent(
    ShortCode shortCode,
    LongUrl longUrl,
    LocalDateTime occurredAt
) implements DomainEvent {

    /**
     * 建立 URL 建立事件
     *
     * @param shortCode 短網址碼
     * @param longUrl 長網址
     * @return UrlCreatedEvent 實例
     */
    public static UrlCreatedEvent now(ShortCode shortCode, LongUrl longUrl) {
        return new UrlCreatedEvent(shortCode, longUrl, LocalDateTime.now());
    }
}

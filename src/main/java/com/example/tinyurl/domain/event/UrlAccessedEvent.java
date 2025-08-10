package com.example.tinyurl.domain.event;

import com.example.tinyurl.domain.model.ShortCode;

import java.time.LocalDateTime;

/**
 * URL 存取事件
 *
 * <p>當短網址被存取（重定向）時觸發。
 * 此事件可用於：
 * <ul>
 *   <li>記錄存取日誌</li>
 *   <li>更新存取統計</li>
 *   <li>分析使用模式</li>
 *   <li>觸發快取更新</li>
 * </ul>
 */
public record UrlAccessedEvent(
    ShortCode shortCode,
    LocalDateTime occurredAt,
    int totalAccessCount
) implements DomainEvent {

    /**
     * 建立 URL 存取事件
     *
     * @param shortCode 被存取的短網址碼
     * @param totalAccessCount 總存取次數
     * @return UrlAccessedEvent 實例
     */
    public static UrlAccessedEvent now(ShortCode shortCode, int totalAccessCount) {
        return new UrlAccessedEvent(shortCode, LocalDateTime.now(), totalAccessCount);
    }
}

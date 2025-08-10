package com.example.tinyurl.domain.event;

import java.time.LocalDateTime;

/**
 * 領域事件標記介面
 *
 * <p>所有領域事件都應該實作此介面。
 * 領域事件用於表示領域中發生的重要業務事件，
 * 可以觸發其他聚合的狀態變更或外部系統的處理。
 */
public interface DomainEvent {

    /**
     * 取得事件發生的時間
     *
     * @return 事件時間戳
     */
    LocalDateTime occurredAt();
}

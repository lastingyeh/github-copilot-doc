/**
 * 領域事件包 - 包含領域事件定義
 *
 * <p>包含的組件：
 * <ul>
 *   <li>領域事件介面：{@link com.example.tinyurl.domain.event.DomainEvent}</li>
 *   <li>URL 建立事件：{@link com.example.tinyurl.domain.event.UrlCreatedEvent}</li>
 *   <li>URL 存取事件：{@link com.example.tinyurl.domain.event.UrlAccessedEvent}</li>
 * </ul>
 *
 * <p>領域事件用途：
 * <ul>
 *   <li>解耦：避免聚合間的直接依賴</li>
 *   <li>審計：記錄重要的業務事件</li>
 *   <li>整合：觸發外部系統的處理</li>
 *   <li>分析：支援業務分析與統計</li>
 * </ul>
 *
 * @since 1.0
 */
package com.example.tinyurl.domain.event;

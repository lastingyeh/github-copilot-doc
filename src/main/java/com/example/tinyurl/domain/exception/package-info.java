/**
 * 領域異常包 - 包含業務異常定義
 *
 * <p>包含的組件：
 * <ul>
 *   <li>領域異常基類：{@link com.example.tinyurl.domain.exception.DomainException}</li>
 *   <li>URL 相關異常：{@link com.example.tinyurl.domain.exception.InvalidUrlException}</li>
 *   <li>短網址碼異常：{@link com.example.tinyurl.domain.exception.InvalidShortCodeException}</li>
 *   <li>業務邏輯異常：{@link com.example.tinyurl.domain.exception.ShortCodeAlreadyExistsException}</li>
 * </ul>
 *
 * <p>異常設計原則：
 * <ul>
 *   <li>明確性：清楚表達業務錯誤原因</li>
 *   <li>層次性：通過繼承建立異常層次</li>
 *   <li>資訊性：提供足夠的錯誤上下文</li>
 *   <li>可處理性：支援適當的錯誤處理策略</li>
 * </ul>
 *
 * @since 1.0
 */
package com.example.tinyurl.domain.exception;

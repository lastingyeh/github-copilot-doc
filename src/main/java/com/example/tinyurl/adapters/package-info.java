/**
 * 適配器層 - 外部介面的適配實作
 *
 * <p>職責：
 * <ul>
 *   <li>實作輸入 Port，提供外部訪問介面</li>
 *   <li>適配外部協定（HTTP、訊息佇列等）</li>
 *   <li>轉換外部資料格式到應用層 DTO</li>
 *   <li>處理技術相關的橫切關注點</li>
 * </ul>
 *
 * <p>設計原則：
 * <ul>
 *   <li>適配器模式：將外部協定適配為應用層介面</li>
 *   <li>關注點分離：技術細節與業務邏輯分離</li>
 *   <li>可替換性：不同的適配器可以互相替換</li>
 * </ul>
 *
 * <p>TinyURL 適配器：
 * <ul>
 *   <li>Web：REST API Controllers</li>
 *   <li>Messaging：事件發布與訂閱</li>
 *   <li>Scheduler：排程任務</li>
 * </ul>
 */
package com.example.tinyurl.adapters;

/**
 * 領域層 - 包含純粹的業務邏輯與領域模型
 *
 * <p>職責：
 * <ul>
 *   <li>定義領域實體、值對象與聚合根</li>
 *   <li>實作業務規則與領域邏輯</li>
 *   <li>定義 Repository 介面（不含實作）</li>
 *   <li>定義領域服務與領域異常</li>
 * </ul>
 *
 * <p>約束：
 * <ul>
 *   <li>不可依賴任何外部框架（Spring、JPA 等）</li>
 *   <li>不可依賴其他架構層</li>
 *   <li>只能使用 Java 標準庫</li>
 *   <li>所有介面定義在此層，實作在外層</li>
 * </ul>
 *
 * <p>TinyURL 領域概念：
 * <ul>
 *   <li>URL 聚合根：管理長網址與短網址的映射關係</li>
 *   <li>短網址生成策略：Base62 編碼與唯一性保證</li>
 *   <li>訪問統計：記錄點擊次數與時間</li>
 * </ul>
 *
 * @author TinyURL Team
 * @version 1.0
 * @since 1.0
 */
package com.example.tinyurl.domain;

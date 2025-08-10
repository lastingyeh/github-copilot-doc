/**
 * Repository 介面套件 - 定義資料存取的抽象介面
 *
 * <p>此套件包含所有資料存取的介面定義，遵循 Repository 模式：
 * <ul>
 *   <li>UrlRepository：URL 聚合根的資料存取介面</li>
 *   <li>提供基本的 CRUD 操作</li>
 *   <li>定義特定的查詢方法</li>
 * </ul>
 *
 * <p>設計原則：
 * <ul>
 *   <li>介面分離：只定義必要的操作</li>
 *   <li>抽象化：不依賴具體的資料存取技術</li>
 *   <li>領域驅動：以領域需求為導向設計介面</li>
 * </ul>
 */
package com.example.tinyurl.domain.repository;

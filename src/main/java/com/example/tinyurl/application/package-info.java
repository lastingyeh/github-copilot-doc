/**
 * 應用層 - 協調領域物件執行使用案例
 *
 * <p>職責：
 * <ul>
 *   <li>定義應用程式的使用案例（Use Cases）</li>
 *   <li>協調領域物件完成業務流程</li>
 *   <li>定義輸入輸出 Port 介面</li>
 *   <li>管理事務邊界</li>
 * </ul>
 *
 * <p>約束：
 * <ul>
 *   <li>只能依賴 Domain 層</li>
 *   <li>不包含業務邏輯，只負責協調</li>
 *   <li>透過 Port 介面與外部世界互動</li>
 *   <li>不依賴框架特定的註解</li>
 * </ul>
 *
 * <p>TinyURL 使用案例：
 * <ul>
 *   <li>CreateShortUrlUseCase：建立短網址</li>
 *   <li>GetLongUrlUseCase：查詢原始網址</li>
 *   <li>RedirectUseCase：處理重定向請求</li>
 * </ul>
 */
package com.example.tinyurl.application;

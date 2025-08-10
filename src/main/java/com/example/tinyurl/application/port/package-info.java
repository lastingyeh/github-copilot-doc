/**
 * Port 介面套件 - 定義應用層的輸入輸出邊界
 *
 * <p>此套件依照六角架構原則定義 Port 介面：
 * <ul>
 *   <li>in：輸入 Port，定義從外部驅動應用的介面</li>
 *   <li>out：輸出 Port，定義應用對外部依賴的抽象</li>
 * </ul>
 *
 * <p>設計目的：
 * <ul>
 *   <li>依賴反轉：應用層定義介面，外層實作</li>
 *   <li>測試友善：便於單元測試的模擬</li>
 *   <li>技術無關：不綁定特定的技術實作</li>
 * </ul>
 */
package com.example.tinyurl.application.port;

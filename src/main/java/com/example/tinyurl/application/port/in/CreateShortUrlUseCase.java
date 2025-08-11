package com.example.tinyurl.application.port.in;

import com.example.tinyurl.application.dto.CreateShortUrlResponse;

/**
 * 建立短網址 Use Case
 *
 * <p>
 * 定義建立短網址的業務操作契約。
 * 此介面屬於應用層的輸入端口，封裝了短網址建立的業務邏輯。
 *
 * <p>
 * 主要職責：
 * <ul>
 * <li>接收建立短網址的指令</li>
 * <li>執行業務邏輯驗證</li>
 * <li>協調領域服務與基礎設施</li>
 * <li>返回建立結果</li>
 * </ul>
 */
public interface CreateShortUrlUseCase {

  /**
   * 執行建立短網址操作
   *
   * @param command 建立短網址指令
   * @return 建立結果，包含短網址碼與相關資訊
   * @throws IllegalArgumentException                                          當輸入參數不正確時
   * @throws com.example.tinyurl.domain.exception.ShortCodeGenerationException 當無法生成唯一短網址時
   */
  CreateShortUrlResponse execute(CreateShortUrlCommand command);
}

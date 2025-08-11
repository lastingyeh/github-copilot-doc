package com.example.tinyurl.application.port.in;

import com.example.tinyurl.application.dto.GetLongUrlResponse;

import java.util.Optional;

/**
 * 取得長網址查詢 Use Case
 *
 * <p>
 * 定義查詢長網址的業務操作契約。
 * 此介面屬於應用層的輸入端口，封裝了長網址查詢的業務邏輯。
 *
 * <p>
 * 主要職責：
 * <ul>
 * <li>接收查詢長網址的指令</li>
 * <li>整合快取與資料庫查詢</li>
 * <li>返回查詢結果</li>
 * </ul>
 */
public interface GetLongUrlQuery {

  /**
   * 執行查詢長網址操作
   *
   * @param command 查詢指令
   * @return 查詢結果，若找不到則返回空 Optional
   */
  Optional<GetLongUrlResponse> execute(GetLongUrlCommand command);
}

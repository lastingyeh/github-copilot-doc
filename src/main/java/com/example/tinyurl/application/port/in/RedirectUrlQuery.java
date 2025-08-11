package com.example.tinyurl.application.port.in;

import com.example.tinyurl.application.dto.RedirectUrlResponse;

import java.util.Optional;

/**
 * 重定向網址查詢 Use Case
 *
 * <p>
 * 定義網址重定向的業務操作契約。
 * 此介面屬於應用層的輸入端口，封裝了重定向的業務邏輯。
 *
 * <p>
 * 主要職責：
 * <ul>
 * <li>接收重定向指令</li>
 * <li>查詢目標網址</li>
 * <li>更新存取統計</li>
 * <li>返回重定向資訊</li>
 * </ul>
 */
public interface RedirectUrlQuery {

  /**
   * 執行重定向查詢操作
   *
   * @param command 重定向指令
   * @return 重定向資訊，若找不到目標網址則返回空 Optional
   */
  Optional<RedirectUrlResponse> execute(RedirectUrlCommand command);
}

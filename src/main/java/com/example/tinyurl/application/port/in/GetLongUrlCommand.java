package com.example.tinyurl.application.port.in;

import jakarta.validation.constraints.NotBlank;

/**
 * 取得長網址指令
 *
 * <p>
 * 封裝查詢長網址所需的輸入參數。
 *
 * @param shortCode 短網址碼，不可為空
 */
public record GetLongUrlCommand(
    @NotBlank(message = "短網址碼不可為空") String shortCode) {
  public GetLongUrlCommand {
    if (shortCode != null && shortCode.trim().isEmpty()) {
      throw new IllegalArgumentException("短網址碼不可為空白");
    }
  }
}

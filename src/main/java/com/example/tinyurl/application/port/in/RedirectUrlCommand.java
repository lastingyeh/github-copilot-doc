package com.example.tinyurl.application.port.in;

import jakarta.validation.constraints.NotBlank;

/**
 * 重定向網址指令
 *
 * <p>
 * 封裝重定向操作所需的輸入參數。
 *
 * @param shortCode 短網址碼，不可為空
 */
public record RedirectUrlCommand(
    @NotBlank(message = "短網址碼不可為空") String shortCode) {
  public RedirectUrlCommand {
    if (shortCode != null && shortCode.trim().isEmpty()) {
      throw new IllegalArgumentException("短網址碼不可為空白");
    }
  }
}

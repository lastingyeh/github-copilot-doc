package com.example.tinyurl.domain.exception;

/**
 * 短網址碼生成異常
 *
 * <p>
 * 當無法生成唯一的短網址碼時拋出此異常。
 * 通常發生在多次嘗試生成後仍遇到碰撞的情況。
 */
public class ShortCodeGenerationException extends DomainException {

  /**
   * 建構子
   *
   * @param message 異常訊息
   */
  public ShortCodeGenerationException(String message) {
    super(message);
  }

  /**
   * 建構子
   *
   * @param message 異常訊息
   * @param cause   原因異常
   */
  public ShortCodeGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}

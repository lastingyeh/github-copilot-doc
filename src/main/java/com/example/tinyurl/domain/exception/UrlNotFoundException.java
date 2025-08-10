package com.example.tinyurl.domain.exception;

/**
 * URL 未找到異常
 *
 * <p>當查詢的短網址碼對應的 URL 不存在時拋出。
 */
public class UrlNotFoundException extends DomainException {

    public UrlNotFoundException(String shortCode) {
        super("找不到短網址: " + shortCode);
    }
}

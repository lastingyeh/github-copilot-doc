package com.example.tinyurl.domain.exception;

/**
 * 無效短網址碼異常
 *
 * <p>當短網址碼不符合格式要求時拋出，例如：
 * <ul>
 *   <li>包含非法字符（非 Base62 字符）</li>
 *   <li>長度不在允許範圍內</li>
 *   <li>為空或 null</li>
 * </ul>
 */
public class InvalidShortCodeException extends DomainException {

    public InvalidShortCodeException(String shortCode) {
        super("無效的短網址碼: " + shortCode);
    }

    public InvalidShortCodeException(String shortCode, String reason) {
        super("無效的短網址碼: " + shortCode + ", 原因: " + reason);
    }
}

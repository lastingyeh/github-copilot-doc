package com.example.tinyurl.domain.exception;

/**
 * 短網址碼已存在異常
 *
 * <p>當嘗試建立的短網址碼已經被使用時拋出。
 * 這種情況可能發生在隨機生成短網址碼時的碰撞。
 */
public class ShortCodeAlreadyExistsException extends DomainException {

    public ShortCodeAlreadyExistsException(String shortCode) {
        super("短網址碼已存在: " + shortCode);
    }
}

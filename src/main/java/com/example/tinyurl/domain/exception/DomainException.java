package com.example.tinyurl.domain.exception;

/**
 * 領域異常基類
 *
 * <p>所有領域層的異常都應該繼承此類，用於與技術異常區分。
 * 領域異常表示業務規則違反或領域不變量破壞。
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

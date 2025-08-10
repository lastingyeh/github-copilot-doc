package com.example.tinyurl.domain.exception;

/**
 * 無效 URL 異常
 *
 * <p>當提供的 URL 格式不正確、不支援的協議或長度超限時拋出。
 */
public class InvalidUrlException extends DomainException {

    public InvalidUrlException(String url) {
        super("無效的 URL: " + url);
    }

    public InvalidUrlException(String url, String reason) {
        super("無效的 URL: " + url + ", 原因: " + reason);
    }
}

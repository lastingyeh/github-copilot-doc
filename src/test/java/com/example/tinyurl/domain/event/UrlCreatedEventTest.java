package com.example.tinyurl.domain.event;

import com.example.tinyurl.domain.model.LongUrl;
import com.example.tinyurl.domain.model.ShortCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UrlCreatedEvent 測試")
class UrlCreatedEventTest {

    @Test
    @DisplayName("應該正確建立 URL 建立事件")
    void shouldCreateUrlCreatedEvent() {
        // Given
        ShortCode shortCode = new ShortCode("abc123");
        LongUrl longUrl = new LongUrl("https://example.com");
        LocalDateTime now = LocalDateTime.now();

        // When
        UrlCreatedEvent event = new UrlCreatedEvent(shortCode, longUrl, now);

        // Then
        assertThat(event.shortCode()).isEqualTo(shortCode);
        assertThat(event.longUrl()).isEqualTo(longUrl);
        assertThat(event.occurredAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("靜態工廠方法應該使用當前時間")
    void shouldUseCurrentTimeInStaticFactoryMethod() {
        // Given
        ShortCode shortCode = new ShortCode("abc123");
        LongUrl longUrl = new LongUrl("https://example.com");
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        UrlCreatedEvent event = UrlCreatedEvent.now(shortCode, longUrl);

        // Then
        LocalDateTime afterCreation = LocalDateTime.now();
        assertThat(event.occurredAt()).isBetween(beforeCreation, afterCreation);
        assertThat(event.shortCode()).isEqualTo(shortCode);
        assertThat(event.longUrl()).isEqualTo(longUrl);
    }

    @Test
    @DisplayName("應該正確比較相等性")
    void shouldCompareEquality() {
        // Given
        ShortCode shortCode = new ShortCode("abc123");
        LongUrl longUrl = new LongUrl("https://example.com");
        LocalDateTime time = LocalDateTime.now();

        UrlCreatedEvent event1 = new UrlCreatedEvent(shortCode, longUrl, time);
        UrlCreatedEvent event2 = new UrlCreatedEvent(shortCode, longUrl, time);
        UrlCreatedEvent event3 = new UrlCreatedEvent(new ShortCode("def456"), longUrl, time);

        // When & Then
        assertThat(event1).isEqualTo(event2);
        assertThat(event1).isNotEqualTo(event3);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("toString 應該包含主要資訊")
    void shouldIncludeMainInfoInToString() {
        // Given
        ShortCode shortCode = new ShortCode("abc123");
        LongUrl longUrl = new LongUrl("https://example.com");
        LocalDateTime now = LocalDateTime.now();
        UrlCreatedEvent event = new UrlCreatedEvent(shortCode, longUrl, now);

        // When
        String toString = event.toString();

        // Then
        assertThat(toString).contains("abc123");
        assertThat(toString).contains("https://example.com");
    }
}

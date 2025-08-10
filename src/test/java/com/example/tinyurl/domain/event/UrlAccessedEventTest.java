package com.example.tinyurl.domain.event;

import com.example.tinyurl.domain.model.ShortCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UrlAccessedEvent 測試")
class UrlAccessedEventTest {

    @Test
    @DisplayName("應該正確建立 URL 存取事件")
    void shouldCreateUrlAccessedEvent() {
        // Given
        ShortCode shortCode = new ShortCode("abc123");
        LocalDateTime now = LocalDateTime.now();
        int accessCount = 5;

        // When
        UrlAccessedEvent event = new UrlAccessedEvent(shortCode, now, accessCount);

        // Then
        assertThat(event.shortCode()).isEqualTo(shortCode);
        assertThat(event.occurredAt()).isEqualTo(now);
        assertThat(event.totalAccessCount()).isEqualTo(accessCount);
    }

    @Test
    @DisplayName("靜態工廠方法應該使用當前時間")
    void shouldUseCurrentTimeInStaticFactoryMethod() {
        // Given
        ShortCode shortCode = new ShortCode("abc123");
        int accessCount = 3;
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        UrlAccessedEvent event = UrlAccessedEvent.now(shortCode, accessCount);

        // Then
        LocalDateTime afterCreation = LocalDateTime.now();
        assertThat(event.occurredAt()).isBetween(beforeCreation, afterCreation);
        assertThat(event.shortCode()).isEqualTo(shortCode);
        assertThat(event.totalAccessCount()).isEqualTo(accessCount);
    }

    @Test
    @DisplayName("應該正確比較相等性")
    void shouldCompareEquality() {
        // Given
        ShortCode shortCode = new ShortCode("abc123");
        LocalDateTime time = LocalDateTime.now();
        int accessCount = 2;

        UrlAccessedEvent event1 = new UrlAccessedEvent(shortCode, time, accessCount);
        UrlAccessedEvent event2 = new UrlAccessedEvent(shortCode, time, accessCount);
        UrlAccessedEvent event3 = new UrlAccessedEvent(shortCode, time, accessCount + 1);

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
        LocalDateTime now = LocalDateTime.now();
        int accessCount = 7;
        UrlAccessedEvent event = new UrlAccessedEvent(shortCode, now, accessCount);

        // When
        String toString = event.toString();

        // Then
        assertThat(toString).contains("abc123");
        assertThat(toString).contains("7");
    }
}

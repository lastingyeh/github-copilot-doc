package com.example.tinyurl.domain.model;

import com.example.tinyurl.domain.event.DomainEvent;
import com.example.tinyurl.domain.event.UrlAccessedEvent;
import com.example.tinyurl.domain.event.UrlCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Url 聚合根測試")
class UrlTest {

    private final LongUrl testLongUrl = new LongUrl("https://example.com");
    private final ShortCode testShortCode = new ShortCode("abc123");

    @Test
    @DisplayName("應該成功建立新的 URL 映射")
    void shouldCreateNewUrlMapping() {
        // When
        Url url = Url.create(testLongUrl, testShortCode);

        // Then
        assertThat(url.getShortCode()).isEqualTo(testShortCode);
        assertThat(url.getLongUrl()).isEqualTo(testLongUrl);
        assertThat(url.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(url.getAccessedAt()).isNull();
        assertThat(url.getAccessCount()).isZero();
        assertThat(url.hasBeenAccessed()).isFalse();
    }

    @Test
    @DisplayName("建立 URL 時應該觸發 UrlCreatedEvent")
    void shouldTriggerUrlCreatedEventWhenCreated() {
        // When
        Url url = Url.create(testLongUrl, testShortCode);

        // Then
        List<DomainEvent> events = url.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(UrlCreatedEvent.class);

        UrlCreatedEvent event = (UrlCreatedEvent) events.get(0);
        assertThat(event.shortCode()).isEqualTo(testShortCode);
        assertThat(event.longUrl()).isEqualTo(testLongUrl);
        assertThat(event.occurredAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("應該拒絕 null 參數")
    void shouldRejectNullParameters() {
        // When & Then
        assertThatThrownBy(() -> Url.create(null, testShortCode))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("長網址不能為 null");

        assertThatThrownBy(() -> Url.create(testLongUrl, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("短網址碼不能為 null");
    }

    @Test
    @DisplayName("記錄存取應該更新存取資訊")
    void shouldUpdateAccessInfoWhenRecordingAccess() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);
        LocalDateTime beforeAccess = LocalDateTime.now();

        // When
        Url accessedUrl = url.recordAccess();

        // Then
        assertThat(accessedUrl.getAccessCount()).isEqualTo(1);
        assertThat(accessedUrl.getAccessedAt()).isAfterOrEqualTo(beforeAccess);
        assertThat(accessedUrl.hasBeenAccessed()).isTrue();

        // 原始 URL 應該保持不變
        assertThat(url.getAccessCount()).isZero();
        assertThat(url.getAccessedAt()).isNull();
    }

    @Test
    @DisplayName("記錄存取應該觸發 UrlAccessedEvent")
    void shouldTriggerUrlAccessedEventWhenAccessed() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);

        // When
        Url accessedUrl = url.recordAccess();

        // Then
        List<DomainEvent> events = accessedUrl.getDomainEvents();
        assertThat(events).hasSize(2); // UrlCreatedEvent + UrlAccessedEvent
        assertThat(events.get(1)).isInstanceOf(UrlAccessedEvent.class);

        UrlAccessedEvent event = (UrlAccessedEvent) events.get(1);
        assertThat(event.shortCode()).isEqualTo(testShortCode);
        assertThat(event.totalAccessCount()).isEqualTo(1);
        assertThat(event.occurredAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("多次存取應該累積計數")
    void shouldAccumulateAccessCount() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);

        // When
        Url accessed1 = url.recordAccess();
        Url accessed2 = accessed1.recordAccess();
        Url accessed3 = accessed2.recordAccess();

        // Then
        assertThat(accessed3.getAccessCount()).isEqualTo(3);
        assertThat(accessed3.hasBeenAccessed()).isTrue();
    }

    @Test
    @DisplayName("應該正確判斷 URL 是否過期")
    void shouldCorrectlyDetermineIfExpired() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);

        // When & Then
        assertThat(url.isExpired(Duration.ofHours(1))).isFalse();

        // 使用非常小的時間間隔，並加入短暫延遲確保過期
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertThat(url.isExpired(Duration.ofMillis(1))).isTrue();

        assertThat(url.isExpired(Duration.ZERO)).isFalse(); // 永不過期
        assertThat(url.isExpired(Duration.ofMillis(-1))).isFalse(); // 永不過期
    }

    @Test
    @DisplayName("過期檢查應該拒絕 null TTL")
    void shouldRejectNullTtl() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);

        // When & Then
        assertThatThrownBy(() -> url.isExpired(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("TTL 不能為 null");
    }

    @Test
    @DisplayName("應該正確計算存活時間")
    void shouldCalculateAgeCorrectly() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);

        // When
        Duration age = url.age();

        // Then
        assertThat(age.isNegative()).isFalse();
        assertThat(age.toMillis()).isLessThan(1000); // 應該在 1 秒內
    }

    @Test
    @DisplayName("應該正確計算自上次存取的時間")
    void shouldCalculateTimeSinceLastAccessCorrectly() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);

        // When - 未存取過
        Duration timeSinceLastAccess1 = url.timeSinceLastAccess();

        // Then - 應該返回自建立的時間
        assertThat(timeSinceLastAccess1.isNegative()).isFalse();

        // When - 存取後
        Url accessedUrl = url.recordAccess();
        Duration timeSinceLastAccess2 = accessedUrl.timeSinceLastAccess();

        // Then - 應該返回自上次存取的時間
        assertThat(timeSinceLastAccess2.isNegative()).isFalse();
        assertThat(timeSinceLastAccess2.toMillis()).isLessThan(1000);
    }

    @Test
    @DisplayName("應該正確比較相等性")
    void shouldCompareEquality() {
        // Given
        Url url1 = Url.create(testLongUrl, testShortCode);
        Url url2 = Url.create(new LongUrl("https://other.com"), testShortCode);
        Url url3 = Url.create(testLongUrl, new ShortCode("def456"));

        // When & Then
        assertThat(url1).isEqualTo(url2); // 基於短網址碼比較
        assertThat(url1).isNotEqualTo(url3); // 短網址碼不同
        assertThat(url1.hashCode()).isEqualTo(url2.hashCode());
    }

    @Test
    @DisplayName("清除領域事件應該返回所有事件並清空列表")
    void shouldClearDomainEventsAndReturnThem() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);
        Url accessedUrl = url.recordAccess();

        // When
        List<DomainEvent> events = accessedUrl.clearDomainEvents();

        // Then
        assertThat(events).hasSize(2);
        assertThat(accessedUrl.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("toString 應該包含主要資訊")
    void shouldIncludeMainInfoInToString() {
        // Given
        Url url = Url.create(testLongUrl, testShortCode);

        // When
        String toString = url.toString();

        // Then
        assertThat(toString).contains("abc123");
        assertThat(toString).contains("https://example.com");
        assertThat(toString).contains("accessCount=0");
    }
}

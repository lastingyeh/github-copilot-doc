package com.example.tinyurl.domain.model;

import com.example.tinyurl.domain.exception.InvalidUrlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("LongUrl 值對象測試")
class LongUrlTest {

    @Test
    @DisplayName("應該成功建立有效的 HTTP URL")
    void shouldCreateValidHttpUrl() {
        // Given
        String validUrl = "http://example.com";

        // When
        LongUrl longUrl = new LongUrl(validUrl);

        // Then
        assertThat(longUrl.value()).isEqualTo(validUrl);
        assertThat(longUrl.getProtocol()).isEqualTo("http");
        assertThat(longUrl.getHost()).isEqualTo("example.com");
        assertThat(longUrl.isSecure()).isFalse();
    }

    @Test
    @DisplayName("應該成功建立有效的 HTTPS URL")
    void shouldCreateValidHttpsUrl() {
        // Given
        String validUrl = "https://secure.example.com/path?param=value";

        // When
        LongUrl longUrl = new LongUrl(validUrl);

        // Then
        assertThat(longUrl.value()).isEqualTo(validUrl);
        assertThat(longUrl.getProtocol()).isEqualTo("https");
        assertThat(longUrl.getHost()).isEqualTo("secure.example.com");
        assertThat(longUrl.isSecure()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://example.com",
        "https://example.com",
        "https://sub.example.com/path",
        "http://example.com:8080/path?param=value",
        "https://127.0.0.1:3000/test"
    })
    @DisplayName("應該接受有效的 HTTP/HTTPS URL")
    void shouldAcceptValidUrls(String validUrl) {
        // When & Then
        assertThatCode(() -> new LongUrl(validUrl))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "   ",
        "not-a-url",
        "ftp://example.com",
        "file:///path/to/file",
        "javascript:alert('xss')",
        "http://",
        "https://",
        "http:// ",
        "https:// "
    })
    @DisplayName("應該拒絕無效的 URL 格式")
    void shouldRejectInvalidUrls(String invalidUrl) {
        // When & Then
        assertThatThrownBy(() -> new LongUrl(invalidUrl))
            .isInstanceOf(InvalidUrlException.class)
            .hasMessageContaining("必須是有效的 HTTP/HTTPS URL");
    }

    @Test
    @DisplayName("應該拒絕 null 值")
    void shouldRejectNullValue() {
        // When & Then
        assertThatThrownBy(() -> new LongUrl(null))
            .isInstanceOf(InvalidUrlException.class);
    }

    @Test
    @DisplayName("應該拒絕超長的 URL")
    void shouldRejectTooLongUrl() {
        // Given
        String tooLongUrl = "https://example.com/" + "a".repeat(2050);

        // When & Then
        assertThatThrownBy(() -> new LongUrl(tooLongUrl))
            .isInstanceOf(InvalidUrlException.class)
            .hasMessageContaining("長度不超過 2048 字符");
    }

    @Test
    @DisplayName("應該正確比較相等性")
    void shouldCompareEquality() {
        // Given
        LongUrl url1 = new LongUrl("https://example.com");
        LongUrl url2 = new LongUrl("https://example.com");
        LongUrl url3 = new LongUrl("https://other.com");

        // When & Then
        assertThat(url1).isEqualTo(url2);
        assertThat(url1).isNotEqualTo(url3);
        assertThat(url1.hashCode()).isEqualTo(url2.hashCode());
    }

    @Test
    @DisplayName("應該正確轉換為字符串")
    void shouldConvertToString() {
        // Given
        String value = "https://example.com";
        LongUrl longUrl = new LongUrl(value);

        // When & Then
        assertThat(longUrl.toString()).isEqualTo(value);
    }

    @Test
    @DisplayName("靜態工廠方法應該正常工作")
    void shouldWorkWithStaticFactoryMethod() {
        // Given
        String value = "https://test.example.com";

        // When
        LongUrl longUrl = LongUrl.of(value);

        // Then
        assertThat(longUrl.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("應該正確解析主機名稱")
    void shouldParseHostCorrectly() {
        // Given
        LongUrl longUrl = new LongUrl("https://sub.example.com:8080/path");

        // When & Then
        assertThat(longUrl.getHost()).isEqualTo("sub.example.com");
    }

    @Test
    @DisplayName("應該正確解析協議")
    void shouldParseProtocolCorrectly() {
        // Given
        LongUrl httpUrl = new LongUrl("HTTP://EXAMPLE.COM");
        LongUrl httpsUrl = new LongUrl("HTTPS://EXAMPLE.COM");

        // When & Then
        assertThat(httpUrl.getProtocol()).isEqualTo("http");
        assertThat(httpsUrl.getProtocol()).isEqualTo("https");
    }

    @Test
    @DisplayName("應該正確判斷是否為安全協議")
    void shouldIdentifySecureProtocol() {
        // Given
        LongUrl httpUrl = new LongUrl("http://example.com");
        LongUrl httpsUrl = new LongUrl("https://example.com");

        // When & Then
        assertThat(httpUrl.isSecure()).isFalse();
        assertThat(httpsUrl.isSecure()).isTrue();
    }

    @Test
    @DisplayName("應該正確返回長度")
    void shouldReturnCorrectLength() {
        // Given
        String url = "https://example.com";
        LongUrl longUrl = new LongUrl(url);

        // When & Then
        assertThat(longUrl.length()).isEqualTo(url.length());
    }

    @Test
    @DisplayName("應該處理帶有空白字符的 URL")
    void shouldHandleUrlWithWhitespace() {
        // Given
        String urlWithWhitespace = "  https://example.com  ";

        // When
        LongUrl longUrl = new LongUrl(urlWithWhitespace);

        // Then
        assertThat(longUrl.value()).isEqualTo(urlWithWhitespace); // 保持原始輸入
        assertThat(longUrl.getHost()).isEqualTo("example.com"); // 但解析時會去除空白
    }
}

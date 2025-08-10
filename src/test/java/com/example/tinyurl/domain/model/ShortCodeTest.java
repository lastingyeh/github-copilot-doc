package com.example.tinyurl.domain.model;

import com.example.tinyurl.domain.exception.InvalidShortCodeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ShortCode 值對象測試")
class ShortCodeTest {

    @Test
    @DisplayName("應該成功建立有效的短網址碼")
    void shouldCreateValidShortCode() {
        // Given
        String validCode = "abc123";

        // When
        ShortCode shortCode = new ShortCode(validCode);

        // Then
        assertThat(shortCode.value()).isEqualTo(validCode);
        assertThat(shortCode.length()).isEqualTo(6);
        assertThat(shortCode.isBase62()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc123", "ABC123", "Ab12Cd", "abcdefgh", "123456"})
    @DisplayName("應該接受有效的 Base62 格式短網址碼")
    void shouldAcceptValidBase62Codes(String validCode) {
        // When & Then
        assertThatCode(() -> new ShortCode(validCode))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "     ", "abc12", "abcdefghi", "abc-123", "abc 123", "abc@123"})
    @DisplayName("應該拒絕無效格式的短網址碼")
    void shouldRejectInvalidCodes(String invalidCode) {
        // When & Then
        assertThatThrownBy(() -> new ShortCode(invalidCode))
            .isInstanceOf(InvalidShortCodeException.class)
            .hasMessageContaining("必須是 6-8 位 Base62 字符");
    }

    @Test
    @DisplayName("應該拒絕 null 值")
    void shouldRejectNullValue() {
        // When & Then
        assertThatThrownBy(() -> new ShortCode(null))
            .isInstanceOf(InvalidShortCodeException.class);
    }

    @Test
    @DisplayName("應該正確比較相等性")
    void shouldCompareEquality() {
        // Given
        ShortCode code1 = new ShortCode("abc123");
        ShortCode code2 = new ShortCode("abc123");
        ShortCode code3 = new ShortCode("def456");

        // When & Then
        assertThat(code1).isEqualTo(code2);
        assertThat(code1).isNotEqualTo(code3);
        assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
    }

    @Test
    @DisplayName("應該正確轉換為字符串")
    void shouldConvertToString() {
        // Given
        String value = "abc123";
        ShortCode shortCode = new ShortCode(value);

        // When & Then
        assertThat(shortCode.toString()).isEqualTo(value);
    }

    @Test
    @DisplayName("靜態工廠方法應該正常工作")
    void shouldWorkWithStaticFactoryMethod() {
        // Given
        String value = "test123";

        // When
        ShortCode shortCode = ShortCode.of(value);

        // Then
        assertThat(shortCode.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("應該正確驗證 Base62 字符集")
    void shouldValidateBase62Charset() {
        // Given
        ShortCode validCode = new ShortCode("aB3cD7");

        // When & Then
        assertThat(validCode.isBase62()).isTrue();
    }

    @Test
    @DisplayName("應該正確返回長度")
    void shouldReturnCorrectLength() {
        // Given
        ShortCode shortCode6 = new ShortCode("abc123");
        ShortCode shortCode8 = new ShortCode("abcd1234");

        // When & Then
        assertThat(shortCode6.length()).isEqualTo(6);
        assertThat(shortCode8.length()).isEqualTo(8);
    }
}

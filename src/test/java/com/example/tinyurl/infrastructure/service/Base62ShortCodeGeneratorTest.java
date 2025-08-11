package com.example.tinyurl.infrastructure.service;

import com.example.tinyurl.domain.model.ShortCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Base62ShortCodeGenerator 單元測試
 */
@DisplayName("Base62 短網址碼生成器測試")
class Base62ShortCodeGeneratorTest {

  private Base62ShortCodeGenerator generator;

  @BeforeEach
  void setUp() {
    generator = new Base62ShortCodeGenerator();
  }

  @Test
  @DisplayName("應該生成預設長度的短網址碼")
  void shouldGenerateDefaultLengthShortCode() {
    // When
    ShortCode shortCode = generator.generate();

    // Then
    assertThat(shortCode.value()).hasSize(6); // 預設長度為 6
    assertThat(shortCode.value()).matches("[0-9A-Za-z]+"); // 只包含 Base62 字元
  }

  @Test
  @DisplayName("應該生成指定長度的短網址碼")
  void shouldGenerateSpecifiedLengthShortCode() {
    // Given
    int[] lengths = { 6, 7, 8 }; // 修正為符合領域規則的長度範圍

    for (int length : lengths) {
      // When
      ShortCode shortCode = generator.generate(length);

      // Then
      assertThat(shortCode.value()).hasSize(length);
      assertThat(shortCode.value()).matches("[0-9A-Za-z]+");
    }
  }

  @Test
  @DisplayName("當長度小於最小值時應該拋出異常")
  void shouldThrowExceptionWhenLengthTooSmall() {
    // Given & When & Then
    assertThatThrownBy(() -> generator.generate(5))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("短網址碼長度必須在 6-8 之間，實際: 5");
  }

  @Test
  @DisplayName("當長度大於最大值時應該拋出異常")
  void shouldThrowExceptionWhenLengthTooLarge() {
    // Given & When & Then
    assertThatThrownBy(() -> generator.generate(9))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("短網址碼長度必須在 6-8 之間，實際: 9");
  }

  @Test
  @DisplayName("應該生成指定數量的短網址碼")
  void shouldGenerateBatchOfShortCodes() {
    // Given
    int count = 10;

    // When
    List<ShortCode> shortCodes = generator.generateBatch(count);

    // Then
    assertThat(shortCodes).hasSize(count);
    shortCodes.forEach(code -> {
      assertThat(code.value()).hasSize(6); // 預設長度
      assertThat(code.value()).matches("[0-9A-Za-z]+");
    });
  }

  @Test
  @DisplayName("當批次數量小於等於 0 時應該拋出異常")
  void shouldThrowExceptionWhenBatchCountInvalid() {
    // Given & When & Then
    assertThatThrownBy(() -> generator.generateBatch(0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("批次生成數量必須大於 0");

    assertThatThrownBy(() -> generator.generateBatch(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("批次生成數量必須大於 0");
  }

  @Test
  @DisplayName("應該生成唯一的短網址碼")
  void shouldGenerateUniqueShortCodes() {
    // Given
    int count = 1000;
    Set<String> generatedCodes = new HashSet<>();

    // When
    for (int i = 0; i < count; i++) {
      ShortCode shortCode = generator.generate();
      generatedCodes.add(shortCode.value());
    }

    // Then
    // 由於是隨機生成，不能保證 100% 唯一，但應該有很高的唯一性
    // 以 6 位 Base62 碼來說，總共有 62^6 = 56,800,235,584 種組合
    // 生成 1000 個碼出現重複的機率極低
    assertThat(generatedCodes.size()).isCloseTo(count, withinPercentage(95));
  }

  @Test
  @DisplayName("應該只包含 Base62 字元")
  void shouldContainOnlyBase62Characters() {
    // Given
    String base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // When
    for (int i = 0; i < 100; i++) {
      ShortCode shortCode = generator.generate();

      // Then
      for (char c : shortCode.value().toCharArray()) {
        assertThat(base62Chars).contains(String.valueOf(c));
      }
    }
  }

  @Test
  @DisplayName("批次生成的短網址碼應該具有合理的唯一性")
  void shouldHaveReasonableUniquenessInBatch() {
    // Given
    int batchSize = 100;

    // When
    List<ShortCode> batch = generator.generateBatch(batchSize);
    Set<String> uniqueCodes = new HashSet<>();
    batch.forEach(code -> uniqueCodes.add(code.value()));

    // Then
    // 批次生成的短網址碼應該有很高的唯一性
    assertThat(uniqueCodes.size()).isCloseTo(batchSize, withinPercentage(90));
  }
}

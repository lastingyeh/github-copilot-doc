package com.example.tinyurl.infrastructure.service;

import com.example.tinyurl.application.port.out.ShortCodeGenerator;
import com.example.tinyurl.domain.model.ShortCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Base62 短網址碼生成器
 *
 * <p>
 * 使用 Base62 編碼生成短網址碼。
 * Base62 使用數字 (0-9)、大寫字母 (A-Z) 和小寫字母 (a-z)，
 * 總共 62 個字元，避免了容易混淆的字元組合。
 *
 * <p>
 * 特性：
 * <ul>
 * <li>使用 SecureRandom 確保隨機性</li>
 * <li>支援 4-8 位長度的短網址碼</li>
 * <li>預設長度為 6 位</li>
 * <li>支援批次生成</li>
 * </ul>
 */
@Component
public class Base62ShortCodeGenerator implements ShortCodeGenerator {

  /**
   * Base62 字元集
   * 包含數字、大寫字母、小寫字母，總共 62 個字元
   */
  private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  /**
   * 預設短網址碼長度
   */
  private static final int DEFAULT_LENGTH = 6;

  /**
   * 最小長度
   */
  private static final int MIN_LENGTH = 6;

  /**
   * 最大長度
   */
  private static final int MAX_LENGTH = 8;

  /**
   * 安全隨機數生成器
   */
  private static final SecureRandom RANDOM = new SecureRandom();

  private static final Logger log = LoggerFactory.getLogger(Base62ShortCodeGenerator.class);

  @Override
  public ShortCode generate() {
    return generate(DEFAULT_LENGTH);
  }

  @Override
  public ShortCode generate(int length) {
    validateLength(length);

    StringBuilder code = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = RANDOM.nextInt(BASE62_CHARS.length());
      code.append(BASE62_CHARS.charAt(randomIndex));
    }

    String generatedCode = code.toString();
    log.debug("Generated short code: {} (length: {})", generatedCode, length);

    return new ShortCode(generatedCode);
  }

  @Override
  public List<ShortCode> generateBatch(int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("批次生成數量必須大於 0");
    }

    log.debug("Generating batch of {} short codes", count);

    return IntStream.range(0, count)
        .mapToObj(i -> generate())
        .collect(Collectors.toList());
  }

  /**
   * 驗證短網址碼長度
   *
   * @param length 長度
   * @throws IllegalArgumentException 當長度不在有效範圍內時
   */
  private void validateLength(int length) {
    if (length < MIN_LENGTH || length > MAX_LENGTH) {
      throw new IllegalArgumentException(
          String.format("短網址碼長度必須在 %d-%d 之間，實際: %d",
              MIN_LENGTH, MAX_LENGTH, length));
    }
  }
}

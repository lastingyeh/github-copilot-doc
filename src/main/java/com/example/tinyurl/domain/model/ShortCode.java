package com.example.tinyurl.domain.model;

import com.example.tinyurl.domain.exception.InvalidShortCodeException;

import java.util.regex.Pattern;

/**
 * 短網址碼值對象
 *
 * <p>包裝短網址的字符串值，提供驗證邏輯與類型安全。
 * 短網址碼必須符合以下規則：
 * <ul>
 *   <li>只能包含 Base62 字符（a-z, A-Z, 0-9）</li>
 *   <li>長度必須在 6-8 個字符之間</li>
 *   <li>不能為空或 null</li>
 * </ul>
 *
 * <p>值對象特性：
 * <ul>
 *   <li>不可變性：建立後無法修改</li>
 *   <li>等值性：基於值而非參照比較</li>
 *   <li>自我驗證：建立時即驗證有效性</li>
 * </ul>
 */
public record ShortCode(String value) {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,8}$");
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 8;

    public ShortCode {
        if (!isValid(value)) {
            throw new InvalidShortCodeException(value, "必須是 6-8 位 Base62 字符");
        }
    }

    /**
     * 驗證短網址碼格式是否有效
     *
     * @param value 要驗證的字符串
     * @return 是否為有效的短網址碼
     */
    private static boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        return value.length() >= MIN_LENGTH &&
               value.length() <= MAX_LENGTH &&
               VALID_PATTERN.matcher(value).matches();
    }

    /**
     * 創建短網址碼實例的靜態工廠方法
     *
     * @param value 短網址碼字符串
     * @return ShortCode 實例
     * @throws InvalidShortCodeException 當格式無效時
     */
    public static ShortCode of(String value) {
        return new ShortCode(value);
    }

    /**
     * 檢查是否為 Base62 字符集
     *
     * @return 是否符合 Base62 編碼
     */
    public boolean isBase62() {
        return VALID_PATTERN.matcher(value).matches();
    }

    /**
     * 取得字符串長度
     *
     * @return 短網址碼長度
     */
    public int length() {
        return value.length();
    }

    @Override
    public String toString() {
        return value;
    }
}

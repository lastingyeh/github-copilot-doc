package com.example.tinyurl.domain.model;

import com.example.tinyurl.domain.exception.InvalidUrlException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * 長網址值對象
 *
 * <p>包裝原始 URL 字符串，提供驗證邏輯與類型安全。
 * 長網址必須符合以下規則：
 * <ul>
 *   <li>必須是有效的 URL 格式</li>
 *   <li>只支援 HTTP 與 HTTPS 協議</li>
 *   <li>長度不能超過 2048 字符</li>
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
public record LongUrl(String value) {

    private static final int MAX_LENGTH = 2048;
    private static final Set<String> SUPPORTED_PROTOCOLS = Set.of("http", "https");

    public LongUrl {
        if (!isValid(value)) {
            throw new InvalidUrlException(value, "必須是有效的 HTTP/HTTPS URL，且長度不超過 " + MAX_LENGTH + " 字符");
        }
    }

    /**
     * 驗證 URL 格式是否有效
     *
     * @param value 要驗證的 URL 字符串
     * @return 是否為有效的 URL
     */
    private static boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        if (value.length() > MAX_LENGTH) {
            return false;
        }

        try {
            URL url = new URL(value.trim());
            String protocol = url.getProtocol().toLowerCase();
            return SUPPORTED_PROTOCOLS.contains(protocol) &&
                   url.getHost() != null &&
                   !url.getHost().trim().isEmpty();
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 創建長網址實例的靜態工廠方法
     *
     * @param value URL 字符串
     * @return LongUrl 實例
     * @throws InvalidUrlException 當格式無效時
     */
    public static LongUrl of(String value) {
        return new LongUrl(value);
    }

    /**
     * 取得 URL 的主機名稱
     *
     * @return 主機名稱
     */
    public String getHost() {
        try {
            URL url = new URL(value);
            return url.getHost();
        } catch (MalformedURLException e) {
            // 這不應該發生，因為建構時已驗證
            throw new IllegalStateException("內部錯誤：已驗證的 URL 解析失敗", e);
        }
    }

    /**
     * 取得 URL 的協議
     *
     * @return 協議（http 或 https）
     */
    public String getProtocol() {
        try {
            URL url = new URL(value);
            return url.getProtocol().toLowerCase();
        } catch (MalformedURLException e) {
            // 這不應該發生，因為建構時已驗證
            throw new IllegalStateException("內部錯誤：已驗證的 URL 解析失敗", e);
        }
    }

    /**
     * 檢查是否為 HTTPS URL
     *
     * @return 是否使用 HTTPS 協議
     */
    public boolean isSecure() {
        return "https".equals(getProtocol());
    }

    /**
     * 取得字符串長度
     *
     * @return URL 長度
     */
    public int length() {
        return value.length();
    }

    @Override
    public String toString() {
        return value;
    }
}

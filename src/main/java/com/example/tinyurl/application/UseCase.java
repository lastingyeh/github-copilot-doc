package com.example.tinyurl.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use Case 註解
 *
 * <p>
 * 標記 Use Case 實作類別的組合註解。
 * 結合了 Spring 的 {@code @Service} 與 {@code @Transactional} 註解，
 * 確保 Use Case 被 Spring 容器管理且具備事務支援。
 *
 * <p>
 * 使用此註解的類別應該：
 * <ul>
 * <li>實作特定的 Use Case 介面</li>
 * <li>遵循單一職責原則</li>
 * <li>無狀態設計</li>
 * <li>依賴注入外部服務</li>
 * </ul>
 *
 * <p>
 * 範例使用方式：
 * 
 * <pre>
 * {@code
 * @UseCase
 * public class CreateShortUrlUseCaseImpl implements CreateShortUrlUseCase {
 *   // 實作內容
 * }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
@Transactional
public @interface UseCase {
}

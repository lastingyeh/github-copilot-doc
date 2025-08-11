package com.example.tinyurl.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring 應用程式配置類
 * <p>
 * 這個配置類負責啟用 JPA 資料庫存取功能。
 * 其他配置由 @SpringBootApplication 自動處理。
 * </p>
 *
 * @author TinyURL Team
 * @since 1.0.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.tinyurl.infrastructure.persistence.jpa")
public class ApplicationConfig {
    // 配置類無需額外方法，註解已處理所有配置
}

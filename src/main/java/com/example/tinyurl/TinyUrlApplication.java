package com.example.tinyurl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TinyURL API 應用程式的主要入口點
 * <p>
 * 這個短網址服務採用 Clean Architecture 架構模式，
 * 提供 URL 縮短與重定向功能，並整合 Redis 快取與 PostgreSQL 持久化儲存。
 * </p>
 *
 * @author TinyURL Team
 * @since 1.0.0
 */
@SpringBootApplication
public class TinyUrlApplication {

  /**
   * 應用程式主要進入點
   *
   * @param args 命令列參數
   */
  public static void main(String[] args) {
    SpringApplication.run(TinyUrlApplication.class, args);
  }
}

package com.example.tinyurl.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 快取配置屬性
 *
 * 統一管理快取相關的配置參數
 * 支援從 application.yml 讀取自訂設定
 */
@Configuration
@ConfigurationProperties(prefix = "tinyurl.cache")
@Getter
@Setter
public class CacheProperties {

  /**
   * 預設快取過期時間
   */
  private Duration defaultTtl = Duration.ofHours(1);

  /**
   * 熱門 URL 快取過期時間
   */
  private Duration popularTtl = Duration.ofHours(24);

  /**
   * 判定為熱門 URL 的存取次數閾值
   */
  private int popularThreshold = 100;

  /**
   * 快取鍵值前綴
   */
  private String keyPrefix = "tinyurl:short:";

  /**
   * 統計鍵值前綴
   */
  private String statsPrefix = "tinyurl:stats:";

  /**
   * 是否啟用快取統計
   */
  private boolean enableStatistics = true;

  /**
   * 批量操作的最大數量
   */
  private int batchSize = 1000;

  /**
   * 健康檢查的超時時間
   */
  private Duration healthCheckTimeout = Duration.ofSeconds(2);
}

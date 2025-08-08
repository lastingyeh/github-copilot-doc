package com.example.tinyurl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * TinyURL 應用程式整合測試
 * <p>
 * 驗證 Spring Boot 應用程式能夠正常啟動，
 * 並且基本的 Spring 容器配置正確。
 * </p>
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TinyURL 應用程式測試")
class TinyUrlApplicationTest {

  @Test
  @DisplayName("應用程式上下文載入成功")
  void contextLoads() {
    // 這個測試驗證 Spring Boot 應用程式能夠正常啟動
    // 如果應用程式上下文載入失敗，測試會拋出例外
  }

  @Test
  @DisplayName("應用程式主方法可正常執行")
  void mainMethodExecutes() {
    // 驗證主方法可以被調用而不拋出例外
    // 注意：這裡不實際啟動應用程式以避免端口衝突
    TinyUrlApplication.main(new String[] {
        "--spring.main.web-environment=false",
        "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
    });
  }
}

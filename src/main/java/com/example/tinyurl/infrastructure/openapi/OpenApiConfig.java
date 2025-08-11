package com.example.tinyurl.infrastructure.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 配置類
 *
 * <p>
 * 配置 SpringDoc OpenAPI 的設定，包含：
 * <ul>
 * <li>API 基本資訊</li>
 * <li>伺服器清單</li>
 * <li>共用元件定義</li>
 * <li>錯誤回應 Schema</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  @Value("${spring.application.name:TinyURL API}")
  private String applicationName;

  /**
   * 配置 OpenAPI 規格
   *
   * @return OpenAPI 配置
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(createApiInfo())
        .servers(createServerList())
        .components(createComponents());
  }

  /**
   * 建立 API 基本資訊
   *
   * @return API 資訊
   */
  private Info createApiInfo() {
    return new Info()
        .title("TinyURL API")
        .description("""
            # TinyURL 短網址服務 API

            提供完整的短網址管理功能，包含：

            ## 核心功能
            - **建立短網址**：將長網址轉換為短網址
            - **重定向服務**：透過短網址重定向到原始網址
            - **資訊查詢**：查詢短網址的詳細資訊與統計

            ## 技術特性
            - Base62 編碼的短網址碼
            - Redis 快取加速
            - 存取統計追蹤
            - RESTful API 設計

            ## 使用範例
            1. 建立短網址：`POST /api/urls`
            2. 使用重定向：`GET /{shortCode}`
            3. 查詢資訊：`GET /api/urls/{shortCode}`
            """)
        .version("1.0.0")
        .contact(new Contact()
            .name("TinyURL 開發團隊")
            .email("dev@tinyurl.example.com")
            .url("https://github.com/example/tinyurl"));
  }

  /**
   * 建立伺服器清單
   *
   * @return 伺服器清單
   */
  private List<Server> createServerList() {
    return List.of(
        new Server()
            .url(baseUrl)
            .description("本地開發環境"),
        new Server()
            .url("https://api.tinyurl.example.com")
            .description("生產環境"),
        new Server()
            .url("https://staging-api.tinyurl.example.com")
            .description("測試環境"));
  }

  /**
   * 建立共用元件
   *
   * @return 元件定義
   */
  private Components createComponents() {
    return new Components()
        .addSchemas("ErrorResponse", createErrorResponseSchema())
        .addSchemas("ValidationError", createValidationErrorSchema());
  }

  /**
   * 建立錯誤回應 Schema
   *
   * @return 錯誤回應 Schema
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Schema<?> createErrorResponseSchema() {
    Schema schema = new Schema();
    schema.type("object");
    schema.description("統一錯誤回應格式");
    schema.addProperty("code", new StringSchema()
        .description("錯誤代碼")
        .example("VALIDATION_ERROR"));
    schema.addProperty("message", new StringSchema()
        .description("錯誤訊息")
        .example("請求參數驗證失敗"));
    schema.addProperty("details", new ArraySchema()
        .items(new StringSchema())
        .description("錯誤詳情列表")
        .example(List.of("longUrl: 長網址不可為空", "ttlSeconds: TTL 秒數必須為正數")));
    schema.addProperty("trace_id", new StringSchema()
        .description("請求追蹤 ID")
        .example("req-abc12345"));
    schema.addProperty("timestamp", new StringSchema()
        .format("date-time")
        .description("錯誤發生時間")
        .example("2023-08-11T10:30:00"));
    schema.required(List.of("code", "message", "details", "trace_id", "timestamp"));
    return schema;
  }

  /**
   * 建立驗證錯誤 Schema
   *
   * @return 驗證錯誤 Schema
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Schema<?> createValidationErrorSchema() {
    Schema schema = new Schema();
    schema.type("object");
    schema.description("參數驗證錯誤");
    schema.addProperty("field", new StringSchema()
        .description("驗證失敗的欄位名稱")
        .example("longUrl"));
    schema.addProperty("message", new StringSchema()
        .description("驗證錯誤訊息")
        .example("長網址不可為空"));
    schema.required(List.of("field", "message"));
    return schema;
  }
}

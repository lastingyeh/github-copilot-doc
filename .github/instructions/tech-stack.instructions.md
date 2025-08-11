---
applyTo: '**'
---
# 技術棧規範

> **重要**：所有輸出一律使用**繁體中文**，包含程式碼註解和文件。

## 必選技術棧（Required）

### 核心框架
- **Java 版本**：17 LTS（明確在 `pom.xml` 中指定）
- **Spring Boot**：3.x 最新穩定版
- **建置工具**：Maven + Maven Wrapper (`./mvnw`)

### 資料層
- **主要資料庫**：PostgreSQL
- **資料庫版控**：Liquibase（changelog 檔案）
- **資料存取**：Spring Data JPA

### 日誌與監控
- **日誌框架**：SLF4J API + Logback 實作
- **日誌格式**：支援 console 和 JSON 兩種模式
- **應用監控**：Spring Boot Actuator + Micrometer
- **健康檢查**：`/actuator/health` + 自訂 HealthIndicator

### 測試策略
- **單元測試**：JUnit 5 + Mockito
- **整合測試**：Testcontainers (PostgreSQL + Redis)

### API 文件
- **OpenAPI 規範**：SpringDoc (`springdoc-openapi-starter-webmvc-ui`)
- **Swagger UI**：自動產生並可存取

### 容器化
- **容器**：Docker + Docker Compose
- **語法**：使用新版 `docker compose` 指令

## 可選技術棧 (Optional)

### 訊息處理
- **訊息佇列**：RabbitMQ / Apache Kafka / GCP Pub/Sub

### 額外儲存
- **文件資料庫**：MongoDB（當需要文件型資料時）
- **快取系統**：Redis（Session 儲存、查詢快取、分散式鎖）

### 進階監控
- **指標收集**：Prometheus + Grafana
- **鏈路追蹤**：OpenTelemetry Collector
- **日誌聚合**：EFK Stack / Vector

## 環境組態規範

### 必須支援的環境變數
以下所有組態項目必須可透過環境變數覆蓋：
```yaml
# 資料庫連線
DB_HOST: localhost
DB_PORT: 5432
DB_NAME: myapp
DB_USERNAME: postgres
DB_PASSWORD: password

# Redis 連線
REDIS_HOST: localhost
REDIS_PORT: 6379
REDIS_PASSWORD: ""

# 日誌組態
LOG_FORMAT: console  # console | json

# 管理埠口
MANAGEMENT_PORT: 8081  # 可選

# Prometheus 整合
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: health,info,prometheus
```

## 效能與最佳實務

### 資料存取
- **查詢分頁**：預設實作分頁查詢，避免大量資料回傳
- **投影查詢**：使用 DTO 投影，僅選取必要欄位
- **索引策略**：為常用查詢條件建立合適索引

### 快取策略
- **Redis TTL**：為所有快取設定合理的過期時間
- **快取鍵設計**：使用有意義且唯一的鍵名規範
- **失效策略**：實作快取失效機制

### 資料庫優化
- **批次操作**：使用批次寫入提升效能
- **約束設計**：合理使用外鍵約束
- **避免 N+1**：使用適當的 fetch 策略


# TinyURL API 服務開發指引

這是一個短網址服務的實驗室項目，採用 **Clean Architecture** 與 **Spring Boot 3.x**，實現 URL 縮短與重定向功能。

## 專案架構概覽

### 領域概念

- **核心功能**：長網址 → 短網址生成；短網址 → 長網址檢索與重定向
- **技術棧**：Java 17 + Spring Boot 3.x + PostgreSQL + Redis + Docker
- **架構模式**：Clean Architecture 四層分離 (`domain/application/adapters/infrastructure`)

### 目錄結構 (Clean Architecture)

```
src/main/java/com/example/tinyurl/
├── domain/          # 純領域模型（無框架依賴）
│   ├── model/       # URL 聚合根、值對象
│   └── repository/  # 介面定義
├── application/     # Use Cases 與 Ports
│   ├── usecase/     # CreateShortUrl, GetLongUrl, RedirectUrl
│   └── port/        # 輸入/輸出端口介面
├── adapters/        # 介面層
│   └── web/         # REST Controller + Web DTO
└── infrastructure/  # 技術實作
    ├── persistence/ # JPA Entity + Repository 實作
    └── cache/       # Redis 快取實作
```

## 開發工作流程

### 環境設置

- **必須使用 Maven Wrapper**：`./mvnw` 而非 `mvn`
- **Docker 優先**：透過 `docker-compose.yml` 啟動完整環境
- **預設端口**：App 8080, PostgreSQL 5432, Redis 6379, Prometheus 9090

### 核心 Use Cases (優先實作順序)

1. **CreateShortUrlUseCase**：接收長網址，生成唯一短網址
2. **GetLongUrlUseCase**：根據短網址查詢原始網址（含 Redis 快取）
3. **RedirectUseCase**：處理瀏覽器重定向請求

### 測試策略

- **Testcontainers**：PostgreSQL + Redis 整合測試
- **測試命名**：使用 `@DisplayName` 描述測試情境
- **覆蓋範圍**：Positive/Negative 案例，重點測試 URL 生成唯一性

## 專案特定約定

### URL 生成策略

- **短網址格式**：Base62 編碼 (a-z, A-Z, 0-9)，長度 6-8 字符
- **碰撞處理**：資料庫唯一約束 + 重試機制
- **過期機制**：可選的 TTL 支持

### 快取設計

- **Redis Key 模式**：`tinyurl:short:{shortCode}` → 長網址
- **TTL 策略**：熱門網址長期快取，一般網址 1 小時
- **Cache-aside Pattern**：查詢時檢查快取，未命中則查 DB 並更新快取

### API 設計

```
POST /api/urls          # 創建短網址
GET  /api/urls/{code}   # 獲取長網址（JSON 回應）
GET  /{code}            # 直接重定向（HTTP 302）
```

### 資料庫設計重點

- **主鍵**：短網址碼作為自然主鍵
- **索引**：長網址 hash 索引（支持重複檢測）
- **審計欄位**：created_at, accessed_at, access_count

## 監控與運維

### 觀測性端點

- `/actuator/health`：健康檢查（包含 DB/Redis 連通性）
- `/actuator/prometheus`：業務指標（URL 生成率、命中率、回應時間）
- `/actuator/info`：應用版本與建置資訊

### 自訂指標

- `tinyurl_urls_created_total`：創建的短網址數量
- `tinyurl_cache_hit_ratio`：Redis 快取命中率
- `tinyurl_redirect_duration_seconds`：重定向延遲分布

## 常見問題解決

### 開發時 Docker 問題

```bash
# 重置並重新建置環境
docker-compose down -v
docker-compose up --build
```

### Testcontainers 設定

- 確保 Docker Desktop 執行中
- 使用 `@Testcontainers` + `@Container` 註解
- 測試資料庫與 Redis 會自動清理

### 效能最佳化

- **批次查詢**：使用 `@Query` 批次載入多個 URL
- **連接池**：HikariCP 預設配置，監控連接使用率
- **讀寫分離**：如需要，使用 `@Transactional(readOnly = true)`

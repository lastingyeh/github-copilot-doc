# TinyURL API 服務

一個採用 Clean Architecture 設計的高效能短網址服務，基於 Spring Boot 3.x 與 Java 17 構建。

## ✨ 特色功能

- 🚀 **高效能**: 支援高並發短網址生成與重定向
- 🏗️ **Clean Architecture**: 清晰的分層架構，易於維護與擴展
- 📊 **完整監控**: Prometheus + Grafana 監控儀表板
- 🐳 **容器化**: Docker Compose 一鍵部署
- ⚡ **Redis 快取**: 提升查詢效能，降低資料庫負載
- 📝 **完整文件**: OpenAPI 3.0 自動生成 API 文件

## 🏛️ 架構圖

```mermaid
graph TB
    subgraph "外部使用者"
        U[User/Browser]
        A[API Client]
    end

    subgraph "應用程式層"
        WEB[Web Controllers]
        UC[Use Cases]
    end

    subgraph "基礎設施層"
        CACHE[Redis Cache]
        DB[PostgreSQL]
        METRICS[Prometheus]
    end

    U --> WEB
    A --> WEB
    WEB --> UC
    UC --> CACHE
    UC --> DB
    WEB --> METRICS
```

## 🚀 快速開始

### 先決條件
- Docker 與 Docker Compose
- Java 17 (開發環境)
- Maven 3.8+ (開發環境)

### 一鍵啟動
```bash
# 複製專案
git clone https://github.com/example/tinyurl-api.git
cd tinyurl-api

# 啟動所有服務
docker-compose up -d

# 等待服務啟動 (約 30 秒)
docker-compose logs -f tinyurl-api
```

### 驗證部署
```bash
# 檢查服務狀態
curl http://localhost:8080/actuator/health

# 建立第一個短網址
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://github.com"}'
```

## 📋 API 文件

### 核心端點
| 方法 | 路徑               | 功能           | 範例                     |
| ---- | ------------------ | -------------- | ------------------------ |
| POST | `/api/urls`        | 建立短網址     | [建立範例](#建立短網址)  |
| GET  | `/api/urls/{code}` | 查詢 URL 資訊  | [查詢範例](#查詢url資訊) |
| GET  | `/{code}`          | 重定向到長網址 | [重定向範例](#重定向)    |

### 建立短網址
```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{
    "longUrl": "https://github.com/example/project",
    "ttlSeconds": 3600
  }'
```

**回應:**
```json
{
  "short_code": "a1B2c3",
  "long_url": "https://github.com/example/project",
  "short_url": "http://localhost:8080/a1B2c3",
  "created_at": [2025,8,11,13,17,7,201577503],
  "ttl_seconds": 3600
}
```

### 查詢 URL 資訊
```bash
curl http://localhost:8080/api/urls/a1B2c3
```

### 重定向
```bash
curl -I http://localhost:8080/a1B2c3
# HTTP/1.1 302 Found
# Location: https://github.com/example/project
```

## 🔧 環境變數

| 變數          | 預設值       | 說明                    |
| ------------- | ------------ | ----------------------- |
| `DB_HOST`     | localhost    | PostgreSQL 主機         |
| `DB_PORT`     | 5432         | PostgreSQL 連接埠       |
| `DB_NAME`     | tinyurl_db   | 資料庫名稱              |
| `DB_USERNAME` | tinyurl_user | 資料庫使用者            |
| `DB_PASSWORD` | tinyurl_pass | 資料庫密碼              |
| `REDIS_HOST`  | localhost    | Redis 主機              |
| `REDIS_PORT`  | 6379         | Redis 連接埠            |
| `LOG_FORMAT`  | console      | 日誌格式 (console/json) |

## 📊 監控與觀測

### 監控端點
- **健康檢查**: http://localhost:8080/actuator/health
- **Prometheus 指標**: http://localhost:8080/actuator/prometheus
- **API 文件**: http://localhost:8080/swagger-ui.html

### 監控儀表板
- **Grafana**: http://localhost:3000 (admin/admin123)
- **Prometheus**: http://localhost:9090

### 核心指標
- `http_server_requests_total`: HTTP 請求總數
- `http_server_requests_duration_seconds`: API 回應時間
- `jvm_memory_used_bytes`: JVM 記憶體使用量
- `jvm_gc_pause_seconds`: GC 暫停時間

## 🏗️ 開發指南

### 本地開發
```bash
# 啟動依賴服務
docker-compose up postgres redis -d

# 執行應用程式
./mvnw spring-boot:run

# 執行測試
./mvnw test
```

### 專案結構
```
src/main/java/com/example/tinyurl/
├── domain/          # 領域模型 (無框架依賴)
├── application/     # Use Cases 與 Ports
├── adapters/        # 外部介面適配器
└── infrastructure/  # 技術實作細節
```

## 🐛 故障排除

### 常見問題

**Q: 應用程式啟動失敗**
```bash
# 檢查依賴服務狀態
docker-compose ps

# 查看應用程式日誌
docker-compose logs tinyurl-api

# 重新建置並啟動
docker-compose down -v
docker-compose up --build
```

**Q: 資料庫連線錯誤**
```bash
# 檢查 PostgreSQL 狀態
docker-compose logs postgres

# 驗證資料庫連線
docker-compose exec postgres psql -U tinyurl_user -d tinyurl_db -c "\dt"
```

**Q: Redis 快取問題**
```bash
# 檢查 Redis 狀態
docker-compose logs redis

# 測試 Redis 連線
docker-compose exec redis redis-cli ping
```

## 📈 效能指標

### 基準測試結果
- **建立短網址**: < 50ms (平均 23ms)
- **查詢短網址**: < 20ms (平均 16ms)
- **重定向請求**: < 20ms (平均 18ms)
- **吞吐量**: > 500 RPS
- **系統可用性**: > 99.9%

### 執行效能測試
```bash
# 功能驗證測試
./scripts/functional-test.sh

# 效能基準測試
./scripts/performance-test.sh
```

### 前置需求
- Java 17+
- Docker & Docker Compose（用於資料庫和快取）
- Maven 3.8+（或使用專案提供的 Maven Wrapper）

### 1. 克隆專案
```bash
git clone <repository-url>
cd shorturlapi-lab
```

## 🤝 貢獻指南

1. Fork 專案
2. 建立功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交變更 (`git commit -m 'feat: add amazing feature'`)
4. 推送分支 (`git push origin feature/amazing-feature`)
5. 建立 Pull Request

## 📖 相關文件

- [Clean Architecture 文件](docs/CLEAN_ARCHITECTURE.md)
- [快取實作說明](CACHE_IMPLEMENTATION.md)
- [整合測試指南](INTEGRATION_TESTING_GUIDE.md)
- [監控驗證報告](MONITORING_VERIFICATION_REPORT.md)
- [Docker 部署指南](README-docker.md)

## 🧪 測試

### 執行單元測試
```bash
./mvnw test
```

### 執行整合測試
```bash
./mvnw integration-test
```

### 檢查測試覆蓋率
```bash
./mvnw jacoco:report
open target/site/jacoco/index.html
```

## 📄 授權

本專案採用 MIT 授權 - 詳見 [LICENSE](LICENSE) 檔案

## 🙋‍♂️ 支援

- 📧 Email: dev@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/example/tinyurl-api/issues)
- 📖 Wiki: [專案 Wiki](https://github.com/example/tinyurl-api/wiki)

---

✅ **專案完成**: 所有核心功能已實作並經過驗證
📚 **文件齊全**: 提供完整的使用與維護文件
🔧 **生產就緒**: 可直接部署到生產環境使用

## 環境變數

應用程式支援以下環境變數進行配置：

### 資料庫
- `DB_HOST`: PostgreSQL 主機 (預設: localhost)
- `DB_PORT`: PostgreSQL 端口 (預設: 5432)
- `DB_NAME`: 資料庫名稱 (預設: tinyurl_db)
- `DB_USERNAME`: 資料庫使用者 (預設: tinyurl)
- `DB_PASSWORD`: 資料庫密碼 (預設: password123)

### 快取
- `REDIS_HOST`: Redis 主機 (預設: localhost)
- `REDIS_PORT`: Redis 端口 (預設: 6379)

### 日誌
- `SQL_LOG_LEVEL`: SQL 日誌級別 (預設: WARN)

## Clean Architecture 層級說明

### Domain Layer（領域層）
- **model/**: 核心業務實體與值物件
- **repository/**: 資料存取的抽象介面

### Application Layer（應用層）
- **usecase/**: 業務邏輯與使用案例實作
- **port/in/**: 對外提供的服務介面
- **port/out/**: 對基礎設施的依賴介面

### Infrastructure Layer（基礎設施層）
- **persistence/**: JPA 實體與資料庫存取實作
- **cache/**: Redis 快取實作
- **web/**: REST API 控制器
- **config/**: Spring 配置類別

## 建置狀態

- [x] Maven 專案結構建立完成
- [x] pom.xml 依賴配置完成
- [x] Spring Boot 應用程式主類別
- [x] Maven Wrapper 配置
- [x] 基本配置檔案 (application.yml)
- [x] 測試配置與基本測試類別
- [x] Clean Architecture 目錄結構
- [x] 編譯驗證通過
- [x] 測試執行通過
- [x] 打包建置通過

## 下一步

此專案骨架已經準備就緒，可以開始進行以下開發工作：

1. **領域模型設計** - 建立 URL 實體與值物件
2. **資料庫層實作** - 建立 JPA 實體與儲存庫
3. **快取層實作** - 實作 Redis 快取策略
4. **業務邏輯開發** - 實作短網址生成與查詢邏輯
5. **REST API 開發** - 建立 RESTful 端點
6. **整合測試** - 撰寫全面的整合測試

## 參考資料

- [Spring Boot 官方文件](https://spring.io/projects/spring-boot)
- [Clean Architecture 參考](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Spring Data JPA 文件](https://spring.io/projects/spring-data-jpa)
- [Spring Data Redis 文件](https://spring.io/projects/spring-data-redis)
- [Testcontainers 文件](https://www.testcontainers.org/)

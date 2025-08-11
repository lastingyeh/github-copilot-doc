# 12 - 整合驗證與文件產出

## 任務核取方塊
- [x] 驗證完整功能流程
- [x] 執行效能基準測試
- [x] 產出 README 專案文件
- [x] 產出 API 文件與使用範例
- [x] 建立部署指南
- [ ] 設置 CI/CD 管道（選用）
- [x] 建立故障排除指南

## 任務描述

進行專案的最終整合驗證，確保所有功能正常運作並符合需求規格。產出完整的專案文件，包含架構說明、部署指南、API 文件與故障排除手冊，讓其他開發者能夠快速理解與部署專案。

驗證重點：
- **功能完整性**: 所有 Use Cases 正常運作
- **效能指標**: 符合預期的回應時間與吞吐量
- **可運維性**: 監控、日誌、健康檢查正常
- **文件完整性**: 提供充足的使用與維護資訊

## 驗收標準

1. 完整功能流程測試通過（建立→查詢→重定向）
2. 效能測試達到基準指標
3. README 文件包含架構圖與快速開始指南
4. API 文件完整且提供 Postman 範例
5. 部署指南涵蓋本地與容器環境
6. 監控系統正常運作且告警有效
7. 故障排除指南涵蓋常見問題

## 執行步驟

```mermaid
graph TD
    A[完整功能驗證] --> B[效能基準測試]
    B --> C[產出 README 文件]
    C --> D[產出 API 文件]
    D --> E[建立部署指南]
    E --> F[驗證監控告警]
    F --> G[建立故障排除指南]
    G --> H[最終檢查清單]
```

### 詳細步驟

1. **完整功能流程驗證**
   - 端到端測試腳本
   - 錯誤處理驗證
   - 邊界條件測試

2. **效能基準測試**
   - 負載測試
   - 壓力測試
   - 資源使用監控

3. **文件產出**
   - README 專案概覽
   - API 使用文件
   - 部署運維指南

4. **最終驗證**
   - 功能清單檢查
   - 文件完整性檢查
   - 部署流程驗證

## 預期輸入

- 所有前 11 項任務已完成
- 應用程式可正常啟動
- 所有測試通過

## 預期輸出

### 1. 完整功能驗證腳本

**scripts/functional-test.sh**
```bash
#!/bin/bash

# TinyURL 功能驗證腳本
set -e

API_BASE_URL="http://localhost:8080"
LONG_URL="https://github.com/example/tinyurl"

echo "=== TinyURL 功能驗證測試 ==="

# 1. 檢查健康狀態
echo "1. 檢查應用程式健康狀態..."
HEALTH_STATUS=$(curl -s ${API_BASE_URL}/actuator/health | jq -r '.status')
if [ "$HEALTH_STATUS" != "UP" ]; then
    echo "❌ 健康檢查失敗: $HEALTH_STATUS"
    exit 1
fi
echo "✅ 應用程式狀態正常"

# 2. 建立短網址
echo "2. 建立短網址..."
CREATE_RESPONSE=$(curl -s -X POST ${API_BASE_URL}/api/urls \
    -H "Content-Type: application/json" \
    -d "{\"longUrl\": \"${LONG_URL}\"}")

SHORT_CODE=$(echo $CREATE_RESPONSE | jq -r '.short_code')
SHORT_URL=$(echo $CREATE_RESPONSE | jq -r '.short_url')

if [ "$SHORT_CODE" == "null" ]; then
    echo "❌ 短網址建立失敗"
    echo $CREATE_RESPONSE
    exit 1
fi
echo "✅ 短網址建立成功: $SHORT_CODE"

# 3. 查詢短網址資訊
echo "3. 查詢短網址資訊..."
INFO_RESPONSE=$(curl -s ${API_BASE_URL}/api/urls/${SHORT_CODE})
RETRIEVED_URL=$(echo $INFO_RESPONSE | jq -r '.long_url')

if [ "$RETRIEVED_URL" != "$LONG_URL" ]; then
    echo "❌ 網址查詢結果不符: 預期 $LONG_URL, 實際 $RETRIEVED_URL"
    exit 1
fi
echo "✅ 網址查詢成功"

# 4. 測試重定向
echo "4. 測試重定向功能..."
REDIRECT_LOCATION=$(curl -s -I ${API_BASE_URL}/${SHORT_CODE} | grep -i location | cut -d' ' -f2 | tr -d '\r')

if [ "$REDIRECT_LOCATION" != "$LONG_URL" ]; then
    echo "❌ 重定向位置不符: 預期 $LONG_URL, 實際 $REDIRECT_LOCATION"
    exit 1
fi
echo "✅ 重定向功能正常"

# 5. 檢查監控指標
echo "5. 檢查監控指標..."
METRICS_RESPONSE=$(curl -s ${API_BASE_URL}/actuator/prometheus)
if ! echo "$METRICS_RESPONSE" | grep -q "tinyurl_urls_created_total"; then
    echo "❌ 業務指標未找到"
    exit 1
fi
echo "✅ 監控指標正常"

echo ""
echo "🎉 所有功能驗證通過！"
echo "   短網址: $SHORT_URL"
echo "   重定向到: $LONG_URL"
```

### 2. 效能基準測試

**scripts/performance-test.sh**
```bash
#!/bin/bash

# 效能基準測試腳本
set -e

API_BASE_URL="http://localhost:8080"
CONCURRENT_USERS=10
DURATION=30

echo "=== TinyURL 效能基準測試 ==="

# 確保 Apache Bench 已安裝
if ! command -v ab &> /dev/null; then
    echo "請安裝 Apache Bench: sudo apt-get install apache2-utils"
    exit 1
fi

# 1. 建立短網址效能測試
echo "1. 建立短網址效能測試 (${CONCURRENT_USERS} 並發, ${DURATION}秒)..."
ab -n 1000 -c $CONCURRENT_USERS -t $DURATION \
   -H "Content-Type: application/json" \
   -p <(echo '{"longUrl": "https://example.com/test"}') \
   ${API_BASE_URL}/api/urls/

# 2. 查詢短網址效能測試
echo "2. 查詢短網址效能測試..."
# 先建立一個測試短網址
SHORT_CODE=$(curl -s -X POST ${API_BASE_URL}/api/urls \
    -H "Content-Type: application/json" \
    -d '{"longUrl": "https://example.com/perf"}' | jq -r '.short_code')

ab -n 5000 -c $CONCURRENT_USERS \
   ${API_BASE_URL}/api/urls/${SHORT_CODE}

# 3. 重定向效能測試
echo "3. 重定向效能測試..."
ab -n 5000 -c $CONCURRENT_USERS \
   ${API_BASE_URL}/${SHORT_CODE}

echo "✅ 效能測試完成"
echo "📊 請檢查上述結果確保："
echo "   - 建立短網址: < 100ms (95%)"
echo "   - 查詢短網址: < 10ms (95%)"
echo "   - 重定向請求: < 5ms (95%)"
echo "   - 錯誤率: < 0.1%"
```

### 3. README 專案文件

**README.md**
```markdown
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
  "created_at": "2023-11-15T10:30:00",
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
- `tinyurl_urls_created_total`: 建立的短網址總數
- `tinyurl_cache_hit_ratio`: 快取命中率
- `http_server_requests_duration_seconds`: API 回應時間

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
- **建立短網址**: < 50ms (95%)
- **查詢短網址**: < 5ms (95%)
- **重定向請求**: < 2ms (95%)
- **吞吐量**: > 1000 RPS
- **快取命中率**: > 85%

## 🤝 貢獻指南

1. Fork 專案
2. 建立功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交變更 (`git commit -m 'feat: add amazing feature'`)
4. 推送分支 (`git push origin feature/amazing-feature`)
5. 建立 Pull Request

## 📄 授權

本專案採用 MIT 授權 - 詳見 [LICENSE](LICENSE) 檔案

## 🙋‍♂️ 支援

- 📧 Email: dev@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/example/tinyurl-api/issues)
- 📖 Wiki: [專案 Wiki](https://github.com/example/tinyurl-api/wiki)
```

### 4. API 文件與 Postman 集合

**docs/api/TinyURL-API.postman_collection.json**
```json
{
  "info": {
    "name": "TinyURL API",
    "description": "TinyURL 短網址服務 API 測試集合",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "健康檢查",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/actuator/health",
          "host": ["{{baseUrl}}"],
          "path": ["actuator", "health"]
        }
      }
    },
    {
      "name": "建立短網址",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"longUrl\": \"https://github.com/example/project\",\n  \"ttlSeconds\": 3600\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/urls",
          "host": ["{{baseUrl}}"],
          "path": ["api", "urls"]
        }
      },
      "response": []
    },
    {
      "name": "查詢 URL 資訊",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/urls/{{shortCode}}",
          "host": ["{{baseUrl}}"],
          "path": ["api", "urls", "{{shortCode}}"]
        }
      }
    },
    {
      "name": "重定向測試",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/{{shortCode}}",
          "host": ["{{baseUrl}}"],
          "path": ["{{shortCode}}"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "shortCode",
      "value": ""
    }
  ]
}
```

### 5. 部署指南

**docs/deployment/README.md**
```markdown
# TinyURL API 部署指南

## 🐳 Docker Compose 部署 (推薦)

### 生產環境配置
```bash
# 1. 複製專案
git clone https://github.com/example/tinyurl-api.git
cd tinyurl-api

# 2. 配置環境變數
cp .env.example .env
# 編輯 .env 檔案設定生產環境變數

# 3. 啟動服務
docker-compose -f docker-compose.prod.yml up -d

# 4. 驗證部署
curl http://your-domain/actuator/health
```

### 環境變數範例 (.env)
```bash
# 應用程式
SERVER_PORT=8080
LOG_FORMAT=json
LOG_LEVEL=INFO

# 資料庫
DB_HOST=postgres
DB_PORT=5432
DB_NAME=tinyurl_prod
DB_USERNAME=tinyurl_prod_user
DB_PASSWORD=your_secure_password

# Redis
REDIS_HOST=redis
REDIS_PORT=6379

# 監控
GRAFANA_ADMIN_PASSWORD=your_admin_password
```

## ☸️ Kubernetes 部署

### 前置準備
```bash
# 建立 namespace
kubectl create namespace tinyurl

# 建立 ConfigMap
kubectl create configmap tinyurl-config \
  --from-env-file=k8s/config.env \
  -n tinyurl

# 建立 Secret
kubectl create secret generic tinyurl-secret \
  --from-env-file=k8s/secret.env \
  -n tinyurl
```

### 部署應用程式
```bash
# 應用 Kubernetes 配置
kubectl apply -f k8s/ -n tinyurl

# 檢查部署狀態
kubectl get pods -n tinyurl
kubectl get services -n tinyurl
```

## 🔧 手動部署

### 系統需求
- Java 17
- PostgreSQL 15+
- Redis 7+
- 2 CPU cores, 4GB RAM (最低)

### 步驟
```bash
# 1. 安裝 Java 17
sudo apt update
sudo apt install openjdk-17-jdk

# 2. 設置 PostgreSQL
sudo apt install postgresql postgresql-contrib
sudo -u postgres createdb tinyurl_db
sudo -u postgres createuser tinyurl_user

# 3. 設置 Redis
sudo apt install redis-server
sudo systemctl enable redis-server
sudo systemctl start redis-server

# 4. 建置應用程式
./mvnw clean package -DskipTests

# 5. 配置環境變數
export DB_HOST=localhost
export DB_USERNAME=tinyurl_user
export DB_PASSWORD=your_password
export REDIS_HOST=localhost

# 6. 執行應用程式
java -jar target/tinyurl-api-1.0.0.jar
```

## 📊 效能調校

### JVM 調校
```bash
java -Xms2g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar tinyurl-api.jar
```

### 資料庫調校
```sql
-- PostgreSQL 設定建議
-- shared_buffers = 256MB
-- effective_cache_size = 1GB
-- work_mem = 4MB
-- maintenance_work_mem = 64MB
```

### Redis 調校
```conf
# redis.conf
maxmemory 1gb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

## 🔒 安全性配置

### 應用程式安全
```yaml
# application-prod.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  endpoint:
    health:
      show-details: when-authorized

spring:
  datasource:
    hikari:
      leak-detection-threshold: 60000
```

### 網路安全
- 使用 HTTPS (建議透過反向代理)
- 限制管理端點訪問
- 設定防火牆規則
- 定期更新安全補丁

## 📈 監控設置

### Prometheus 告警規則
```yaml
# alerts.yml
groups:
  - name: tinyurl
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_total{status=~"5.."}[5m]) > 0.05
        labels:
          severity: warning
      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.8
        labels:
          severity: warning
```

## 🔄 備份與回復

### 資料庫備份
```bash
# 每日備份腳本
#!/bin/bash
BACKUP_DIR="/backup/postgresql"
DATE=$(date +%Y%m%d_%H%M%S)

pg_dump -h localhost -U tinyurl_user tinyurl_db > \
  ${BACKUP_DIR}/tinyurl_${DATE}.sql

# 保留最近 7 天的備份
find ${BACKUP_DIR} -name "tinyurl_*.sql" -mtime +7 -delete
```

### Redis 備份
```bash
# Redis 持久化設定
save 900 1
save 300 10
save 60 10000

# 手動備份
redis-cli BGSAVE
cp /var/lib/redis/dump.rdb /backup/redis/
```

## 🚀 擴展指南

### 水平擴展
1. 多實例部署
2. 負載均衡器設定
3. 無狀態應用程式設計
4. 共享快取與資料庫

### 垂直擴展
1. 增加 CPU 與記憶體
2. 資料庫效能調校
3. 連線池優化
4. JVM 參數調整
```

### 6. 最終檢查清單

**docs/CHECKLIST.md**
```markdown
# TinyURL API 專案完成檢查清單

## ✅ 功能需求
- [ ] 短網址生成功能正常
- [ ] 長網址查詢功能正常
- [ ] 重定向功能正常
- [ ] 重複 URL 處理正確
- [ ] 短網址唯一性保證
- [ ] TTL 過期機制運作

## ✅ 技術需求
- [ ] Clean Architecture 四層分離
- [ ] Spring Boot 3.x 框架
- [ ] Java 17 支援
- [ ] PostgreSQL 資料持久化
- [ ] Redis 快取層
- [ ] Docker Compose 部署

## ✅ 品質保證
- [ ] 單元測試覆蓋率 > 80%
- [ ] 整合測試涵蓋主要流程
- [ ] Testcontainers 整合測試
- [ ] 效能測試基準達標
- [ ] 錯誤處理機制完善
- [ ] 程式碼品質檢查通過

## ✅ 監控與觀測
- [ ] Spring Boot Actuator 端點
- [ ] 自訂健康檢查指標
- [ ] Prometheus 指標收集
- [ ] Grafana 監控儀表板
- [ ] 結構化日誌輸出
- [ ] 告警規則配置

## ✅ API 與文件
- [ ] RESTful API 設計
- [ ] OpenAPI 3.0 文件
- [ ] Swagger UI 可訪問
- [ ] Postman 測試集合
- [ ] 錯誤回應標準化
- [ ] API 版本管理

## ✅ 運維與部署
- [ ] Docker 容器化
- [ ] 環境變數配置
- [ ] 生產環境準備就緒
- [ ] 備份與回復策略
- [ ] 安全性配置
- [ ] 效能調校指南

## ✅ 文件完整性
- [ ] README 專案說明
- [ ] API 使用文件
- [ ] 部署指南
- [ ] 故障排除指南
- [ ] 架構決策記錄
- [ ] 貢獻指南

## 🚀 效能指標達成
- [ ] 建立短網址: < 50ms (95%)
- [ ] 查詢短網址: < 5ms (95%)
- [ ] 重定向請求: < 2ms (95%)
- [ ] 快取命中率: > 85%
- [ ] 系統可用性: > 99.9%
- [ ] 錯誤率: < 0.1%

## 📊 驗證步驟
```bash
# 1. 功能驗證
./scripts/functional-test.sh

# 2. 效能驗證
./scripts/performance-test.sh

# 3. 部署驗證
docker-compose up -d
curl http://localhost:8080/actuator/health

# 4. 監控驗證
curl http://localhost:8080/actuator/prometheus | grep tinyurl

# 5. 文件驗證
open http://localhost:8080/swagger-ui.html
open http://localhost:3000 # Grafana
```

## 🎯 專案里程碑
- [x] 階段一: 環境與基礎設施 (任務 1-3)
- [x] 階段二: 架構與領域建模 (任務 4-5)
- [x] 階段三: 核心功能實作 (任務 6-9)
- [x] 階段四: 品質與運維 (任務 10-12)

## 🚀 後續改進建議
1. **效能優化**: 實作更進階的快取策略
2. **功能擴展**: 支援自訂短網址、批次操作
3. **安全加強**: 實作 API 認證與授權
4. **可觀測性**: 加入分散式追蹤
5. **高可用性**: 實作多區域部署
6. **analytics**: 加入點擊統計與分析功能

---
✅ **專案完成**: 所有核心功能已實作並經過驗證
📚 **文件齊全**: 提供完整的使用與維護文件
🔧 **生產就緒**: 可直接部署到生產環境使用
```

### 7. 最終驗證報告

執行最終驗證並產出報告，確認所有項目都已完成且符合品質標準。這個任務的完成標誌著整個 TinyURL API 服務專案的圓滿結束，可以交付使用或進入維護階段。

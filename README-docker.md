# Docker 開發環境使用說明

## 概覽

此 Docker Compose 環境提供 TinyURL 服務開發所需的完整基礎設施，包含：

- **PostgreSQL 15**：主要資料庫
- **Redis 7**：快取與 Session 存儲
- **Prometheus**：指標收集與監控
- **Grafana**：監控視覺化儀表板

## 快速開始

### 1. 複製環境變數檔案

```bash
cp .env.example .env
```

編輯 `.env` 檔案以符合您的本地開發需求。

### 2. 啟動所有服務

```bash
# 啟動所有服務（背景執行）
docker-compose up -d

# 查看服務狀態
docker-compose ps

# 查看服務日誌
docker-compose logs -f
```

### 3. 驗證服務狀態

```bash
# 檢查所有服務健康狀態
docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"

# 個別檢查服務
docker-compose exec postgres pg_isready -U tinyurl_user -d tinyurl_db
docker-compose exec redis redis-cli ping
```

## 服務存取點

| 服務       | 網址                  | 預設帳密                  |
| ---------- | --------------------- | ------------------------- |
| PostgreSQL | `localhost:5432`      | tinyurl_user/tinyurl_pass |
| Redis      | `localhost:6379`      | 無密碼                    |
| Prometheus | http://localhost:9090 | 無需認證                  |
| Grafana    | http://localhost:3000 | admin/admin123            |

## 常用操作

### 資料庫操作

```bash
# 連線到 PostgreSQL
docker-compose exec postgres psql -U tinyurl_user -d tinyurl_db

# 執行 SQL 檔案
docker-compose exec -T postgres psql -U tinyurl_user -d tinyurl_db < your-script.sql

# 資料庫備份
docker-compose exec postgres pg_dump -U tinyurl_user tinyurl_db > backup.sql

# 資料庫還原
docker-compose exec -T postgres psql -U tinyurl_user -d tinyurl_db < backup.sql
```

### Redis 操作

```bash
# 連線到 Redis CLI
docker-compose exec redis redis-cli

# 查看 Redis 資訊
docker-compose exec redis redis-cli info

# 清除所有快取
docker-compose exec redis redis-cli flushall
```

### 監控操作

```bash
# 重新載入 Prometheus 配置
curl -X POST http://localhost:9090/-/reload

# 查看 Prometheus 目標狀態
curl http://localhost:9090/api/v1/targets
```

## 環境管理

### 停止服務

```bash
# 停止所有服務
docker-compose down

# 停止並移除 volumes（會刪除所有資料）
docker-compose down -v

# 停止特定服務
docker-compose stop postgres
```

### 重建服務

```bash
# 重建並啟動所有服務
docker-compose up --build -d

# 重建特定服務
docker-compose up --build -d postgres
```

### 清理環境

```bash
# 清理未使用的 Docker 資源
docker system prune -f

# 清理所有 volumes（危險操作，會刪除所有資料）
docker-compose down -v
docker volume prune -f
```

## 故障排除

### 常見問題

1. **埠號衝突**
   ```bash
   # 檢查埠號使用情況
   lsof -i :5432
   lsof -i :6379
   lsof -i :9090
   lsof -i :3000
   ```

2. **服務啟動失敗**
   ```bash
   # 查看詳細錯誤日誌
   docker-compose logs [service-name]

   # 重啟特定服務
   docker-compose restart [service-name]
   ```

3. **資料庫連線問題**
   ```bash
   # 檢查 PostgreSQL 是否就緒
   docker-compose exec postgres pg_isready -U tinyurl_user -d tinyurl_db

   # 查看資料庫日誌
   docker-compose logs postgres
   ```

4. **Redis 記憶體問題**
   ```bash
   # 檢查 Redis 記憶體使用
   docker-compose exec redis redis-cli info memory

   # 清除過期 keys
   docker-compose exec redis redis-cli --scan --pattern "*" | xargs docker-compose exec redis redis-cli del
   ```

### 效能調整

1. **PostgreSQL 效能**
   - 修改 `docker-compose.yml` 中的 `shared_buffers`、`effective_cache_size` 等參數
   - 監控連線池使用情況

2. **Redis 效能**
   - 調整 `maxmemory` 和 `maxmemory-policy` 設定
   - 使用 `redis-cli --latency` 監控延遲

## 開發工作流程

### 本地開發流程

1. 啟動基礎設施服務：
   ```bash
   docker-compose up -d postgres redis
   ```

2. 本地執行 Spring Boot 應用程式

3. 需要監控時啟動 Prometheus 和 Grafana：
   ```bash
   docker-compose up -d prometheus grafana
   ```

### 完整環境測試

```bash
# 啟動完整環境
docker-compose up -d

# 等待所有服務就緒
sleep 30

# 執行健康檢查
./scripts/health-check.sh  # 如果有此腳本

# 執行整合測試
./mvnw test -Dspring.profiles.active=integration
```

## 監控設定

### Grafana 儀表板

初次登入 Grafana 後：

1. 使用預設帳密 `admin/admin123` 登入
2. Prometheus 資料源已自動配置
3. 匯入 Spring Boot 相關儀表板：
   - JVM Micrometer（ID: 4701）
   - Spring Boot Statistics（ID: 6756）

### 自訂指標

在 Spring Boot 應用程式中，確保暴露以下端點：
- `/actuator/health` - 健康檢查
- `/actuator/prometheus` - Prometheus 指標
- `/actuator/info` - 應用程式資訊

## 安全注意事項

⚠️ **重要**：此配置僅適用於本地開發環境，不可直接用於生產環境。

生產環境需要考慮：
- 使用強密碼和憑證管理
- 網路隔離和防火牆設定
- SSL/TLS 加密
- 資料備份策略
- 監控告警設定

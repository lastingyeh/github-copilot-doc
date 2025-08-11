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

## 🛠️ 常用部署指令

```bash
# 檢查服務狀態
docker-compose ps

# 查看應用程式日誌
docker-compose logs tinyurl-api

# 重啟服務
docker-compose restart tinyurl-api

# 更新應用程式
docker-compose pull tinyurl-api
docker-compose up -d tinyurl-api

# 備份資料
docker-compose exec postgres pg_dump -U tinyurl_user tinyurl_db > backup.sql

# 監控系統資源
docker stats

# 清理未使用的容器和映像
docker system prune -f
```

## 🔍 健康檢查腳本

```bash
#!/bin/bash
# health-check.sh

API_URL="http://localhost:8080"

# 檢查應用程式健康
HEALTH=$(curl -s ${API_URL}/actuator/health | jq -r '.status')
if [ "$HEALTH" != "UP" ]; then
    echo "❌ 應用程式狀態異常: $HEALTH"
    exit 1
fi

# 檢查資料庫連線
DB_STATUS=$(curl -s ${API_URL}/actuator/health | jq -r '.components.db.status')
if [ "$DB_STATUS" != "UP" ]; then
    echo "❌ 資料庫連線異常"
    exit 1
fi

# 檢查 Redis 連線
REDIS_STATUS=$(curl -s ${API_URL}/actuator/health | jq -r '.components.redis.status')
if [ "$REDIS_STATUS" != "UP" ]; then
    echo "❌ Redis 連線異常"
    exit 1
fi

echo "✅ 所有服務運行正常"
```

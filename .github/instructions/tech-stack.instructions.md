---
applyTo: '**'
---
# 技術棧規範（必選與可選）

> **語言**：所有輸出一律使用**繁體中文**。

## 必選（Required）
- **Java**：17（LTS）
- **Spring Boot**：3.x
- **建置**：Maven（必用 Maven Wrapper `mvnw`）
- **日誌**：SLF4J API + Logback（console + JSON）
- **資料庫**：PostgreSQL（primary DB）
- **快取**：Redis（cache/session）
- **資料遷移**：Flyway
- **監控**：Spring Boot Actuator + Micrometer + Prometheus
- **健康檢查**：`/actuator/health` + 自訂 `HealthIndicator`
- **測試**：JUnit 5 + Mockito + **Testcontainers（Postgres/Redis）**
- **OpenAPI**：SpringDoc（如 `springdoc-openapi-starter-webmvc-ui`）
- **部署**：Docker Compose（至少包含 `app、Postgres、Redis、Prometheus、Grafana`）
- **預設埠/名稱**：
  - App `8080`；DB 名稱 `orders_db`；Redis `6379`；Prometheus `9090`；Grafana `3000`

## 可選（Optional）
- **訊息佇列**：RabbitMQ / Kafka /（GCP）Pub/Sub
- **額外儲存**：MongoDB（如需文件型資料）
- **快取應用**：Session 存放、查詢結果快取、分散式鎖
- **觀測性加值**：Trace（OpenTelemetry Collector）、自訂 Metrics、Log 轉送（EFK/Vector）

## 組態與環境變數（所有環境必須可覆蓋）
- **DB**：`DB_HOST`, `DB_PORT`, `DB_NAME=orders_db`, `DB_USERNAME`, `DB_PASSWORD`
- **Redis**：`REDIS_HOST`, `REDIS_PORT=6379`
- **Logging**：`LOG_FORMAT` = `console` / `json`
- **管理埠**（可選）：`MANAGEMENT_PORT`
- **Prometheus**：必須啟用 `/actuator/prometheus` 供抓取

## 產物要求（當我要求 scaffold / sample）
1. **`pom.xml`**：明確列出相依與外掛**版本**（Spring Boot 3.x、Lombok、SpringDoc、Flyway、Micrometer、Testcontainers...）。
2. **`application.yml`**：以環境變數覆蓋（如上），含 DB、Redis、Actuator、Logging、OpenAPI。
3. **`logback-spring.xml`**：提供 console 與 JSON 兩種輸出模式（以 Profile 或 `LOG_FORMAT` 切換）。
4. **`Dockerfile` + `docker-compose.yml`**：
   - 服務：`app、postgres、redis、prometheus、grafana`
   - Prometheus 目標：`app: /actuator/prometheus`
   - 預設埠與帳密（若需要）以環境變數提供。
5. **監控**：啟動後 Grafana 可匯入 Micrometer 預設儀表板。
6. **Orders 範例**：`Create / Get（含 Redis 快取） / List` 完整骨架（Controller + Use case + Ports + Adapters + Infra 實作）。
7. **CI/CD 友善**：本地與 CI 可直接執行 Testcontainers 測試。

## 效能與最佳實務（跨技術棧）
- 預設為分頁查詢與投影（避免大 ResultSet）
- 為常用查詢建立索引；以 TTL 與 key 設計管理 Redis 快取
- 合理使用批次寫入與外鍵約束；避免過度雙寫
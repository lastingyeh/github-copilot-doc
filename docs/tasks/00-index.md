# TinyURL API 服務開發任務清單

## 專案簡介

這是一個採用 Clean Architecture 設計模式的短網址服務，基於 Spring Boot 3.x、Java 17、PostgreSQL 與 Redis 構建。核心功能包括長網址轉換為短網址以及短網址重定向到原始長網址。

## 技術架構

- **後端框架**：Spring Boot 3.x + Java 17
- **資料庫**：PostgreSQL（主資料庫）+ Redis（快取）
- **架構模式**：Clean Architecture 四層分離
- **容器化**：Docker Compose
- **監控**：Prometheus + Grafana
- **測試**：JUnit 5 + Testcontainers

## 開發任務列表

| 編號 | 任務名稱                         | 描述                                                                      | 檔案連結                                                                 |
| ---- | -------------------------------- | ------------------------------------------------------------------------- | ------------------------------------------------------------------------ |
| 01   | 設置 Docker 開發環境             | 建立 docker-compose.yml，包含 PostgreSQL、Redis、Prometheus、Grafana 服務 | [01-setup-docker.md](./01-setup-docker.md)                               |
| 02   | 建立 Spring Boot 專案骨架        | 使用 Maven 建立 Spring Boot 3.x 專案結構與基本依賴                        | [02-init-springboot-project.md](./02-init-springboot-project.md)         |
| 03   | 配置應用程式組態與環境變數       | 設定 application.yml、logback-spring.xml 與環境變數覆蓋機制               | [03-configure-application.md](./03-configure-application.md)             |
| 04   | 建立 Clean Architecture 目錄結構 | 按照四層架構建立 domain、application、adapters、infrastructure 目錄       | [04-create-clean-architecture.md](./04-create-clean-architecture.md)     |
| 05   | 實作領域模型與儲存庫介面         | 建立 URL 聚合根、值對象與 Repository 介面（不含實作）                     | [05-implement-domain-model.md](./05-implement-domain-model.md)           |
| 06   | 實作資料庫持久層                 | 建立 JPA Entity、Repository 實作與 Flyway 遷移腳本                        | [06-implement-persistence-layer.md](./06-implement-persistence-layer.md) |
| 07   | 實作 Redis 快取層                | 建立 Redis 快取實作，支援 URL 查詢快取與 TTL 管理                         | [07-implement-cache-layer.md](./07-implement-cache-layer.md)             |
| 08   | 實作核心 Use Cases               | 建立 CreateShortUrl、GetLongUrl、RedirectUrl 用例與相關 Port 介面         | [08-implement-use-cases.md](./08-implement-use-cases.md)                 |
| 09   | 實作 REST API 控制器             | 建立 Web 適配器、DTO 與控制器，提供短網址 CRUD 與重定向 API               | [09-implement-rest-controllers.md](./09-implement-rest-controllers.md)   |
| 10   | 設置監控與觀測性                 | 配置 Spring Boot Actuator、自訂 Metrics 與健康檢查端點                    | [10-setup-observability.md](./10-setup-observability.md)                 |
| 11   | 撰寫整合測試                     | 使用 Testcontainers 撰寫 PostgreSQL 與 Redis 整合測試                     | [11-write-integration-tests.md](./11-write-integration-tests.md)         |
| 12   | 整合驗證與文件產出               | 驗證所有功能運作正常，產出 README、API 文件與部署指南                     | [12-integration-validation.md](./12-integration-validation.md)           |

## 開發流程

### 階段一：環境與基礎設施（任務 1-3）
建立開發環境、專案基礎結構與基本配置。

### 階段二：架構與領域建模（任務 4-5）
依據 Clean Architecture 原則建立專案結構與領域模型。

### 階段三：核心功能實作（任務 6-9）
實作資料持久層、快取層、業務邏輯與 API 介面。

### 階段四：品質與運維（任務 10-12）
加入監控、測試與最終整合驗證。

## 預計開發時程

- **總時程**：約 2-3 週
- **每日可完成**：1-2 項任務
- **里程碑**：每完成一個階段進行整體測試

## 注意事項

1. 每個任務都需要包含相應的單元測試
2. 遵循 Conventional Commits 規範進行 Git 提交
3. 所有文件與註解使用繁體中文
4. 確保每個階段完成後應用程式可正常編譯與運行

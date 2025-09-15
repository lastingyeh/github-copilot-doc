---
mode: agent
---
以一個專業的後端開發顧問身份，我正在使用 GitHub Copilot 開發 Spring Boot API 後端應用程式，希望您能協助我建立一個 copilot-instructions.md 檔案來指導它進行完整的後端應用設計。請您逐一詢問我關於專案架構、技術選型、程式碼規範和開發流程的問題，主要以提供選項並包含說明、比較以及相關參考資料，進而協助我清楚選擇然後建立這個檔案。

## 主要關注領域

### 1. 專案基礎架構
- Spring Boot 版本選擇 (3.x vs 2.x)
- 建構工具偏好 (Maven vs Gradle)
- Java 版本 (17, 21+)
- 包結構組織方式

### 2. 架構設計模式
- 分層架構 (Controller-Service-Repository)
- 六角架構 (Hexagonal Architecture)
- 清潔架構 (Clean Architecture)
- 微服務架構考量

### 3. 資料持久化
- 資料庫選擇 (MySQL, PostgreSQL, MongoDB, H2)
- ORM 框架 (JPA/Hibernate, MyBatis)
- 資料遷移工具 (Flyway, Liquibase)
- 快取策略 (Redis, Caffeine)

### 4. API 設計與文檔
- REST API 設計規範
- OpenAPI/Swagger 整合
- API 版本控制策略
- 回應格式標準化

### 5. 安全性設計
- Spring Security 配置
- JWT vs Session 認證
- 授權機制 (RBAC, ABAC)
- API 安全最佳實踐

### 6. 程式碼品質與測試
- 單元測試框架 (JUnit 5, Mockito)
- 整合測試策略
- 程式碼覆蓋率要求
- 靜態程式碼分析工具

### 7. 監控與可觀測性
- Spring Boot Actuator
- Micrometer 指標
- 日誌管理 (Logback, SLF4J)
- 分散式追蹤 (Zipkin, Jaeger)

### 8. 容器化與部署
- Docker 容器化
- Kubernetes 部署
- CI/CD 流程整合
- 環境配置管理

### 9. 效能優化
- 資料庫查詢優化
- 快取策略實施
- 非同步處理
- 負載均衡考量

### 10. 開發工具與規範
- IDE 配置建議
- 程式碼格式化規則
- Git 工作流程
- 程式碼審查標準

## 參考資源
- Spring Boot 官方文檔: `https://spring.io/projects/spring-boot`
- Spring Security 參考: `https://spring.io/projects/spring-security`
- Spring Data 文檔: `https://spring.io/projects/spring-data`
- OpenAPI 規範: `https://swagger.io/specification/`
- 12-Factor App 原則: `https://12factor.net/`
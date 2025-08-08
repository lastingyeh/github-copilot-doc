---
applyTo: '**'
---
# Development Guidelines

## 0. 開發指南

- 必須使用 **繁體中文** 於所有溝通和文件

## 1. API 架構

- 使用 RESTful API 設計原則

## 2. 程式碼標準

- 樣式指南：遵循 Google Java Style Guide，並更好地實現 `clean architecture` 原則
- 縮排：4 個空格（不使用 tabs）
- 命名規則：
    - Classes：PascalCase
    - Methods/Variables：camelCase
    - Constants：UPPER_SNAKE_CASE
    - DTOs：以 Request/Response/Dto 為後綴
    - Repositories：XxxRepository
    - Enums：UPPER_SNAKE_CASE

## 3. 目錄結構

```text
.
├── docker      # Docker 相關檔案（例如：Dockerfile、docker-compose.yml）
├── docs        # 專案文件檔案
└── src/main/java/com/example/project
    ├── config      # 配置檔案（例如：Beans、Security）
    ├── controller  # 處理 HTTP 請求的 Controllers（API endpoints）
    ├── exception   # 全域例外處理器和自定義例外類別
    ├── model
    │   ├── dto     # 請求/回應主體的 Data Transfer Objects
    │   └── entity  # 資料庫實體（例如：JPA）
    ├── repository  # 資料存取的 Repository interfaces
    ├── service     # 業務邏輯層
    └── util        # 工具類別
```

## 4. 開發實務

- Controller 層：輸入驗證和 service 委派
- Service 層：包含業務邏輯
- Repository 層：透過 Spring Data JPA 進行資料存取
- Constants：使用常數而非魔法數字
- Optional：優先使用而非 null 回傳
- Logging：使用 @Slf4j（Lombok）
- 文件：為 public APIs 撰寫 JavaDoc
- Dependency Injection：基於建構子的方式（不使用 @Autowired）
- 物件建立：對複雜物件使用 @Builder

## 5. Testing

- 覆蓋率：所有 service 方法都要有單元測試
- Framework：JUnit 5 搭配 Mockito
- Test Class Modifier：測試類別使用 package-private scope
- 測試文件：使用 @DisplayName 提高清晰度
- 測試案例：
    - Positive：預期的成功行為
    - Negative：錯誤和例外情境

## 6. 技術

- Java 17
- Maven: 使用 Maven Wrapper (`mvnw`)
- Spring Boot 3+
- Docker: 優先使用新版 `docker compose` 指令語法
- Lombok Annotations:
    - @Getter, @Setter, @Builder, @Slf4j
    - Avoid field injection
- SpringDoc OpenAPI 用於文件
- Database (如無特殊偏好使用 Postgres):
  - Postgres
  - MongoDB
- Cache (選用，可用於資料快取或 sessions):
  - Redis
- Message Queue (選用，僅在需要時使用):
  - RabbitMQ
  - Kafka
  - Pubsub for GCP

## 7. 最佳實務

- 註解：僅用於非顯而易見的邏輯
- 文件：專注於「為什麼」而非「是什麼」
- 錯誤訊息：清楚且具描述性
- 例外處理：實作全域處理
- 不可變性：在可能的情況下優先使用
- Static Methods：限制在工具類別中使用
- 效能和最佳實務：所有開發任務都應考慮效能並遵循業界最佳實務
- 善用現成工具：優先使用穩定可靠的套件或函式庫，避免重複造輪子

## 8. 驗證

- 使用 Jakarta Validation annotations
- Controller Class：@Validated
- Request Bodies：@Valid
- 錯誤訊息：清楚且具描述性

## 9. API 文件

- 使用 SpringDoc OpenAPI
- Controllers：@Tag annotation
- Endpoints：@Operation annotation
- DTO Fields：@Schema 搭配描述

## 10. Git Commit 慣例

- Commit Message 結構
  ```text
  <type>: <subject>

  <body>
  ```
    - 範例
      ```text
      fix: 修正員工管理中的時間格式錯誤

      1. 更新 TimeUtils 中的時間轉換邏輯，從時間戳中減去時區偏移量
      ```
- Type 定義
    - feat：新增或修改功能
    - fix：修正錯誤
    - docs：更新或新增文件
    - style：不影響行為的程式碼風格變更（例如：格式化、空白字元、缺少分號）
    - refactor：程式碼重構（業務邏輯保持不變）
    - perf：改善效能的程式碼變更
    - test：新增或更新測試
    - chore：建置流程、工具或維護任務的變更
    - revert：回復先前的 commit（例如：revert: feat(auth): support OAuth2 login）
- Subject
    - 變更的簡短摘要（祈使語氣，結尾不加句號）
- Body
    - 選用。以項目符號列表詳細說明變更：
        - 變更了什麼以及為什麼
        - 影響或背景（如有需要）

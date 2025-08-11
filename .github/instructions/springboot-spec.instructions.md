---
applyTo: '**'
---
# Spring Boot 框架與目錄規範（Clean Architecture）

> **語言**：所有輸出一律使用**繁體中文**（程式碼註解、提交訊息、PR、文件）。

## 架構總則
- **API**：採用 RESTful 原則。
- **Clean Architecture**：分層 `domain / application / adapters / infrastructure`
  - **禁止** `domain` 直接依賴 Spring 與框架型註解。
  - 以 **use case** 驅動（application 層），I/O 透過 **ports** 抽象。
- **功能切片**：以 `Orders` 為範例，至少提供：
  - `CreateOrder`、`GetOrderById`（含 Redis 快取）、`ListOrders`

## 套用的封裝與命名
- **Style**：Google Java Style；縮排 4 spaces、禁用 tab。
- **命名**：
  - 類別：PascalCase；方法/變數：camelCase；常數：UPPER_SNAKE_CASE
  - DTO 後綴：`Request` / `Response` / `Dto`
  - Repository 介面：`XxxRepository`
  - Enum 常數：UPPER_SNAKE_CASE
- **原則**：
  - 避免魔術數字（以常數取代）
  - 回傳值優先使用 `Optional`（避免 `null`）
  - 依賴注入採**建構式注入**（禁止欄位 `@Autowired`）
  - getter, setter 使用 Lombok `@Getter`/`@Setter` 或是 `@Data`
  - 複雜物件使用 Lombok `@Builder`
  - 記錄使用 Lombok `@Slf4j`
  - 公共 API 撰寫 JavaDoc，強調**為什麼**（非重述**做了什麼**）

## 專案目錄（Clean Architecture）
```

src/main/java/com/example/orders
├── domain                      # 純領域模型（無框架依賴）
│   ├── model                   # Aggregate/Entity/ValueObject
│   ├── repository              # 介面（如 OrderRepository）
│   ├── service                 # Domain Service（必要時）
│   └── event                   # Domain Events（如 OrderCreated）
├── application                 # Use Cases 與 Ports
│   ├── usecase                 # Interactors（Create/Get/List）
│   ├── port
│   │   ├── in                  # Commands/Queries Port（input）
│   │   └── out                 # Repository/Cache/MQ Port（output）
│   └── dto                     # Use case 輸入/輸出 DTO（非 Web DTO）
├── adapters                    # 介面層（依賴 application 的 port）
│   ├── web                     # REST Controller + Web 專用 DTO/Mappers
│   ├── messaging               # Kafka/RabbitMQ Pub/Sub（可選）
│   └── scheduler               # 排程（可選）
└── infrastructure              # 技術細節（Spring/I/O/組態）
├── persistence
│   ├── jpa                 # JPA Entity/Repository 實作、mapping
├── cache
│   └── redis               # Redis 快取實作、序列化
├── config                  # Spring 組態（Beans/Security/Observability）
├── openapi                 # SpringDoc 設定
└── observability           # Micrometer/Actuator 設定

```

> **建議**：`domain.model.Order` 與 `infrastructure.persistence.jpa.OrderEntity` 分離，以 mapping 解耦（避免讓 JPA 註解滲入 domain）。

## 控制器 / 驗證 / 文件
- **Controller** 僅負責：輸入驗證（`@Validated` + `@Valid`）、轉換 Web DTO ⇄ Use case DTO、調用 use case。
- **OpenAPI（SpringDoc）**：
  - Controller 使用 `@Tag`；Endpoint 使用 `@Operation`
  - Web DTO 欄位以 `@Schema` 撰寫描述
- **全域例外處理**：集中處理商業錯誤與驗證錯誤，訊息需清晰可讀。

## 測試規範（框架面）
- **JUnit 5 + Mockito**；測試類維持 **package-private**
- 使用 `@DisplayName` 增加可讀性；覆蓋：
  - Positive（成功）
  - Negative（錯誤／例外）
-如涉及外部 I/O，使用 Testcontainers
  - **Testcontainers**：Postgres/Redis 由 tech stack 檔定義；此處僅要求 service/adapter 層可在 CI 以容器測試啟動。

## 性能與實務
- 避免 N+1、必要欄位索引、合理快取 TTL
- 適度使用批量操作與分頁查詢
- 日誌支援 console 與 JSON（由 `LOG_FORMAT` 或 Spring Profile 切換）

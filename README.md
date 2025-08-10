# TinyURL API - Spring Boot 專案

這是一個基於 Clean Architecture 架構的短網址服務 API，使用 Spring Boot 3.x 建構。

## 專案資訊

- **專案名稱**: TinyURL API
- **版本**: 1.0.0-SNAPSHOT
- **Java 版本**: 17
- **Spring Boot 版本**: 3.2.0
- **建置工具**: Maven 3.9.5
- **架構模式**: Clean Architecture

## 技術棧

### 核心框架
- **Spring Boot 3.2.0** - 核心應用框架
- **Spring Data JPA** - 資料持久化
- **Spring Data Redis** - 快取支援
- **Spring Boot Actuator** - 監控與管理
- **Spring Boot Validation** - 資料驗證

### 資料庫與快取
- **PostgreSQL 42.6.0** - 主要資料庫
- **Redis with Jedis** - 分散式快取
- **Flyway** - 資料庫遷移
- **H2** - 測試資料庫

### 測試
- **Spring Boot Test** - 整合測試
- **JUnit 5** - 單元測試框架
- **Testcontainers 1.19.3** - 容器化測試

### 文件與工具
- **SpringDoc OpenAPI 2.2.0** - API 文件生成
- **Lombok 1.18.30** - 程式碼簡化
- **Micrometer Prometheus** - 指標收集

## 專案結構

```
src/
├── main/
│   ├── java/com/example/tinyurl/
│   │   ├── TinyUrlApplication.java           # 應用程式進入點
│   │   ├── domain/                           # 領域層
│   │   │   ├── model/                        # 領域模型
│   │   │   └── repository/                   # 領域儲存庫介面
│   │   ├── application/                      # 應用層
│   │   │   ├── usecase/                      # 使用案例實作
│   │   │   └── port/                         # 端口定義
│   │   │       ├── in/                       # 輸入端口
│   │   │       └── out/                      # 輸出端口
│   │   └── infrastructure/                   # 基礎設施層
│   │       ├── persistence/                  # 資料持久化實作
│   │       ├── cache/                        # 快取實作
│   │       ├── web/                          # Web 控制器
│   │       └── config/                       # 配置類別
│   └── resources/
│       └── application.yml                   # 應用程式配置
└── test/
    ├── java/com/example/tinyurl/
    │   └── TinyUrlApplicationTest.java       # 整合測試
    └── resources/
        └── application-test.yml              # 測試配置
```

## 快速開始

### 前置需求
- Java 17+
- Docker & Docker Compose（用於資料庫和快取）
- Maven 3.8+（或使用專案提供的 Maven Wrapper）

### 1. 克隆專案
```bash
git clone <repository-url>
cd shorturlapi-lab
```

### 2. 啟動資料庫與快取服務
```bash
docker-compose up -d postgres redis
```

### 3. 編譯專案
```bash
./mvnw clean compile
```

### 4. 執行測試
```bash
./mvnw test
```

### 5. 啟動應用程式
```bash
./mvnw spring-boot:run
```

### 6. 訪問 API 文件
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

### 7. 監控端點
- 健康檢查: http://localhost:8080/actuator/health
- 指標: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

## 開發指令

```bash
# 編譯專案
./mvnw clean compile

# 執行測試
./mvnw test

# 打包應用程式
./mvnw package

# 跳過測試打包
./mvnw package -DskipTests

# 啟動應用程式（開發模式）
./mvnw spring-boot:run

# 清理建置產物
./mvnw clean
```

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

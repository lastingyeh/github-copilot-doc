# 整合測試說明文件

## 概覽

本專案實作了完整的整合測試套件，使用 Testcontainers 提供真實的 PostgreSQL 與 Redis 環境，確保各層之間的整合正確性。

## 測試架構

```
src/test/java/
├── com/example/tinyurl/
│   ├── AbstractIntegrationTest.java                    # 整合測試基類
│   ├── test/
│   │   └── UrlTestFactory.java                        # 測試資料工廠
│   ├── infrastructure/
│   │   ├── persistence/
│   │   │   └── UrlRepositoryIntegrationTest.java      # Repository 層測試
│   │   └── cache/
│   │       └── RedisUrlCacheServiceIntegrationTest.java # Cache 層測試
│   ├── application/
│   │   └── usecase/
│   │       └── CreateShortUrlUseCaseIntegrationTest.java # Use Case 層測試
│   └── adapters/
│       └── web/
│           └── controller/
│               └── UrlControllerIntegrationTest.java  # API 層測試
```

## 測試基類 - AbstractIntegrationTest

### 功能特色

- **自動化容器管理**: 使用 Testcontainers 自動啟動 PostgreSQL 與 Redis
- **動態配置**: 自動配置資料庫連接與 Redis 連接
- **測試隔離**: 每個測試方法執行前自動清理資料
- **共用工具**: 提供測試常用的工具方法

### 使用方式

```java
@DisplayName("我的整合測試")
class MyIntegrationTest extends AbstractIntegrationTest {

    @Test
    void shouldDoSomething() {
        // 測試邏輯
        // 可以直接使用 urlRepository, cachePort, restTemplate 等
    }
}
```

## 測試資料工廠 - UrlTestFactory

### 提供的工廠方法

```java
// 建立基本 URL
Url url = UrlTestFactory.createUrl();

// 建立指定內容的 URL
Url url = UrlTestFactory.createUrl("https://example.com", "abc123");

// 建立指定時間的 URL
Url url = UrlTestFactory.createUrlWithTime(longUrl, shortCode, createdAt);

// 建立指定存取次數的 URL
Url url = UrlTestFactory.createUrlWithAccessCount(longUrl, shortCode, count);

// 建立命令物件
CreateShortUrlCommand cmd = UrlTestFactory.createCommand("https://example.com");

// 建立請求物件
CreateUrlRequest req = UrlTestFactory.createRequest("https://example.com");
```

## 各層測試說明

### 1. Repository 層測試

**測試範圍**:
- 基本 CRUD 操作
- 複雜查詢功能（長網址查詢、統計查詢等）
- 資料完整性約束
- 唯一性檢查

**關鍵測試案例**:
```java
@Test
void shouldSaveAndFindUrl() {
    // 測試儲存與查詢
}

@Test
void shouldFindByLongUrl() {
    // 測試長網址查詢
}

@Test
void shouldCheckShortCodeUniqueness() {
    // 測試短網址唯一性
}
```

### 2. Cache 層測試

**測試範圍**:
- 快取儲存與檢索
- TTL 機制驗證
- 快取清除功能
- 錯誤處理機制

**關鍵測試案例**:
```java
@Test
void shouldCacheAndRetrieveUrl() {
    // 測試基本快取功能
}

@Test
void shouldSupportCustomTtl() {
    // 測試自訂 TTL
}

@Test
void shouldHandleRedisFailureGracefully() {
    // 測試 Redis 故障處理
}
```

### 3. Use Case 層測試

**測試範圍**:
- 完整業務流程測試
- 資料庫與快取的協作
- 重複網址處理
- 錯誤處理流程

**關鍵測試案例**:
```java
@Test
void shouldCreateNewShortUrl() {
    // 測試建立新短網址的完整流程
}

@Test
void shouldReturnExistingShortUrlForDuplicateLongUrl() {
    // 測試重複網址處理
}
```

### 4. API 層測試

**測試範圍**:
- HTTP 端點測試
- 請求/回應格式驗證
- 錯誤狀態碼測試
- 重定向功能測試

**關鍵測試案例**:
```java
@Test
void shouldCreateShortUrl() {
    // POST /api/urls
}

@Test
void shouldGetUrlInfo() {
    // GET /api/urls/{code}
}

@Test
void shouldRedirectToLongUrl() {
    // GET /{code}
}
```

## 測試覆蓋率配置

### JaCoCo 設定

- **目標覆蓋率**: 80%
- **報告格式**: HTML + XML
- **覆蓋率檢查**: 在測試階段自動執行

### 分層覆蓋率目標

| 層級           | 目標覆蓋率 |
| -------------- | ---------- |
| Domain         | > 95%      |
| Application    | > 90%      |
| Infrastructure | > 80%      |
| Adapters       | > 85%      |
| **整體**       | **> 80%**  |

## 執行測試

### 前置需求

1. **Docker**: Testcontainers 需要 Docker 來運行測試容器
2. **Java 17+**: 專案使用 Java 17
3. **Maven 3.6+**: 建置工具

### 執行指令

```bash
# 執行所有測試
./mvnw test

# 執行整合測試
./mvnw test -Dtest="*IntegrationTest"

# 執行單元測試
./mvnw test -Dtest="*Test" -DexcludeGroups="integration"

# 執行特定測試類
./mvnw test -Dtest="UrlRepositoryIntegrationTest"

# 生成覆蓋率報告
./mvnw jacoco:report

# 檢查覆蓋率門檻
./mvnw jacoco:check

# 使用便利腳本
./scripts/run-tests.sh
```

### 測試報告

執行測試後，可在以下位置查看報告：

- **JaCoCo HTML 報告**: `target/site/jacoco/index.html`
- **JaCoCo XML 報告**: `target/site/jacoco/jacoco.xml`
- **Surefire 測試報告**: `target/surefire-reports/`

## 故障排除

### 常見問題

1. **Docker 連接問題**
   ```
   Could not find a valid Docker environment
   ```
   **解決**: 確保 Docker 已啟動且可連接

2. **容器啟動逾時**
   ```
   Container startup failed
   ```
   **解決**: 檢查 Docker 資源分配，增加記憶體或 CPU

3. **連接埠衝突**
   ```
   Address already in use
   ```
   **解決**: 確保沒有其他服務占用相同連接埠

4. **測試資料隔離問題**
   ```
   Test data contamination
   ```
   **解決**: 確保繼承 AbstractIntegrationTest 並在 @BeforeEach 中清理資料

### 最佳實務

1. **使用測試工廠**: 統一測試資料建立邏輯
2. **遵循測試命名**: `should[Expected]When[StateUnderTest]`
3. **測試隔離**: 每個測試都應該獨立運行
4. **資源清理**: 確保測試後清理資源
5. **有意義的斷言**: 使用描述性的斷言訊息

## 持續整合

### CI/CD 配置建議

```yaml
# GitHub Actions 範例
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    services:
      docker:
        image: docker:dind
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run tests
        run: ./mvnw test
      - name: Generate coverage report
        run: ./mvnw jacoco:report
      - name: Upload coverage
        uses: codecov/codecov-action@v3
```

## 效能考量

### 測試優化策略

1. **容器重用**: 使用 `.withReuse(true)` 避免重複啟動容器
2. **並行執行**: 配置 Maven Surefire 並行執行測試
3. **快速失敗**: 使用 `@DirtiesContext` 標記需要重置的測試
4. **資源分層**: 區分單元測試與整合測試的執行

### 監控指標

- 測試執行時間
- 容器啟動時間
- 記憶體使用量
- 測試覆蓋率趨勢

---

## 總結

這個整合測試套件提供了完整的測試覆蓋，確保 TinyURL API 服務的品質與可靠性。透過 Testcontainers 使用真實的資料庫與快取環境，讓測試更接近生產環境，提高了問題發現的準確性。

配合自動化的測試執行腳本與覆蓋率報告，開發團隊可以持續監控程式品質，及早發現並修復問題。

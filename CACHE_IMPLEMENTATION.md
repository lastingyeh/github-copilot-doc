# Redis 快取層實作說明

本專案已完成 Redis 快取層的實作，提供基於 Cache-aside Pattern 的高效能 URL 快取機制。

## 實作的功能

### 1. 核心組件

#### UrlCachePort 介面
- 定義快取操作的抽象介面
- 支援查詢、快取、清除等操作
- 提供快取統計功能

#### RedisUrlCacheService 實作
- 實作 Cache-aside Pattern
- 支援自動 TTL 調整（根據存取次數）
- 包含錯誤處理與容錯機制
- 提供 Micrometer 指標監控

#### CacheStatistics 統計
- 命中率、未命中率、錯誤率統計
- 快取大小與效能監控

### 2. 配置功能

#### RedisConfig
- Redis 連線池配置
- JSON 序列化配置
- 自動連線管理

#### CacheProperties
- 支援從 application.yml 讀取配置
- 可調整的 TTL 策略
- 熱門 URL 閾值設定

### 3. 快取策略

#### TTL 策略
- 預設快取時間：1 小時
- 熱門 URL（存取次數 > 100）：24 小時
- 支援自訂 TTL

#### 快取鍵值規範
- 前綴：`tinyurl:short:`
- 統計前綴：`tinyurl:stats:`
- 統一的命名規則

## 使用方式

### 1. 配置

在 `application.yml` 中：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms

tinyurl:
  cache:
    default-ttl: PT1H        # 1小時
    popular-ttl: PT24H       # 24小時
    popular-threshold: 100   # 熱門閾值
```

### 2. 在 Use Case 中使用

```java
@UseCase
public class GetLongUrlUseCase {

    private final UrlRepository urlRepository;
    private final UrlCachePort cachePort;

    public Optional<GetLongUrlResponse> execute(GetLongUrlCommand command) {
        ShortCode shortCode = new ShortCode(command.shortCode());

        // 1. 先查快取
        Optional<Url> cached = cachePort.findByShortCode(shortCode);
        if (cached.isPresent()) {
            return cached.map(this::toResponse);
        }

        // 2. 快取未命中，查資料庫
        Optional<Url> fromDb = urlRepository.findByShortCode(shortCode);
        if (fromDb.isPresent()) {
            // 3. 更新快取
            cachePort.cache(fromDb.get());
            return fromDb.map(this::toResponse);
        }

        return Optional.empty();
    }
}
```

### 3. 快取操作

```java
// 基本快取操作
cachePort.cache(url);                    // 使用預設 TTL
cachePort.cache(url, Duration.ofHours(2)); // 自訂 TTL
cachePort.evict(shortCode);              // 清除特定項目
cachePort.evictAll();                    // 清除所有快取

// 查詢操作
Optional<Url> cached = cachePort.findByShortCode(shortCode);

// 統計資訊
CacheStatistics stats = cachePort.getStatistics();
System.out.println("命中率: " + (stats.getHitRate() * 100) + "%");
```

## 監控與指標

### Micrometer 指標

快取服務自動產生以下指標：
- `tinyurl_cache_hits_total` - 快取命中次數
- `tinyurl_cache_misses_total` - 快取未命中次數
- `tinyurl_cache_errors_total` - 快取錯誤次數

### 健康檢查

可透過 Spring Boot Actuator 檢查 Redis 連線狀態：
```
GET /actuator/health
```

### 快取統計

```java
CacheStatistics stats = cacheService.getStatistics();
// 可獲得：命中率、快取大小、錯誤率等資訊
```

## 效能特性

### 快取命中率目標
- 熱門 URL 命中率 > 80%
- 快取回應時間 < 1ms
- 錯誤率 < 0.1%

### 容錯機制
- Redis 連線失敗時不影響主要業務流程
- 本地統計計數器防止統計資料丟失
- 自動錯誤重試與降級

### TTL 優化
- 根據存取頻率動態調整快取時間
- 熱門 URL 享有更長的快取時間
- 減少資料庫查詢負載

## 測試驗證

由於環境限制（Java 24 與 Mockito 相容性問題），建議進行以下手動驗證：

1. 啟動 Redis 服務
2. 啟動應用程式
3. 透過 API 呼叫驗證快取行為
4. 觀察日誌輸出確認快取操作
5. 檢查 Actuator 端點的指標資料

## 下一步整合

快取層已準備好與以下組件整合：
- Use Case 層（查詢與更新操作）
- REST Controller（API 回應加速）
- 資料庫層（減少查詢負載）
- 監控系統（效能指標收集）

快取層實作完成，符合 Clean Architecture 原則，可靈活替換不同的快取技術實作。

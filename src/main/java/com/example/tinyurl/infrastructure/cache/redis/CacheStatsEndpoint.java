package com.example.tinyurl.infrastructure.cache.redis;

import com.example.tinyurl.application.port.out.CacheStatistics;
import com.example.tinyurl.application.port.out.UrlCachePort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 快取統計端點
 *
 * 提供快取相關的監控資訊
 * 可透過 /api/cache-stats 端點存取
 */
@RestController
@RequestMapping("/api/cache-stats")
public class CacheStatsEndpoint {

    private final UrlCachePort cacheService;

    public CacheStatsEndpoint(UrlCachePort cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * 獲取快取統計資訊
     *
     * @return 包含詳細統計資訊的 Map
     */
    @GetMapping
    public Map<String, Object> cacheStats() {
        CacheStatistics stats = cacheService.getStatistics();

        Map<String, Object> result = new HashMap<>();
        result.put("hitCount", stats.getHitCount());
        result.put("missCount", stats.getMissCount());
        result.put("errorCount", stats.getErrorCount());
        result.put("hitRate", String.format("%.2f%%", stats.getHitRate() * 100));
        result.put("totalRequests", stats.getTotalRequests());
        result.put("size", stats.getSize());
        result.put("memoryUsage", formatBytes(stats.getMemoryUsage()));

        // 計算額外指標
        result.put("errorRate", calculateErrorRate(stats));
        result.put("efficiency", calculateEfficiency(stats));

        return result;
    }

    /**
     * 計算錯誤率
     */
    private String calculateErrorRate(CacheStatistics stats) {
        long total = stats.getTotalRequests() + stats.getErrorCount();
        if (total == 0)
            return "0.00%";

        double errorRate = (double) stats.getErrorCount() / total;
        return String.format("%.2f%%", errorRate * 100);
    }

    /**
     * 計算快取效率（命中率與錯誤率的綜合指標）
     */
    private String calculateEfficiency(CacheStatistics stats) {
        if (stats.getTotalRequests() == 0)
            return "N/A";

        double efficiency = stats.getHitRate() * (1 - (double) stats.getErrorCount() /
                (stats.getTotalRequests() + stats.getErrorCount()));
        return String.format("%.2f%%", efficiency * 100);
    }

    /**
     * 格式化位元組數為可讀格式
     */
    private String formatBytes(long bytes) {
        if (bytes == 0)
            return "0 B";

        String[] units = { "B", "KB", "MB", "GB" };
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        digitGroups = Math.min(digitGroups, units.length - 1);

        return String.format("%.1f %s",
                bytes / Math.pow(1024, digitGroups),
                units[digitGroups]);
    }
}
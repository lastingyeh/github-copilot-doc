#!/bin/bash

# 效能基準測試腳本
set -e

API_BASE_URL="http://localhost:8080"
CONCURRENT_USERS=10
DURATION=30

echo "=== TinyURL 效能基準測試 ==="

# 檢查 curl 是否可用
if ! command -v curl &> /dev/null; then
    echo "curl 未安裝，請先安裝 curl"
    exit 1
fi

# 建立測試資料檔案
cat > /tmp/create_url_payload.json << EOF
{"longUrl": "https://example.com/test-performance"}
EOF

echo "1. 單個建立短網址效能測試..."
START_TIME=$(date +%s.%N)
for i in {1..10}; do
    curl -s -X POST ${API_BASE_URL}/api/urls \
        -H "Content-Type: application/json" \
        -d @/tmp/create_url_payload.json > /dev/null
done
END_TIME=$(date +%s.%N)
ELAPSED=$(echo "$END_TIME - $START_TIME" | bc)
AVG_TIME=$(echo "scale=3; $ELAPSED / 10" | bc)
echo "✅ 10 次建立短網址平均時間: ${AVG_TIME}s"

# 建立一個測試短網址用於查詢測試
echo "2. 準備查詢測試資料..."
SHORT_CODE=$(curl -s -X POST ${API_BASE_URL}/api/urls \
    -H "Content-Type: application/json" \
    -d '{"longUrl": "https://example.com/perf-query"}' | jq -r '.short_code')

echo "3. 查詢短網址效能測試..."
START_TIME=$(date +%s.%N)
for i in {1..50}; do
    curl -s ${API_BASE_URL}/api/urls/${SHORT_CODE} > /dev/null
done
END_TIME=$(date +%s.%N)
ELAPSED=$(echo "$END_TIME - $START_TIME" | bc)
AVG_TIME=$(echo "scale=3; $ELAPSED / 50" | bc)
echo "✅ 50 次查詢短網址平均時間: ${AVG_TIME}s"

echo "4. 重定向效能測試..."
START_TIME=$(date +%s.%N)
for i in {1..50}; do
    curl -s -I ${API_BASE_URL}/${SHORT_CODE} > /dev/null
done
END_TIME=$(date +%s.%N)
ELAPSED=$(echo "$END_TIME - $START_TIME" | bc)
AVG_TIME=$(echo "scale=3; $ELAPSED / 50" | bc)
echo "✅ 50 次重定向平均時間: ${AVG_TIME}s"

echo "5. 並發建立短網址測試..."
# 使用 xargs 實現簡單的並發測試
seq 1 20 | xargs -n1 -P5 -I{} sh -c "
    curl -s -X POST ${API_BASE_URL}/api/urls \
        -H 'Content-Type: application/json' \
        -d '{\"longUrl\": \"https://example.com/concurrent-{}\"}' > /dev/null
"
echo "✅ 20 個並發建立請求完成"

echo "6. 檢查系統資源使用..."
echo "記憶體使用情況:"
curl -s ${API_BASE_URL}/actuator/metrics/jvm.memory.used | jq '.measurements[0].value' | xargs -I{} echo "JVM 記憶體使用: {} bytes"

echo "GC 資訊:"
curl -s ${API_BASE_URL}/actuator/metrics/jvm.gc.pause | jq '.measurements[0].value' | xargs -I{} echo "GC 暫停時間: {}s"

echo ""
echo "✅ 效能測試完成"
echo "📊 建議檢查指標："
echo "   - 建立短網址: < 100ms"
echo "   - 查詢短網址: < 10ms"
echo "   - 重定向請求: < 5ms"

# 清理
rm -f /tmp/create_url_payload.json

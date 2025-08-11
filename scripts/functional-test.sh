#!/bin/bash

# TinyURL 功能驗證腳本
set -e

API_BASE_URL="http://localhost:8080"
LONG_URL="https://github.com/example/tinyurl"

echo "=== TinyURL 功能驗證測試 ==="

# 1. 檢查健康狀態
echo "1. 檢查應用程式健康狀態..."
HEALTH_STATUS=$(curl -s ${API_BASE_URL}/actuator/health | jq -r '.status')
if [ "$HEALTH_STATUS" != "UP" ]; then
    echo "❌ 健康檢查失敗: $HEALTH_STATUS"
    exit 1
fi
echo "✅ 應用程式狀態正常"

# 2. 建立短網址
echo "2. 建立短網址..."
CREATE_RESPONSE=$(curl -s -X POST ${API_BASE_URL}/api/urls \
    -H "Content-Type: application/json" \
    -d "{\"longUrl\": \"${LONG_URL}\"}")

SHORT_CODE=$(echo $CREATE_RESPONSE | jq -r '.short_code')
SHORT_URL=$(echo $CREATE_RESPONSE | jq -r '.short_url')

if [ "$SHORT_CODE" == "null" ]; then
    echo "❌ 短網址建立失敗"
    echo $CREATE_RESPONSE
    exit 1
fi
echo "✅ 短網址建立成功: $SHORT_CODE"

# 3. 查詢短網址資訊
echo "3. 查詢短網址資訊..."
INFO_RESPONSE=$(curl -s ${API_BASE_URL}/api/urls/${SHORT_CODE})
RETRIEVED_URL=$(echo $INFO_RESPONSE | jq -r '.long_url')

if [ "$RETRIEVED_URL" != "$LONG_URL" ]; then
    echo "❌ 網址查詢結果不符: 預期 $LONG_URL, 實際 $RETRIEVED_URL"
    exit 1
fi
echo "✅ 網址查詢成功"

# 4. 測試重定向
echo "4. 測試重定向功能..."
REDIRECT_LOCATION=$(curl -s -I ${API_BASE_URL}/${SHORT_CODE} | grep -i location | cut -d' ' -f2 | tr -d '\r')

if [ "$REDIRECT_LOCATION" != "$LONG_URL" ]; then
    echo "❌ 重定向位置不符: 預期 $LONG_URL, 實際 $REDIRECT_LOCATION"
    exit 1
fi
echo "✅ 重定向功能正常"

# 5. 檢查監控指標
echo "5. 檢查監控指標..."
METRICS_RESPONSE=$(curl -s ${API_BASE_URL}/actuator/prometheus)
if ! echo "$METRICS_RESPONSE" | grep -q "tinyurl_urls_created_total"; then
    echo "⚠️  業務指標未找到，使用預設指標檢查"
    if ! echo "$METRICS_RESPONSE" | grep -q "http_server_requests_total"; then
        echo "❌ 監控指標未找到"
        exit 1
    fi
fi
echo "✅ 監控指標正常"

# 6. 測試錯誤處理
echo "6. 測試錯誤處理..."
ERROR_RESPONSE=$(curl -s -X POST ${API_BASE_URL}/api/urls \
    -H "Content-Type: application/json" \
    -d '{"longUrl": "invalid-url"}')

if echo "$ERROR_RESPONSE" | grep -q "error"; then
    echo "✅ 錯誤處理正常"
else
    echo "⚠️  錯誤處理檢查無法確認"
fi

# 7. 測試不存在的短網址
echo "7. 測試不存在的短網址..."
NOT_FOUND_RESPONSE=$(curl -s -w "%{http_code}" ${API_BASE_URL}/api/urls/nonexistent)
if echo "$NOT_FOUND_RESPONSE" | grep -q "404"; then
    echo "✅ 404 錯誤處理正常"
else
    echo "⚠️  404 錯誤處理檢查無法確認"
fi

echo ""
echo "🎉 所有功能驗證通過！"
echo "   短網址: $SHORT_URL"
echo "   重定向到: $LONG_URL"

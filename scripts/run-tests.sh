#!/bin/bash

# TinyURL API 整合測試執行腳本
#
# 此腳本演示如何執行不同類型的測試以及生成覆蓋率報告

echo "🚀 TinyURL API 測試執行腳本"
echo "================================"

# 檢查 Docker 是否運行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未運行或無法連接。Testcontainers 需要 Docker 來運行整合測試。"
    echo "請啟動 Docker 後重新執行。"
    exit 1
fi

echo "✅ Docker 已運行"

# 1. 執行所有單元測試（不需要 Docker）
echo ""
echo "📋 步驟 1: 執行單元測試"
echo "------------------------"
./mvnw test -Dtest="*Test" -DexcludeGroups="integration" || {
    echo "❌ 單元測試失敗"
    exit 1
}

# 2. 執行 Repository 層整合測試
echo ""
echo "📋 步驟 2: 執行 Repository 層整合測試"
echo "------------------------------------"
./mvnw test -Dtest="*RepositoryIntegrationTest" || {
    echo "⚠️  Repository 整合測試失敗，繼續執行其他測試"
}

# 3. 執行 Cache 層整合測試
echo ""
echo "📋 步驟 3: 執行 Cache 層整合測試"
echo "------------------------------"
./mvnw test -Dtest="*CacheServiceIntegrationTest" || {
    echo "⚠️  Cache 整合測試失敗，繼續執行其他測試"
}

# 4. 執行 Use Case 整合測試
echo ""
echo "📋 步驟 4: 執行 Use Case 整合測試"
echo "-------------------------------"
./mvnw test -Dtest="*UseCaseIntegrationTest" || {
    echo "⚠️  Use Case 整合測試失敗，繼續執行其他測試"
}

# 5. 執行 API 控制器整合測試
echo ""
echo "📋 步驟 5: 執行 API 控制器整合測試"
echo "--------------------------------"
./mvnw test -Dtest="*ControllerIntegrationTest" || {
    echo "⚠️  API 控制器整合測試失敗，繼續執行其他測試"
}

# 6. 執行所有整合測試
echo ""
echo "📋 步驟 6: 執行所有整合測試"
echo "--------------------------"
./mvnw test -Dtest="*IntegrationTest" || {
    echo "⚠️  部分整合測試失敗，檢查測試覆蓋率報告獲取詳細資訊"
}

# 7. 生成測試覆蓋率報告
echo ""
echo "📋 步驟 7: 生成測試覆蓋率報告"
echo "----------------------------"
./mvnw jacoco:report || {
    echo "❌ 覆蓋率報告生成失敗"
    exit 1
}

# 8. 檢查覆蓋率門檻
echo ""
echo "📋 步驟 8: 檢查覆蓋率門檻"
echo "------------------------"
./mvnw jacoco:check || {
    echo "⚠️  覆蓋率未達到 80% 門檻"
}

echo ""
echo "🎉 測試執行完成！"
echo "==================="
echo ""
echo "📊 查看測試覆蓋率報告:"
echo "   - HTML 報告: target/site/jacoco/index.html"
echo "   - XML 報告:  target/site/jacoco/jacoco.xml"
echo ""
echo "🔍 有用的測試指令:"
echo "   - 執行所有測試:           ./mvnw test"
echo "   - 執行單元測試:           ./mvnw test -Dtest=\"*Test\" -DexcludeGroups=\"integration\""
echo "   - 執行整合測試:           ./mvnw test -Dtest=\"*IntegrationTest\""
echo "   - 執行特定測試類:         ./mvnw test -Dtest=\"ClassName\""
echo "   - 執行特定測試方法:       ./mvnw test -Dtest=\"ClassName#methodName\""
echo "   - 生成覆蓋率報告:         ./mvnw jacoco:report"
echo "   - 檢查覆蓋率門檻:         ./mvnw jacoco:check"
echo ""
echo "📋 測試類型說明:"
echo "   - *Test.java              單元測試"
echo "   - *IntegrationTest.java   整合測試（使用 Testcontainers）"
echo "   - AbstractIntegrationTest 整合測試基類"
echo ""

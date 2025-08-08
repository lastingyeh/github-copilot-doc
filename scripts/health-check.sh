#!/bin/bash

# TinyURL Docker 環境健康檢查腳本

echo "🔍 檢查 TinyURL Docker 環境健康狀態..."
echo "=================================="

# 顏色定義
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 檢查函數
check_service() {
    local service_name=$1
    local check_command=$2
    local expected_response=$3

    echo -n "檢查 $service_name... "

    if eval "$check_command" > /dev/null 2>&1; then
        if [ -n "$expected_response" ]; then
            response=$(eval "$check_command" 2>/dev/null)
            if [[ "$response" == *"$expected_response"* ]]; then
                echo -e "${GREEN}✓ 健康${NC}"
                return 0
            else
                echo -e "${RED}✗ 回應異常${NC}"
                return 1
            fi
        else
            echo -e "${GREEN}✓ 健康${NC}"
            return 0
        fi
    else
        echo -e "${RED}✗ 失敗${NC}"
        return 1
    fi
}

# 檢查 Docker Compose 狀態
echo "📦 Docker Compose 服務狀態："
docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
echo

# 檢查各個服務
echo "🔍 服務健康檢查："

check_service "PostgreSQL" "docker-compose exec -T postgres pg_isready -U tinyurl_user -d tinyurl_db" "accepting connections"
POSTGRES_STATUS=$?

check_service "Redis" "docker-compose exec -T redis redis-cli ping" "PONG"
REDIS_STATUS=$?

check_service "Prometheus" "curl -s http://localhost:9090/-/healthy" "Healthy"
PROMETHEUS_STATUS=$?

check_service "Grafana" "curl -s http://localhost:3000/api/health" "ok"
GRAFANA_STATUS=$?

echo
echo "=================================="

# 總結 (0表示成功，1表示失敗)
TOTAL_FAILED=$((POSTGRES_STATUS + REDIS_STATUS + PROMETHEUS_STATUS + GRAFANA_STATUS))

if [ $TOTAL_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 所有服務都正常運行！${NC}"
    echo
    echo "📊 服務存取點："
    echo "• PostgreSQL: localhost:5432 (tinyurl_user/tinyurl_pass)"
    echo "• Redis: localhost:6379"
    echo "• Prometheus: http://localhost:9090"
    echo "• Grafana: http://localhost:3000 (admin/admin123)"
    exit 0
else
    echo -e "${RED}❌ 有 $TOTAL_FAILED 個服務出現問題${NC}"
    echo
    echo "🔧 故障排除建議："
    echo "1. 檢查服務日誌: docker-compose logs [service-name]"
    echo "2. 重啟服務: docker-compose restart [service-name]"
    echo "3. 檢查埠號衝突: lsof -i :[port]"
    exit 1
fi

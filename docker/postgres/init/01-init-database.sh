#!/bin/bash
# PostgreSQL 初始化腳本
# 此腳本會在 PostgreSQL 容器首次啟動時執行

set -e

# 建立資料庫使用者（如果不存在）
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- 確保資料庫使用 UTF-8 編碼
    ALTER DATABASE $POSTGRES_DB SET timezone TO 'Asia/Taipei';

    -- 建立必要的擴展套件
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";

    -- 建立應用程式專用的 schema
    CREATE SCHEMA IF NOT EXISTS tinyurl;

    -- 設定預設的搜尋路徑
    ALTER DATABASE $POSTGRES_DB SET search_path TO tinyurl, public;

    -- 建立用於統計的 schema
    CREATE SCHEMA IF NOT EXISTS statistics;

    -- 授予必要的權限
    GRANT ALL PRIVILEGES ON SCHEMA tinyurl TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON SCHEMA statistics TO $POSTGRES_USER;

    -- 建立用於監控的角色（生產環境使用）
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'monitoring') THEN
            CREATE ROLE monitoring WITH LOGIN;
            GRANT CONNECT ON DATABASE $POSTGRES_DB TO monitoring;
            GRANT USAGE ON SCHEMA tinyurl TO monitoring;
            GRANT USAGE ON SCHEMA statistics TO monitoring;
        END IF;
    END
    \$\$;
EOSQL

echo "PostgreSQL 初始化完成"
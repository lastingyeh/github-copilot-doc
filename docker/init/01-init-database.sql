-- TinyURL 資料庫初始化腳本
-- 此腳本會在 PostgreSQL 容器首次啟動時執行

-- 建立擴展（如果需要）
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 建立應用程式使用者（如果尚未存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'tinyurl_app') THEN
        CREATE ROLE tinyurl_app WITH LOGIN PASSWORD 'app_password';
    END IF;
END
$$;

-- 授予必要權限
GRANT CONNECT ON DATABASE tinyurl_db TO tinyurl_app;
GRANT USAGE ON SCHEMA public TO tinyurl_app;
GRANT CREATE ON SCHEMA public TO tinyurl_app;

-- 建立基本表格結構（將由 Flyway 管理，這裡僅作示範）
-- 實際的 schema 建立將在 Spring Boot 應用程式中透過 Flyway 進行

COMMENT ON DATABASE tinyurl_db IS 'TinyURL 短網址服務資料庫';

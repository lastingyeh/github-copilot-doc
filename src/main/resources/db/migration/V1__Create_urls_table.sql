-- 建立 urls 表格
-- TinyURL 短網址服務的主要資料表，儲存長網址與短網址的映射關係

CREATE TABLE urls (
    -- 短網址碼作為主鍵，支援 6-8 位 Base62 編碼
    short_code VARCHAR(8) NOT NULL PRIMARY KEY,

    -- 原始長網址，最大長度 2048 字符
    long_url VARCHAR(2048) NOT NULL,

    -- 長網址的 SHA-256 雜湊值，用於快速查詢重複 URL
    long_url_hash VARCHAR(64) NOT NULL,

    -- 建立時間戳，不可為空
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 最後存取時間戳，可為空表示從未被存取
    accessed_at TIMESTAMP,

    -- 存取次數，預設為 0，不可為負數
    access_count INTEGER NOT NULL DEFAULT 0
);

-- 建立索引以提升查詢效能

-- 1. 長網址雜湊索引 - 用於檢查重複 URL
CREATE INDEX idx_urls_long_url_hash ON urls(long_url_hash);

-- 2. 建立時間索引 - 用於統計和時間範圍查詢
CREATE INDEX idx_urls_created_at ON urls(created_at);

-- 3. 存取次數索引（降序）- 用於熱門 URL 查詢
CREATE INDEX idx_urls_access_count ON urls(access_count DESC);

-- 4. 複合索引：建立時間 + 存取次數 - 用於複合查詢優化
CREATE INDEX idx_urls_created_access ON urls(created_at, access_count);

-- 建立約束條件

-- 1. 短網址碼格式約束：只允許 Base62 字符，長度 6-8 位
ALTER TABLE urls ADD CONSTRAINT chk_short_code_format
    CHECK (short_code ~ '^[a-zA-Z0-9]{6,8}$');

-- 2. 存取次數必須為非負數
ALTER TABLE urls ADD CONSTRAINT chk_access_count_positive
    CHECK (access_count >= 0);

-- 3. 長網址不能為空字符串
ALTER TABLE urls ADD CONSTRAINT chk_long_url_not_empty
    CHECK (length(trim(long_url)) > 0);

-- 4. 雜湊值不能為空字符串
ALTER TABLE urls ADD CONSTRAINT chk_long_url_hash_not_empty
    CHECK (length(trim(long_url_hash)) > 0);

-- 新增註解說明
COMMENT ON TABLE urls IS '短網址映射表：儲存長網址與短網址的對應關係';
COMMENT ON COLUMN urls.short_code IS '短網址碼：6-8位Base62編碼的唯一識別碼';
COMMENT ON COLUMN urls.long_url IS '原始長網址：用戶提供的完整URL地址';
COMMENT ON COLUMN urls.long_url_hash IS '長網址SHA-256雜湊：用於快速查詢重複URL';
COMMENT ON COLUMN urls.created_at IS '建立時間：URL映射的建立時間戳';
COMMENT ON COLUMN urls.accessed_at IS '最後存取時間：短網址最後一次被存取的時間';
COMMENT ON COLUMN urls.access_count IS '存取次數：短網址被存取的總次數';

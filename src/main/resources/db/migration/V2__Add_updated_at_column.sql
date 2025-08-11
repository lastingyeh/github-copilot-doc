-- 新增 updated_at 欄位到 urls 表格
-- 這個欄位用於追蹤記錄的最後更新時間

ALTER TABLE urls ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 更新現有記錄的 updated_at 值為 created_at 的值
UPDATE urls SET updated_at = created_at;

-- 建立 updated_at 欄位的索引以提升查詢效能
CREATE INDEX idx_urls_updated_at ON urls(updated_at);

-- ============================================================
-- Таблица файлов (исправленная, без дублирования)
-- ============================================================
CREATE TABLE IF NOT EXISTS files (
    id SERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    file_name TEXT NOT NULL,
    file_url TEXT NOT NULL,
    storage_key TEXT,                     -- ключ объекта в S3 (для удаления)
    file_type VARCHAR(255),
    file_size BIGINT,
    uploaded_by_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_files_entity ON files(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_files_uploaded_by ON files(uploaded_by_id);
CREATE INDEX IF NOT EXISTS idx_files_storage_key ON files(storage_key);

-- Триггер для автоматического обновления updated_at
CREATE TRIGGER trg_update_files_updated_at
BEFORE UPDATE ON files
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
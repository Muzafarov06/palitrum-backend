-- Учебные периоды (семестры, четверти, годы)
CREATE TABLE IF NOT EXISTS academic_period (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,             -- "Осенний семестр 2025"
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    period_type VARCHAR(20) NOT NULL CHECK (period_type IN ('SEMESTER', 'QUARTER', 'YEAR')),
    is_current BOOLEAN DEFAULT FALSE,       -- текущий период (для быстрых запросов)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_academic_period_current ON academic_period(is_current) WHERE is_current = TRUE;
CREATE INDEX idx_academic_period_dates ON academic_period(start_date, end_date);

CREATE TRIGGER trg_update_academic_period_updated_at
BEFORE UPDATE ON academic_period
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
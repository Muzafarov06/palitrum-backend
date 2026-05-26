CREATE TABLE IF NOT EXISTS system_settings (
    id INT PRIMARY KEY,
    settings JSONB NOT NULL DEFAULT '{}',
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO system_settings (id, settings)
VALUES (1, '{}')
ON CONFLICT (id) DO NOTHING;
CREATE TABLE IF NOT EXISTS position (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,                     -- "Преподаватель фортепиано", "Концертмейстер", "Теоретик"
    hours_per_rate DECIMAL(5,2) NOT NULL DEFAULT 24, -- часов на 1 ставку (обычно 18 или 24)
    is_teaching BOOLEAN DEFAULT TRUE,               -- учебная нагрузка или административная
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);



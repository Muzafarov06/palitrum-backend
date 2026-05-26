CREATE TABLE IF NOT EXISTS staff (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,                 -- связь с users
    position_id BIGINT NOT NULL,                    -- должность
    rate_count DECIMAL(4,2) NOT NULL DEFAULT 1.0,  -- количество ставок (1.0, 0.5, 1.25...)
    hire_date DATE NOT NULL,                        -- дата приёма на работу
    dismissal_date DATE,                            -- дата увольнения (null = работает)
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_staff_position FOREIGN KEY (position_id) REFERENCES position(id) ON DELETE RESTRICT
);

-- ============================
-- Таблица заявок (applications) – исправленная версия
-- ============================
CREATE TABLE IF NOT EXISTS applications (
    id SERIAL PRIMARY KEY,

    -- ---------- Данные ребёнка ----------
    child_last_name TEXT NOT NULL,
    child_first_name TEXT NOT NULL,
    child_middle_name TEXT,
    child_birth_date DATE NOT NULL,
    child_birth_place TEXT,
    child_citizenship TEXT,
    child_address TEXT,
    child_snils TEXT,
    child_individual_plan BOOLEAN DEFAULT FALSE,
    child_last_school TEXT,
    child_grade_level VARCHAR(20),

    -- ---------- Данные родителя ----------
    parent_last_name TEXT NOT NULL,
    parent_first_name TEXT NOT NULL,
    parent_middle_name TEXT,
    parent_relation VARCHAR(20) NOT NULL DEFAULT 'MOTHER',
    parent_phone TEXT NOT NULL,
    parent_email TEXT NOT NULL,

    -- ---------- Программы ----------
    preferred_program_id BIGINT REFERENCES programs(id) ON DELETE SET NULL,
    final_program_id BIGINT REFERENCES programs(id) ON DELETE SET NULL,

    -- ---------- Согласия ----------
    consent_personal_data BOOLEAN NOT NULL DEFAULT FALSE,
    consent_photo_video BOOLEAN NOT NULL DEFAULT FALSE,
    consent_medical_intervention BOOLEAN NOT NULL DEFAULT FALSE,

    -- ---------- Дополнительная информация ----------
    additional_info TEXT,

    -- ---------- Статус и обработка ----------
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    rejection_reason TEXT,
    assigned_officer_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    source VARCHAR(20) NOT NULL DEFAULT 'SITE',

    -- ---------- Временные метки процесса ----------
    submitted_at TIMESTAMP WITH TIME ZONE,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    decision_at TIMESTAMP WITH TIME ZONE,
    enrollment_date DATE,
    waitlist_expiry_date DATE,

    -- ---------- Комментарии и история ----------
    internal_notes TEXT,
    officer_comment TEXT,
    history_changes JSONB,

    -- ---------- Связь с созданными пользователями ----------
    child_user_id BIGINT,
    parent_user_id BIGINT,

    -- ---------- Системные поля ----------
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- ---------- Внешние ключи ----------
    CONSTRAINT fk_applications_child_user FOREIGN KEY (child_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_applications_parent_user FOREIGN KEY (parent_user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_applications_status ON applications(status);
CREATE INDEX IF NOT EXISTS idx_applications_child_snils ON applications(child_snils);
CREATE INDEX IF NOT EXISTS idx_applications_parent_email ON applications(parent_email);
CREATE INDEX IF NOT EXISTS idx_applications_preferred_program ON applications(preferred_program_id);
CREATE INDEX IF NOT EXISTS idx_applications_final_program ON applications(final_program_id);
CREATE INDEX IF NOT EXISTS idx_applications_assigned_officer ON applications(assigned_officer_id);
CREATE INDEX IF NOT EXISTS idx_applications_child_name ON applications(child_last_name);
CREATE INDEX IF NOT EXISTS idx_applications_submitted_at ON applications(submitted_at);
CREATE INDEX IF NOT EXISTS idx_applications_enrollment_date ON applications(enrollment_date);
CREATE INDEX IF NOT EXISTS idx_applications_child_user ON applications(child_user_id);
CREATE INDEX IF NOT EXISTS idx_applications_parent_user ON applications(parent_user_id);

-- Триггер обновления updated_at
CREATE OR REPLACE FUNCTION update_applications_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_applications_update ON applications;
CREATE TRIGGER trg_applications_update
BEFORE UPDATE ON applications
FOR EACH ROW
EXECUTE FUNCTION update_applications_timestamp();
-- Нагрузка преподавателя на учебный период
CREATE TABLE IF NOT EXISTS teacher_load (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    academic_period_id BIGINT NOT NULL,
    staff_id BIGINT,                              -- связь с записью о трудоустройстве
    weekly_hours_planned DECIMAL(5,2) NOT NULL,   -- плановая нагрузка (часы/нед)
    max_weekly_hours DECIMAL(5,2),                -- максимально допустимая нагрузка
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_teacher_load_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_teacher_load_period FOREIGN KEY (academic_period_id) REFERENCES academic_period(id) ON DELETE CASCADE,
    CONSTRAINT fk_teacher_load_staff FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE SET NULL,
    UNIQUE(teacher_id, academic_period_id)
);

CREATE INDEX idx_teacher_load_teacher ON teacher_load(teacher_id);
CREATE INDEX idx_teacher_load_period ON teacher_load(academic_period_id);
CREATE INDEX idx_teacher_load_staff ON teacher_load(staff_id);

CREATE TRIGGER trg_update_teacher_load_updated_at
BEFORE UPDATE ON teacher_load
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();



-- Доступное время преподавателя (слоты)
CREATE TABLE IF NOT EXISTS teacher_availability (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    academic_period_id BIGINT NOT NULL,
    day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7), -- 1 = понедельник, 7 = воскресенье
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_recurring BOOLEAN DEFAULT TRUE,      -- повторяется каждую неделю в этом периоде
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_teacher_availability_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_teacher_availability_period FOREIGN KEY (academic_period_id) REFERENCES academic_period(id) ON DELETE CASCADE
);

CREATE INDEX idx_teacher_availability_teacher ON teacher_availability(teacher_id);
CREATE INDEX idx_teacher_availability_period ON teacher_availability(academic_period_id);
CREATE INDEX idx_teacher_availability_day ON teacher_availability(day_of_week);

CREATE TRIGGER trg_update_teacher_availability_updated_at
BEFORE UPDATE ON teacher_availability
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
-- Шаблоны расписания (для автоматической генерации уроков)
CREATE TABLE IF NOT EXISTS schedule_template (
    id BIGSERIAL PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    group_id BIGINT,                       -- NULL для индивидуальных занятий
    student_id BIGINT,                     -- NULL для групповых
    teacher_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time TIME NOT NULL,
    duration_minutes INT NOT NULL,
    academic_period_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_template_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT fk_template_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE SET NULL,
    CONSTRAINT fk_template_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_template_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_template_room FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE CASCADE,
    CONSTRAINT fk_template_period FOREIGN KEY (academic_period_id) REFERENCES academic_period(id) ON DELETE CASCADE,
    CHECK ((group_id IS NOT NULL AND student_id IS NULL) OR (group_id IS NULL AND student_id IS NOT NULL))
);

CREATE INDEX idx_schedule_template_subject ON schedule_template(subject_id);
CREATE INDEX idx_schedule_template_teacher ON schedule_template(teacher_id);
CREATE INDEX idx_schedule_template_period ON schedule_template(academic_period_id);
CREATE INDEX idx_schedule_template_day_time ON schedule_template(day_of_week, start_time);

CREATE TRIGGER trg_update_schedule_template_updated_at
BEFORE UPDATE ON schedule_template
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
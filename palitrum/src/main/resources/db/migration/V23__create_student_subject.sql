-- Изучаемые предметы студента (с нагрузкой и преподавателем)
CREATE TABLE IF NOT EXISTS student_subject (
    id BIGSERIAL PRIMARY KEY,
    student_program_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    teacher_id BIGINT,                      -- закреплённый преподаватель (для индивид. занятий)
    planned_hours_per_week DECIMAL(4,1) NOT NULL,  -- плановая нагрузка (часы в неделю)
    is_group_lesson BOOLEAN DEFAULT TRUE,   -- TRUE = групповое занятие (через group_id), FALSE = индивид.
    group_id BIGINT,                        -- если групповое – ссылка на группу
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_subject_program FOREIGN KEY (student_program_id) REFERENCES student_program(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_subject_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_subject_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_student_subject_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE SET NULL,
    UNIQUE(student_program_id, subject_id)
);

CREATE INDEX idx_student_subject_program ON student_subject(student_program_id);
CREATE INDEX idx_student_subject_subject ON student_subject(subject_id);
CREATE INDEX idx_student_subject_teacher ON student_subject(teacher_id);

CREATE TRIGGER trg_update_student_subject_updated_at
BEFORE UPDATE ON student_subject
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
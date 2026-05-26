-- Связь преподавателя с программой (назначение)
CREATE TABLE IF NOT EXISTS program_teacher (
    id BIGSERIAL PRIMARY KEY,
    program_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_program_teacher_program FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE CASCADE,
    CONSTRAINT fk_program_teacher_teacher FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(program_id, teacher_id)
);

CREATE INDEX idx_program_teacher_program ON program_teacher(program_id);
CREATE INDEX idx_program_teacher_teacher ON program_teacher(teacher_id);

CREATE TRIGGER trg_update_program_teacher_updated_at
BEFORE UPDATE ON program_teacher
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
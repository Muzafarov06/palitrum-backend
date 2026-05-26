-- Назначение студента на программу (поддержка нескольких программ)
CREATE TABLE IF NOT EXISTS student_program (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    program_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    graduation_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED' CHECK (status IN ('ENROLLED', 'GRADUATED', 'DROPPED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_program_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_program_program FOREIGN KEY (program_id) REFERENCES programs(id) ON DELETE CASCADE,
    UNIQUE(student_id, program_id, enrollment_date)
);

CREATE INDEX idx_student_program_student ON student_program(student_id);
CREATE INDEX idx_student_program_program ON student_program(program_id);
CREATE INDEX idx_student_program_status ON student_program(status);

CREATE TRIGGER trg_update_student_program_updated_at
BEFORE UPDATE ON student_program
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
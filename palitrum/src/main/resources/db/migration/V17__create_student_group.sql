CREATE TABLE student_group (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    enrolled_date DATE NOT NULL,
    left_date DATE,
    enrollment_status VARCHAR(50) NOT NULL
        CHECK (enrollment_status IN ('ENROLLED', 'GRADUATED', 'EXPELLED', 'TRANSFERRED')),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    source_application_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_application FOREIGN KEY (source_application_id) REFERENCES applications(id) ON DELETE SET NULL
);

CREATE TRIGGER trg_update_student_group_updated_at
BEFORE UPDATE ON student_group
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
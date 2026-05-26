CREATE TABLE IF NOT EXISTS user_relations (
    id BIGSERIAL PRIMARY KEY,
    parent_user_id BIGINT NOT NULL,
    child_user_id BIGINT NOT NULL,
    relation_type VARCHAR(20) NOT NULL CHECK (relation_type IN ('parent', 'guardian')),
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent_user FOREIGN KEY (parent_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_child_user FOREIGN KEY (child_user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_update_user_relations_updated_at
BEFORE UPDATE ON user_relations
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
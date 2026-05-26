    -- Журнал действий (AuditLog)
    CREATE TABLE IF NOT EXISTS audit_log (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT,                         -- кто выполнил действие (может быть NULL для системных)
        action VARCHAR(100) NOT NULL,           -- например, 'LOGIN', 'CREATE_USER', 'UPDATE_APPLICATION'
        entity_type VARCHAR(50),                -- имя сущности (User, Application, Lesson и т.д.)
        entity_id BIGINT,                       -- ID записи, над которой совершено действие
        old_state JSONB,                        -- предыдущее состояние (для отслеживания изменений)
        new_state JSONB,                        -- новое состояние
        ip_address INET,                        -- IP-адрес пользователя (если есть)
        user_agent TEXT,                        -- браузер/клиент
        created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

    CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
    CREATE INDEX idx_audit_log_action ON audit_log(action);
    CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
    CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);

    -- Триггер обновления updated_at
    CREATE TRIGGER trg_update_audit_log_updated_at
    BEFORE UPDATE ON audit_log
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
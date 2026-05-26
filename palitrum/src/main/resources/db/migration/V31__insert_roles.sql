INSERT INTO roles (name, description, is_system)
VALUES
    ('SUPER_ADMIN', 'Суперпользователь, доступ ко всему', TRUE),
    ('MANAGER',     'Менеджер – полное управление учебным процессом', FALSE),
    ('TEACHER',     'Преподаватель', FALSE),
    ('STUDENT',     'Ученик', FALSE),
    ('PARENT',      'Родитель', FALSE)
ON CONFLICT (name) DO NOTHING;
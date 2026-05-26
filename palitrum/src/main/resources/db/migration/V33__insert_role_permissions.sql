-- ============================================================
-- V22__insert_role_permissions.sql
-- Назначение прав для ролей: SUPER_ADMIN, MANAGER, TEACHER, STUDENT, PARENT
-- ============================================================

-- 1. SUPER_ADMIN: все существующие права (включая добавленные позже)
INSERT INTO role_permission (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'SUPER_ADMIN'), p.id
FROM permission p
ON CONFLICT DO NOTHING;

-- 2. MANAGER: полный доступ ко всем функциям управления учебным процессом
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permission p
WHERE r.name = 'MANAGER'
  AND p.code IN (
    -- Пользователи и связи
    'user.view', 'user.view_details', 'user.create', 'user.update', 'user.delete',
    'user.block', 'user.unblock', 'user.reset_password',
    'user_relations.view', 'user_relations.create', 'user_relations.delete',

    -- Роли и права
    'role.view', 'role.assign', 'role.revoke',

    -- Отделения, программы, предметы
    'department.view', 'department.create', 'department.update', 'department.delete',
    'program.view', 'program.create', 'program.update', 'program.delete',
    'program_department.assign', 'program_department.unassign',
    'subject.view', 'subject.create', 'subject.update', 'subject.delete',
    'program_subject.assign', 'program_subject.update', 'program_subject.unassign',

    -- Группы и зачисления
    'group.view', 'group.create', 'group.update', 'group.delete',
    'group.assign_teacher',
    'student_group.add', 'student_group.update', 'student_group.remove',
    'program_teacher.assign', 'program_teacher.update', 'program_teacher.remove',

    -- Помещения
    'room.view', 'room.create', 'room.update', 'room.delete',

    -- Расписание и занятия
    'lesson.view', 'lesson.view_details', 'lesson.view_all',
    'lesson.create', 'lesson.update', 'lesson.reassign_teacher', 'lesson.cancel', 'lesson.delete',

    -- Посещаемость и оценки
    'attendance.view', 'attendance.mark', 'attendance.view_all',
    'grade.view', 'grade.set', 'grade.update', 'grade.delete', 'grade.view_all',

    -- Заявки
    'application.view', 'application.create', 'application.update',
    'application.assign_exam', 'application.accept', 'application.reject', 'application.delete',

    -- Новости
    'news.view', 'news.create', 'news.update', 'news.delete', 'news.pin',

    -- Файлы
    'file.view', 'file.upload', 'file.delete',

    -- Отчёты
    'report.generate', 'report.download', 'report.view_all',

    -- Учебные периоды
    'academic_period.view', 'academic_period.create', 'academic_period.update', 'academic_period.delete',

    -- Индивидуальное обучение и нагрузки
    'student_program.view', 'student_program.create', 'student_program.update', 'student_program.delete',
    'student_subject.view', 'student_subject.create', 'student_subject.update', 'student_subject.delete',
    'teacher_load.view', 'teacher_load.create', 'teacher_load.update', 'teacher_load.delete',
    'schedule_template.view', 'schedule_template.create', 'schedule_template.update',
    'schedule_template.delete', 'schedule_template.generate',

    -- Штатное расписание и должности
    'position.view', 'position.create', 'position.update', 'position.delete',
    'staff.view', 'staff.create', 'staff.update', 'staff.delete',

    -- Домашние задания
    'homework.view', 'homework.create', 'homework.update', 'homework.delete', 'homework.submit',

    -- Уведомления
    'notification.send', 'notification.view',

    -- Платежи
    'payment.view', 'payment.create', 'payment.update', 'payment.delete',

    -- Аудит
    'audit.view',

    -- Импорт/экспорт
    'import.data', 'export.data',

    -- Дашборды
    'dashboard.view',

    -- Системные настройки
    'system.settings.view'
)
ON CONFLICT DO NOTHING;

-- 3. TEACHER: свои уроки, посещаемость, оценки, домашние задания, уведомления,
--    а также просмотр групп, своей нагрузки, отделений, программ, новостей, должностей и штата
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permission p
WHERE r.name = 'TEACHER'
  AND p.code IN (
    -- Уроки
    'lesson.view', 'lesson.view_details',
    -- Посещаемость
    'attendance.view', 'attendance.mark',
    -- Оценки
    'grade.view', 'grade.set', 'grade.update', 'grade.delete',
    -- Домашние задания
    'homework.view', 'homework.create', 'homework.update', 'homework.delete',
    -- Уведомления
    'notification.view', 'notification.send',
    -- Группы (для просмотра своих групп)
    'group.view',
    -- Нагрузка преподавателя
    'teacher_load.view',
    -- Штатное расписание (просмотр)
    'position.view', 'staff.view',
    -- Общешкольная информация (просмотр)
    'department.view', 'program.view', 'news.view'
)
ON CONFLICT DO NOTHING;

-- 4. STUDENT: просмотр своего расписания, оценок, посещаемости, домашних заданий,
--    уведомлений, а также общешкольной информации и оплаты
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permission p
WHERE r.name = 'STUDENT'
  AND p.code IN (
    'lesson.view', 'grade.view', 'attendance.view',
    'homework.view', 'homework.submit', 'notification.view',
    'department.view', 'program.view', 'news.view',
    'payment.view'
)
ON CONFLICT DO NOTHING;

-- 5. PARENT: просмотр данных своих детей (через user_relations), оценок, посещаемости,
--    домашних заданий, уведомлений, а также общешкольной информации и оплаты
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permission p
WHERE r.name = 'PARENT'
  AND p.code IN (
    'user.view_details', 'grade.view', 'attendance.view', 'homework.view', 'notification.view',
    'department.view', 'program.view', 'news.view',
    'payment.view'
)
ON CONFLICT DO NOTHING;
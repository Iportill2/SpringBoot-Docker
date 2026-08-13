/* ===== ROLES (se mantienen los 3) ===== */
INSERT IGNORE INTO roles (id, name) VALUES
  (1, 'EMPLEADO'),
  (2, 'ADMIN'),
  (3, 'PENDIENTE');

/* ===== PREGUNTAS DE SEGURIDAD (20) ===== */
INSERT IGNORE INTO questions (id, text) VALUES
  (1, '¿Cuál es el nombre de tu mascota?'),
  (2, '¿Cuál es tu ciudad de nacimiento?'),
  (3, '¿Cómo se llamaba tu primer profesor?'),
  (4, '¿Cuál es tu comida favorita?'),
  (5, '¿Cómo se llama tu calle?'),
  (6, '¿Cuál es tu color favorito?'),
  (7, '¿Cuál es el nombre de tu primer jefe?'),
  (8, '¿Cuál es tu película favorita?'),
  (9, '¿Cómo se llama tu mejor amigo?'),
  (10, '¿Cuál es tu deporte favorito?'),
  (11, '¿Cómo se llamaba tu primera mascota?'),
  (12, '¿Cuál es el nombre de tu escuela primaria?'),
  (13, '¿Cuál es tu equipo de fútbol favorito?'),
  (14, '¿Cómo se llama tu abuela?'),
  (15, '¿Cuál es tu asignatura favorita?'),
  (16, '¿Cuál es tu lugar favorito de vacaciones?'),
  (17, '¿Cómo se llama tu padre?'),
  (18, '¿Cuál es tu número favorito?'),
  (19, '¿Cuál es el nombre de tu primera novia?'),
  (20, '¿Cuál es tu canción favorita?');

/* ===== CLIENTES (20) ===== */
INSERT IGNORE INTO clientes (id, nombre) VALUES
  (1, 'Empresa Alpha'),
  (2, 'Bodegas del Sol'),
  (3, 'Consultora Beta'),
  (4, 'Grupo Nova'),
  (5, 'Industrias Delta'),
  (6, 'Transportes Vía'),
  (7, 'Clínica Salud Total'),
  (8, 'Hotel Gran Bahía'),
  (9, 'Tecnología Nexus'),
  (10, 'Comercios Unión'),
  (11, 'Agro Campos'),
  (12, 'Editorial Letras'),
  (13, 'Constructora Andamios'),
  (14, 'Farmacias San Roque'),
  (15, 'Estudio Diseño Azul'),
  (16, 'Logística Rápida'),
  (17, 'Gimnasio VitalFit'),
  (18, 'Cafetería La Esquina'),
  (19, 'Seguros Protección'),
  (20, 'Automóviles Estrella');

/* ===== USUARIOS (20) ===== */
INSERT IGNORE INTO users (id, username, pass, salt, email, code, fails, blocked, banned, role_id)
VALUES
  (1,  'admin',         'admin123', 'salt_generada', 'admin@example.com',         UUID(), 0, 0, 0, 2),
  (2,  'nuevo_usuario', '123456',   'salt_generada', 'nuevo@example.com',         UUID(), 0, 0, 0, 3),
  (3,  'iker',          '123456',   'salt_generada', 'iker@example.com',          UUID(), 0, 0, 0, 1),
  (4,  'carlos',        '123456',   'salt_generada', 'carlos@example.com',        UUID(), 0, 0, 0, 1),
  (5,  'lucia',         '123456',   'salt_generada', 'lucia@example.com',         UUID(), 0, 0, 0, 1),
  (6,  'marta',         '123456',   'salt_generada', 'marta@example.com',         UUID(), 0, 0, 0, 1),
  (7,  'jorge',         '123456',   'salt_generada', 'jorge@example.com',         UUID(), 0, 0, 0, 1),
  (8,  'ana',           '123456',   'salt_generada', 'ana@example.com',           UUID(), 0, 0, 0, 1),
  (9,  'pedro',         '123456',   'salt_generada', 'pedro@example.com',         UUID(), 0, 0, 0, 1),
  (10, 'sara',          '123456',   'salt_generada', 'sara@example.com',          UUID(), 0, 0, 0, 1),
  (11, 'david',         '123456',   'salt_generada', 'david@example.com',         UUID(), 0, 0, 0, 1),
  (12, 'laura',         '123456',   'salt_generada', 'laura@example.com',         UUID(), 0, 0, 0, 1),
  (13, 'marco',         '123456',   'salt_generada', 'marco@example.com',         UUID(), 0, 0, 0, 1),
  (14, 'elena',         '123456',   'salt_generada', 'elena@example.com',         UUID(), 0, 0, 0, 1),
  (15, 'pablo',         '123456',   'salt_generada', 'pablo@example.com',         UUID(), 0, 0, 0, 1),
  (16, 'claudia',       '123456',   'salt_generada', 'claudia@example.com',       UUID(), 0, 0, 0, 1),
  (17, 'andres',        '123456',   'salt_generada', 'andres@example.com',        UUID(), 0, 0, 0, 1),
  (18, 'valeria',       '123456',   'salt_generada', 'valeria@example.com',       UUID(), 0, 0, 0, 1),
  (19, 'raul',          '123456',   'salt_generada', 'raul@example.com',          UUID(), 0, 0, 0, 1),
  (20, 'sofia',         '123456',   'salt_generada', 'sofia@example.com',         UUID(), 0, 0, 0, 1);

/* ===== TAREAS (20) ===== */
INSERT IGNORE INTO tareas (id, titulo, descripcion, cliente_id, responsable_id, estado, prioridad, fecha_limite, horas_empleadas)
VALUES
  (1,  'Optimizar la web corporativa', 'Mejorar el rendimiento y SEO de la web principal.', 1, 3, 'EN_CURSO', 'MEDIA', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 4.5),
  (2,  'Campaña de marketing digital', 'Lanzar la campaña de publicidad online del trimestre.', 2, 4, 'PENDIENTE', 'ALTA', DATE_ADD(CURDATE(), INTERVAL 5 DAY), NULL),
  (3,  'Migración a nuevos servidores', 'Planificar y ejecutar la migración a los nuevos servidores.', 3, NULL, 'PENDIENTE', 'ALTA', NULL, NULL),
  (4,  'Renovación de licencias', 'Renovar las licencias de software de todo el equipo.', 4, 5, 'COMPLETADA', 'BAJA', DATE_SUB(CURDATE(), INTERVAL 3 DAY), 8.0),
  (5,  'Soporte trimestral al cliente', 'Revisión periódica con el cliente principal.', 5, 6, 'EN_CURSO', 'MEDIA', DATE_ADD(CURDATE(), INTERVAL 15 DAY), 2.5),
  (6,  'Rediseño del logotipo', 'Proponer nuevas propuestas de logotipo.', 6, 7, 'PENDIENTE', 'BAJA', NULL, NULL),
  (7,  'Auditoría de seguridad', 'Revisar accesos y políticas de seguridad de la infraestructura.', 7, 8, 'EN_CURSO', 'ALTA', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 6.0),
  (8,  'Actualización de documentación', 'Actualizar la documentación técnica del proyecto.', 8, NULL, 'PENDIENTE', 'MEDIA', NULL, NULL),
  (9,  'Formación del equipo comercial', 'Impartir formación sobre el nuevo CRM al equipo.', 9, 9, 'COMPLETADA', 'BAJA', DATE_SUB(CURDATE(), INTERVAL 7 DAY), 12.0),
  (10, 'Análisis de datos de ventas', 'Estudiar los datos de ventas del último año.', 10, 10, 'EN_CURSO', 'MEDIA', DATE_ADD(CURDATE(), INTERVAL 20 DAY), 5.0),
  (11, 'Instalación de nueva infraestructura', 'Montaje de los nuevos servidores y redes.', 11, 11, 'PENDIENTE', 'ALTA', DATE_ADD(CURDATE(), INTERVAL 30 DAY), NULL),
  (12, 'Campaña de emailing', 'Envío de la newsletter mensual a clientes.', 12, 12, 'COMPLETADA', 'MEDIA', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 3.0),
  (13, 'Seguimiento de incidencias', 'Resolver las incidencias abiertas del mes.', 13, 3, 'EN_CURSO', 'ALTA', DATE_ADD(CURDATE(), INTERVAL 1 DAY), 9.0),
  (14, 'Plan de contingencia', 'Elaborar el plan de contingencia ante desastres.', 14, NULL, 'PENDIENTE', 'ALTA', DATE_ADD(CURDATE(), INTERVAL 45 DAY), NULL),
  (15, 'Actualización de la app móvil', 'Nueva versión con mejoras de usabilidad.', 15, 13, 'EN_CURSO', 'MEDIA', DATE_ADD(CURDATE(), INTERVAL 25 DAY), 7.0),
  (16, 'Presupuesto anual del cliente', 'Preparar el presupuesto para el próximo ejercicio.', 16, 14, 'COMPLETADA', 'BAJA', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 2.0),
  (17, 'Formación en seguridad', 'Sesiones de concienciación en ciberseguridad.', 17, 15, 'PENDIENTE', 'MEDIA', NULL, NULL),
  (18, 'Optimización de la base de datos', 'Revisar índices y consultas lentas.', 18, 16, 'EN_CURSO', 'ALTA', DATE_ADD(CURDATE(), INTERVAL 4 DAY), 4.0),
  (19, 'Informe de rendimiento', 'Generar el informe trimestral de KPIs.', 19, NULL, 'PENDIENTE', 'BAJA', DATE_ADD(CURDATE(), INTERVAL 12 DAY), NULL),
  (20, 'Lanzamiento del nuevo producto', 'Coordinar el lanzamiento del nuevo producto.', 20, 17, 'EN_CURSO', 'ALTA', DATE_ADD(CURDATE(), INTERVAL 60 DAY), 1.5);

/* ===== REGISTROS DE JORNADA (20) ===== */
SET @d0 = CURDATE();
SET @d1 = DATE_SUB(CURDATE(), INTERVAL 1 DAY);
SET @d2 = DATE_SUB(CURDATE(), INTERVAL 2 DAY);
SET @d3 = DATE_SUB(CURDATE(), INTERVAL 3 DAY);
SET @d4 = DATE_SUB(CURDATE(), INTERVAL 4 DAY);

INSERT IGNORE INTO time_entries (id, user_id, date, start_time, end_time, total_minutes_worked)
VALUES
  (1, 3,  @d0, CONCAT(@d0, ' 09:00:00'), CONCAT(@d0, ' 17:00:00'), 480),
  (2, 4,  @d0, CONCAT(@d0, ' 08:30:00'), CONCAT(@d0, ' 14:30:00'), 360),
  (3, 5,  @d1, CONCAT(@d1, ' 09:00:00'), CONCAT(@d1, ' 18:00:00'), 540),
  (4, 6,  @d1, CONCAT(@d1, ' 10:00:00'), CONCAT(@d1, ' 14:00:00'), 240),
  (5, 7,  @d0, CONCAT(@d0, ' 08:00:00'), CONCAT(@d0, ' 16:00:00'), 480),
  (6, 8,  @d0, CONCAT(@d0, ' 09:30:00'), CONCAT(@d0, ' 13:30:00'), 240),
  (7, 9,  @d2, CONCAT(@d2, ' 09:00:00'), CONCAT(@d2, ' 17:00:00'), 480),
  (8, 10, @d2, CONCAT(@d2, ' 08:00:00'), CONCAT(@d2, ' 15:00:00'), 420),
  (9, 11, @d1, CONCAT(@d1, ' 09:00:00'), CONCAT(@d1, ' 13:00:00'), 240),
  (10, 12, @d0, CONCAT(@d0, ' 10:00:00'), CONCAT(@d0, ' 18:00:00'), 480),
  (11, 13, @d3, CONCAT(@d3, ' 09:00:00'), CONCAT(@d3, ' 17:00:00'), 480),
  (12, 14, @d3, CONCAT(@d3, ' 08:30:00'), CONCAT(@d3, ' 12:30:00'), 240),
  (13, 15, @d1, CONCAT(@d1, ' 09:00:00'), CONCAT(@d1, ' 14:00:00'), 300),
  (14, 16, @d0, CONCAT(@d0, ' 08:00:00'), CONCAT(@d0, ' 17:00:00'), 540),
  (15, 17, @d2, CONCAT(@d2, ' 09:00:00'), CONCAT(@d2, ' 13:00:00'), 240),
  (16, 18, @d0, CONCAT(@d0, ' 09:30:00'), CONCAT(@d0, ' 15:30:00'), 360),
  (17, 19, @d1, CONCAT(@d1, ' 10:00:00'), CONCAT(@d1, ' 18:00:00'), 480),
  (18, 20, @d0, CONCAT(@d0, ' 08:00:00'), CONCAT(@d0, ' 12:00:00'), 240),
  (19, 3,  @d4, CONCAT(@d4, ' 09:00:00'), CONCAT(@d4, ' 17:00:00'), 480),
  (20, 4,  @d4, CONCAT(@d4, ' 09:00:00'), CONCAT(@d4, ' 13:00:00'), 240);

/* ===== PAUSAS (20) ===== */
INSERT IGNORE INTO breaks (id, time_entry_id, start_time, end_time)
VALUES
  (1, 1,  CONCAT(@d0, ' 11:00:00'), CONCAT(@d0, ' 11:30:00')),
  (2, 2,  CONCAT(@d0, ' 10:00:00'), CONCAT(@d0, ' 10:30:00')),
  (3, 3,  CONCAT(@d1, ' 12:00:00'), CONCAT(@d1, ' 12:30:00')),
  (4, 4,  CONCAT(@d1, ' 11:00:00'), CONCAT(@d1, ' 11:15:00')),
  (5, 5,  CONCAT(@d0, ' 10:30:00'), CONCAT(@d0, ' 11:00:00')),
  (6, 6,  CONCAT(@d0, ' 11:00:00'), CONCAT(@d0, ' 11:15:00')),
  (7, 7,  CONCAT(@d2, ' 12:00:00'), CONCAT(@d2, ' 12:45:00')),
  (8, 8,  CONCAT(@d2, ' 10:30:00'), CONCAT(@d2, ' 11:00:00')),
  (9, 9,  CONCAT(@d1, ' 11:00:00'), CONCAT(@d1, ' 11:15:00')),
  (10, 10, CONCAT(@d0, ' 12:30:00'), CONCAT(@d0, ' 13:00:00')),
  (11, 11, CONCAT(@d3, ' 12:00:00'), CONCAT(@d3, ' 12:30:00')),
  (12, 12, CONCAT(@d3, ' 10:30:00'), CONCAT(@d3, ' 10:45:00')),
  (13, 13, CONCAT(@d1, ' 11:00:00'), CONCAT(@d1, ' 11:30:00')),
  (14, 14, CONCAT(@d0, ' 12:00:00'), CONCAT(@d0, ' 12:45:00')),
  (15, 15, CONCAT(@d2, ' 11:00:00'), CONCAT(@d2, ' 11:15:00')),
  (16, 16, CONCAT(@d0, ' 11:30:00'), CONCAT(@d0, ' 12:00:00')),
  (17, 17, CONCAT(@d1, ' 12:00:00'), CONCAT(@d1, ' 12:30:00')),
  (18, 18, CONCAT(@d0, ' 10:00:00'), CONCAT(@d0, ' 10:30:00')),
  (19, 19, CONCAT(@d4, ' 12:00:00'), CONCAT(@d4, ' 12:30:00')),
  (20, 20, CONCAT(@d4, ' 11:00:00'), CONCAT(@d4, ' 11:15:00'));

/* ===== PREGUNTAS DE SEGURIDAD DE LOS USUARIOS (20) ===== */
INSERT IGNORE INTO user_questions (id, user_id, question_id, answer)
VALUES
  (1, 1, 1, 'Rex'),
  (2, 2, 2, 'Sevilla'),
  (3, 3, 3, 'Doña Carmen'),
  (4, 4, 1, 'Luna'),
  (5, 5, 2, 'Madrid'),
  (6, 6, 3, 'Don Luis'),
  (7, 7, 1, 'Nala'),
  (8, 8, 2, 'Bilbao'),
  (9, 9, 3, 'Doña Pilar'),
  (10, 10, 1, 'Thor'),
  (11, 11, 2, 'Valencia'),
  (12, 12, 3, 'Don José'),
  (13, 13, 1, 'Bola'),
  (14, 14, 2, 'Zaragoza'),
  (15, 15, 3, 'Doña María'),
  (16, 16, 1, 'Rocky'),
  (17, 17, 2, 'Granada'),
  (18, 18, 3, 'Don Manuel'),
  (19, 19, 1, 'Coco'),
  (20, 20, 2, 'Málaga');

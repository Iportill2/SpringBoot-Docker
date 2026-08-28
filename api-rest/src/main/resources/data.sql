/* ===== ROLES (se mantienen los 3, se añade BLOQUEADO) ===== */
INSERT IGNORE INTO roles (id, name) VALUES
  (1, 'EMPLEADO'),
  (2, 'ADMIN'),
  (3, 'PENDIENTE'),
  (4, 'BLOQUEADO');

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
INSERT IGNORE INTO clientes (id, nombre, persona_contacto, telefono, direccion, fecha_alta) VALUES
  (1, 'Empresa Alpha', 'Marta Gómez', '944112233', 'Gran Vía 12, Bilbao', '2024-01-15'),
  (2, 'Bodegas del Sol', 'Javier Ruiz', '941223344', 'Calle Mayor 5, Logroño', '2024-02-03'),
  (3, 'Consultora Beta', 'Laura Sánchez', '915334455', 'Paseo de la Castellana 88, Madrid', '2024-02-20'),
  (4, 'Grupo Nova', 'Diego Fernández', '963445566', 'Avenida del Puerto 30, Valencia', '2024-03-05'),
  (5, 'Industrias Delta', 'Elena Torres', '976556677', 'Polígono Industrial Norte 7, Zaragoza', '2024-03-18'),
  (6, 'Transportes Vía', 'Roberto Díaz', '985667788', 'Carretera N-634 km 12, Gijón', '2024-04-02'),
  (7, 'Clínica Salud Total', 'Ana Martínez', '954778899', 'Calle Sierpes 20, Sevilla', '2024-04-25'),
  (8, 'Hotel Gran Bahía', 'Carlos López', '971889900', 'Paseo Marítimo 45, Palma', '2024-05-10'),
  (9, 'Tecnología Nexus', 'Sara Navarro', '946990011', 'Alameda Recalde 18, Bilbao', '2024-05-28'),
  (10, 'Comercios Unión', 'Pablo Iglesias', '922001122', 'Calle León y Castillo 60, Las Palmas', '2024-06-14'),
  (11, 'Agro Campos', 'Rosa Ortega', '957112233', 'Camino de la Vega 3, Córdoba', '2024-06-30'),
  (12, 'Editorial Letras', 'Miguel Serrano', '981223344', 'Calle Real 22, A Coruña', '2024-07-11'),
  (13, 'Constructora Andamios', 'Cristina Vidal', '968334455', 'Avenida Juan Carlos I 14, Murcia', '2024-07-29'),
  (14, 'Farmacias San Roque', 'Fernando Castro', '952445566', 'Calle Larios 9, Málaga', '2024-08-07'),
  (15, 'Estudio Diseño Azul', 'Beatriz Molina', '943556677', 'Parte Vieja 11, San Sebastián', '2024-08-22'),
  (16, 'Logística Rápida', 'Andrés Guerrero', '926667788', 'Polígono El Salobral 4, Ciudad Real', '2024-09-09'),
  (17, 'Gimnasio VitalFit', 'Patricia Ramos', '964778899', 'Avenida del Mar 8, Castellón', '2024-09-24'),
  (18, 'Cafetería La Esquina', 'Álvaro Jiménez', '979889900', 'Plaza Mayor 2, Palencia', '2024-10-05'),
  (19, 'Seguros Protección', 'Nuria Delgado', '925990011', 'Calle Comercio 15, Toledo', '2024-10-19'),
  (20, 'Automóviles Estrella', 'Tomás Herrera', '984001122', 'Avenida de Galicia 33, Oviedo', '2024-11-02');

/* ===== USUARIOS (20) ===== */
INSERT IGNORE INTO users (id, username, pass, salt, email, code, role_id) VALUES 
(1, 'admin', '$2a$10$WMcTdmhuVG7No0l5nFvdn.ZFiDwuoNo0eS99Nza9X1OdUGAQr1TLG', 'salt_generada', 'admin@example.com', UUID(), 2), 
(2, 'nuevo_usuario', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'nuevo@example.com', UUID(), 3), 
(3, 'iker', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'iker@example.com', UUID(), 1), 
(4, 'carlos', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'carlos@example.com', UUID(), 1), 
(5, 'lucia', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'lucia@example.com', UUID(), 1), 
(6, 'marta', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'marta@example.com', UUID(), 1), 
(7, 'jorge', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'jorge@example.com', UUID(), 1), 
(8, 'ana', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'ana@example.com', UUID(), 1), 
(9, 'pedro', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'pedro@example.com', UUID(), 1), 
(10, 'sara', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'sara@example.com', UUID(), 1), 
(11, 'david', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'david@example.com', UUID(), 1), 
(12, 'laura', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'laura@example.com', UUID(), 1), 
(13, 'marco', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'marco@example.com', UUID(), 1), 
(14, 'elena', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'elena@example.com', UUID(), 1), 
(15, 'pablo', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'pablo@example.com', UUID(), 1), 
(16, 'claudia', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'claudia@example.com', UUID(), 1), 
(17, 'andres', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'andres@example.com', UUID(), 1), 
(18, 'valeria', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'valeria@example.com', UUID(), 1), 
(19, 'raul', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'raul@example.com', UUID(), 1), 
(20, 'sofia', '$2a$10$K4t7wjAKRzTY9PDyz5oY0.PEZKWDFNG/9J/c1MzmdeDjx/MjjNGXm', 'salt_generada', 'sofia@example.com', UUID(), 1);

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

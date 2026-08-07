INSERT IGNORE INTO roles (id, name) VALUES
  (1, 'EMPLEADO'),
  (2, 'ADMIN'),
  (3, 'PENDIENTE');

INSERT IGNORE INTO questions (id, text) VALUES
  (1, '¿Cuál es el nombre de tu mascota?'),
  (2, '¿Cuál es tu ciudad de nacimiento?'),
  (3, '¿Cómo se llamaba tu primer profesor?');

INSERT IGNORE INTO users (id, username, pass, salt, email, code, fails, blocked, banned, role_id)
VALUES (1, 'admin', 'admin123', 'salt_generada', 'admin@example.com', UUID(), 0, 0, 0, 2);

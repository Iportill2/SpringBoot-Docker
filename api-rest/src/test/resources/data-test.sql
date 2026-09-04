-- ============================================
-- DATA DE PRUEBA (data-test.sql)
-- --------------------------------------------
-- Este fichero es el equivalente de TEST del
-- data.sql de produccion. Contiene UNICAMENTE
-- los datos maestros de referencia que los tests
-- necesitan de forma compartida (los roles base),
-- y es IDEMPOTENTE: INSERT IGNORE permite que
-- se ejecute sin romperse aunque el registro ya
-- exista en la base en memoria (evita la
-- "Violacion de indice de unicidad" que se
-- producia al chocar con el data.sql real).
--
-- El resto de datos (usuarios, tareas, clientes,
-- preguntas, jornadas...) lo crea cada test de
-- forma aislada a traves de sus repositorios,
-- para que los tests sean independientes entre si
-- y verificables de forma didactica.
-- ============================================

/* Roles base: 1=EMPLEADO, 2=ADMIN, 3=PENDIENTE, 4=BLOQUEADO */
INSERT IGNORE INTO roles (id, name) VALUES
  (1, 'EMPLEADO'),
  (2, 'ADMIN'),
  (3, 'PENDIENTE'),
  (4, 'BLOQUEADO');

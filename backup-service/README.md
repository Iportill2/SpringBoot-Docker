# backup-service — Tests

Test suites for the **backup-service** module (REST service that creates, lists, downloads and restores database backups).

## Running the tests

From this folder:

**Windows (CMD or PowerShell):**
```
.\mvnw.cmd test
```

**Linux / macOS:**
```
./mvnw test
```

Run a single class:

**Windows:**
```
.\mvnw.cmd test -Dtest=BackupControllerTests
```

**Linux / macOS:**
```
./mvnw test -Dtest=BackupControllerTests
```

Run a single method:

**Windows:**
```
.\mvnw.cmd test -Dtest=BackupControllerTests#createBackupReturnsPath
```

**Linux / macOS:**
```
./mvnw test -Dtest=BackupControllerTests#createBackupReturnsPath
```

**No MySQL, `mysqldump`, `bash` or Docker needed:** the tests use `@WebMvcTest` with a mocked `BackupService`, and the real service tests use a temporary directory without running any external process.

## Test infrastructure

| Element                  | Description |
|--------------------------|-------------|
| `BackupControllerTests`  | Main suite: `@WebMvcTest(controllers = BackupController.class)` + `@MockitoBean BackupService`. |
| `MySqlBackupServiceTests`| Unit tests of `MySqlBackupService` with `@TempDir`: listing, directory creation, name validation and download. Does not run real commands. |
| `BackupServiceApplicationTests` | Checks that the application context starts. |

## Summary

**Total: 18 tests** (7 controller + 1 context + 10 service).

## Endpoints — `BackupControllerTests`

The controller exposes 5 REST endpoints under `/backups`:

- `createBackupReturnsPath` — `POST /backups` returns the path of the created backup (string).
- `listBackupsReturnsJsonArray` — `GET /backups` returns the JSON list of backups with `fileName` and `size`.
- `downloadBackupReturnsResourceBody` — `GET /backups/{fileName}` returns the file content in the response body.
- `restoreBackupReturnsTrue` — `POST /backups/restore/{fileName}` returns `true` when the restore works.
- `restoreBackupReturnsFalseWhenFileMissing` — `POST /backups/restore/{fileName}` returns `false` when the file does not exist.
- `deleteBackupReturnsTrue` — `DELETE /backups/{fileName}` returns `true` when the backup is deleted.
- `deleteBackupReturnsFalseWhenFileMissing` — `DELETE /backups/{fileName}` returns `false` when the file does not exist.

## Service — `MySqlBackupServiceTests`

Tests of the real service (without external processes):

- `listBackupsReturnsSqlGzFilesInDirectory` — only lists `.sql.gz` files, ignores others.
- `listBackupsCreatesDirectoryWhenMissing` — creates the backup directory if it does not exist.
- `downloadBackupReturnsResourceForExistingFile` — returns the `Resource` of an existing file.
- `downloadBackupRejectsTraversalName` — rejects `../../etc/passwd` (path traversal).
- `downloadBackupRejectsNonBackupName` — rejects names that do not end in `.sql.gz`.
- `restoreBackupRejectsInvalidName` — rejects names containing `..`.
- `restoreBackupReturnsFalseWhenFileDoesNotExist` — returns `false` if the file does not exist.
- `deleteBackupRemovesFile` — deletes the file and returns `true`.
- `deleteBackupReturnsFalseWhenFileDoesNotExist` — returns `false` if the file does not exist.
- `deleteBackupRejectsInvalidName` — rejects names containing `..`.

## Context — `BackupServiceApplicationTests`

- `contextLoads` — verifies that the Spring Boot context starts correctly.

## Running in Docker

The service is designed to run **only in Docker** (via docker-compose). It starts together with MySQL with:

```
docker compose up -d mysql backup-service
```

- Connects to MySQL through the `mysql` host on port `3306`.
- Stores the backups in the `/backup` volume.
- Uses the `MYSQL_USER` / `MYSQL_PASSWORD` credentials (the same ones as the API, defined in `.env`): the user has `ALL PRIVILEGES` on the database, enough for `mysqldump` and restore.

**Security:** the backup-service port is published only on `127.0.0.1` (host loopback), so it is not accessible from the local network. The rest-client reaches it via `host.docker.internal:8082`.

## Retención automática y programación (cron)

El servicio aplica una **política de retención** tras cada backup (manual o programado) y expone un endpoint para ejecutarla bajo demanda.

### Configuración (`application.yml` / variables de entorno)

| Propiedad                          | Env var                        | Defecto       | Descripción |
|------------------------------------|--------------------------------|---------------|-------------|
| `backup.cron`                      | `BACKUP_CRON`                  | `0 0 2 * * *` | Expresión cron (diario 02:00) del backup automático. |
| `backup.enabled`                   | `BACKUP_ENABLED`               | `true`        | Activa el scheduler de backup automático. |
| `backup.retention.enabled`         | `BACKUP_RETENTION_ENABLED`     | `true`        | Activa la eliminación de backups antiguos. |
| `backup.retention.daily`           | `BACKUP_RETENTION_DAILY`       | `7`           | Días que se conservan íntegramente. |
| `backup.retention.weekly`          | `BACKUP_RETENTION_WEEKLY`      | `4`           | Semanas (ISO) de las que se guarda el más reciente. |
| `backup.retention.monthly`         | `BACKUP_RETENTION_MONTHLY`     | `12`          | Meses de los que se guarda el más reciente. |

### Algoritmo de retención

1. **Diario:** se conservan todos los backups de los últimos `daily` días.
2. **Semanal:** para las semanas más antiguas que la ventana diaria (hasta `weekly` semanas atrás), se conserva el backup más reciente de cada semana ISO.
3. **Mensual:** para los meses más antiguos que la ventana semanal (hasta `monthly` meses atrás), se conserva el backup más reciente de cada mes.
4. El resto se elimina.

El nombre del archivo debe seguir el patrón `aplicacion_yyyy-MM-dd_HH-mm-ss.sql.gz` para que la fecha se infiera correctamente.

### Endpoints

- `POST /backups/cleanup` — ejecuta la retención ahora y devuelve el número de archivos eliminados.
- La creación manual (`POST /backups`) también dispara la retención automáticamente.
- El backup programado (`BackupScheduler`) crea un backup y luego aplica la retención según `backup.cron`.

## Log de auditoría (texto plano)

Cada operación de backup se registra en un **log de texto plano** con formato:

```
yyyy-MM-dd HH:mm:ss | ACTOR | ACCION | detalle
```

Ejemplo:

```
2026-08-12 17:25:13 | admin@empresa.com | CREATE  | Backup creado: aplicacion_2026-08-12_17-25-13.sql.gz
2026-08-12 17:30:00 | SYSTEM (programado) | CLEANUP | Retención aplicada: 3 archivos eliminados (dias=7, semanas=4, meses=12)
2026-08-12 18:00:00 | admin@empresa.com | RESTORE | Backup restaurado: aplicacion_2026-08-10_12-00-00.sql.gz
```

- **Qué** (`ACCION`): `CREATE`, `RESTORE`, `DELETE`, `CLEANUP`, `DOWNLOAD` (y `FALLO` en errores).
- **Quién** (`ACTOR`): usuario que ejecutó la acción (su `username` de sesión, enviado por el rest-client vía cabecera `X-Actor`); o `SYSTEM (programado)` para el backup automático; o `DESCONOCIDO` si no se identifica.
- **Cuándo**: timestamp `yyyy-MM-dd HH:mm:ss`.

El archivo se guarda en `backup.log-file` (por defecto `<backup.directory>/backup-audit.log`, dentro del volumen `/backup`).

### Configuración

| Propiedad        | Env var             | Defecto                      | Descripción |
|------------------|---------------------|------------------------------|-------------|
| `backup.log-file`| `BACKUP_LOG_FILE`   | `<backup.directory>/backup-audit.log` | Ruta del archivo de log. |

### Endpoints

- `GET /backups/log` — devuelve el contenido del log en texto plano (`text/plain`).
- En el rest-client, `GET /menu/backups/log` (solo ADMIN) muestra el mismo log.

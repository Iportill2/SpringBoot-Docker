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

## Automatic retention and scheduling (cron)

The service applies a **retention policy** after the scheduled (cron) backup and exposes an endpoint to run it on demand. Manual backup creation does **not** trigger retention automatically: it must be run explicitly (rest-client button or the `POST /backups/cleanup` endpoint).

### Configuration (`application.yml` / environment variables)

| Property                           | Env var                        | Default       | Description |
|------------------------------------|--------------------------------|---------------|-------------|
| `backup.cron`                      | `BACKUP_CRON`                  | `0 0 2 * * *` | Cron expression (daily 02:00) for the automatic backup. |
| `backup.enabled`                   | `BACKUP_ENABLED`               | `true`        | Enables the automatic backup scheduler. |
| `backup.retention.enabled`         | `BACKUP_RETENTION_ENABLED`     | `true`        | Enables deletion of old backups. |
| `backup.retention.daily`           | `BACKUP_RETENTION_DAILY`       | `7`           | Days kept fully. |
| `backup.retention.weekly`          | `BACKUP_RETENTION_WEEKLY`      | `4`           | ISO weeks for which the most recent backup is kept. |
| `backup.retention.monthly`         | `BACKUP_RETENTION_MONTHLY`     | `12`          | Months for which the most recent backup is kept. |

### Retention algorithm

1. **Daily:** all backups from the last `daily` days are kept.
2. **Weekly:** for weeks older than the daily window (up to `weekly` weeks back), the most recent backup of each ISO week is kept.
3. **Monthly:** for months older than the weekly window (up to `monthly` months back), the most recent backup of each month is kept.
4. The rest are deleted.

The file name must follow the pattern `aplicacion_yyyy-MM-dd_HH-mm-ss.sql.gz` so the date is inferred correctly.

### Endpoints

- `POST /backups/cleanup` — runs retention now and returns the number of deleted files. Used by the rest-client **"Apply retention"** button.
- `POST /backups` (manual creation) does **not** apply retention: it only creates the backup. To clean up manually, use `POST /backups/cleanup`.
- The scheduled backup (`BackupScheduler`) creates a backup and then applies retention according to `backup.cron` (unchanged).

## Audit log (plain text)

Every backup operation is recorded in a **plain-text log** with the format:

```
yyyy-MM-dd HH:mm:ss | ACTOR | ACTION | detail
```

Example:

```
2026-08-12 17:25:13 | admin@empresa.com | CREATE  | Backup created: aplicacion_2026-08-12_17-25-13.sql.gz
2026-08-12 17:30:00 | SYSTEM (scheduled) | CLEANUP | Retention applied: 3 files deleted (days=7, weeks=4, months=12)
2026-08-12 18:00:00 | admin@empresa.com | RESTORE | Backup restored: aplicacion_2026-08-10_12-00-00.sql.gz
```

- **Action** (`ACTION`): `CREATE`, `RESTORE`, `DELETE`, `CLEANUP`, `DOWNLOAD` (and `FAILED` on errors).
- **Who** (`ACTOR`): the user who performed the action (their session `username`, sent by the rest-client via the `X-Actor` header); or `SYSTEM (scheduled)` for the automatic backup; or `UNKNOWN` if not identified.
- **When**: timestamp `yyyy-MM-dd HH:mm:ss`.

The file is stored at `backup.log-file` (default `<backup.directory>/backup-audit.log`, inside the `/backup` volume).

### Configuration

| Property         | Env var            | Default                                | Description |
|------------------|--------------------|----------------------------------------|-------------|
| `backup.log-file`| `BACKUP_LOG_FILE`  | `<backup.directory>/backup-audit.log`  | Path of the log file. |

### Endpoints

- `GET /backups/log` — returns the log content as plain text (`text/plain`).
- In the rest-client, `GET /menu/backups/log` (ADMIN only) shows the same log.
- In the rest-client, `GET /menu/backups/log/view` (ADMIN only) shows the log on a page that **auto-refreshes every 2 s** (without reloading).

### Deleting the audit log

The log file can be deleted directly inside the container:

```
docker compose exec backup-service rm -f /backup/backup-audit.log
```

If `BACKUP_LOG_FILE` is configured with a different path, use that instead of `/backup/backup-audit.log`.

**What happens when you delete it:** nothing breaks. Reading (`GET /backups/log` / rest-client view) returns empty until a new entry appears, and the next operation (create backup, restore, delete or apply retention) **recreates the file automatically** and resumes logging. Only the previous log history is lost.

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

**Total: 13 tests** (5 controller + 1 context + 7 service).

## Endpoints — `BackupControllerTests`

The controller exposes 4 REST endpoints under `/backups`:

- `createBackupReturnsPath` — `POST /backups` returns the path of the created backup (string).
- `listBackupsReturnsJsonArray` — `GET /backups` returns the JSON list of backups with `fileName` and `size`.
- `downloadBackupReturnsResourceBody` — `GET /backups/{fileName}` returns the file content in the response body.
- `restoreBackupReturnsTrue` — `POST /backups/restore/{fileName}` returns `true` when the restore works.
- `restoreBackupReturnsFalseWhenFileMissing` — `POST /backups/restore/{fileName}` returns `false` when the file does not exist.

## Service — `MySqlBackupServiceTests`

Tests of the real service (without external processes):

- `listBackupsReturnsSqlGzFilesInDirectory` — only lists `.sql.gz` files, ignores others.
- `listBackupsCreatesDirectoryWhenMissing` — creates the backup directory if it does not exist.
- `downloadBackupReturnsResourceForExistingFile` — returns the `Resource` of an existing file.
- `downloadBackupRejectsTraversalName` — rejects `../../etc/passwd` (path traversal).
- `downloadBackupRejectsNonBackupName` — rejects names that do not end in `.sql.gz`.
- `restoreBackupRejectsInvalidName` — rejects names containing `..`.
- `restoreBackupReturnsFalseWhenFileDoesNotExist` — returns `false` if the file does not exist.

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

package com.miproyecto.backup.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.miproyecto.backup.config.BackupProperties;
import com.miproyecto.backup.config.BackupActorContext;
import com.miproyecto.backup.config.MySqlProperties;
import com.miproyecto.backup.model.BackupInfo;

@Service
public class MySqlBackupService implements BackupService {

    private final BackupProperties backupProperties;
    private final MySqlProperties mySqlProperties;
    private final BackupAuditLog auditLog;
    private final BackupActorContext actorContext;

    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public MySqlBackupService(
            BackupProperties backupProperties,
            MySqlProperties mySqlProperties,
            BackupAuditLog auditLog,
            BackupActorContext actorContext) {

        this.backupProperties = backupProperties;
        this.mySqlProperties = mySqlProperties;
        this.auditLog = auditLog;
        this.actorContext = actorContext;
    }

    @Override
    public String createBackup() {

        Path directory = Path.of(backupProperties.getDirectory());

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create backups directory: " + directory,
                    e
            );
        }

        String fileName =
                "aplicacion_"
                + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                + ".sql.gz";

        Path file = directory.resolve(fileName);

        validarConfiguracionMySql();

        String error = crearBackupConProceso(file);

        if (error != null) {
            auditLog.log(
                    actorContext.getActor(),
                    BackupAuditLog.ACTION_CREATE,
                    "FAILED: backup creation"
            );
            throw new RuntimeException(
                    "Error creando backup:\n"
                    + error
            );
        }

        auditLog.log(
                actorContext.getActor(),
                BackupAuditLog.ACTION_CREATE,
                "Backup created: " + fileName
        );

        return file.toString();
    }

    @Override
    public List<BackupInfo> listBackups() {

        List<BackupInfo> backups = new ArrayList<>();

        Path directory = Path.of(backupProperties.getDirectory());

        try {

            Files.createDirectories(directory);

            Files.list(directory)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".sql.gz"))
                    .forEach(path -> {

                        try {

                            backups.add(
                                new BackupInfo(
                                    path.getFileName().toString(),
                                    Files.size(path),
                                    Files.getLastModifiedTime(path)
                                         .toInstant()
                                         .atZone(
                                             java.time.ZoneId.systemDefault()
                                         )
                                         .toLocalDateTime()
                                )
                            );

                        } catch (IOException e) {
                            throw new RuntimeException(
                                "Error reading backup: "
                                + path.getFileName(),
                                e
                            );
                        }

                    });

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not read the backups directory",
                    e
            );
        }

        return backups;
    }

    @Override
    public Resource downloadBackup(String fileName) {

        Path file = resolverArchivoBackup(fileName);

        if (!Files.exists(file)) {

            throw new RuntimeException(
                    "Backup does not exist: " + fileName
            );
        }

        auditLog.log(
                actorContext.getActor(),
                BackupAuditLog.ACTION_DOWNLOAD,
                "Backup descargado: " + fileName
        );

        return new FileSystemResource(file);
    }

    @Override
    public Boolean restoreBackup(String fileName) {

        Path file = resolverArchivoBackup(fileName);

        if (!Files.exists(file)) {
            return false;
        }

        validarConfiguracionMySql();

        String error = restaurarConProceso(file);

        if (error != null) {
            auditLog.log(
                    actorContext.getActor(),
                    BackupAuditLog.ACTION_RESTORE,
                    "FAILED: restore backup: " + fileName
            );
            throw new RuntimeException(
                    "Error restoring backup "
                    + fileName + ":\n"
                    + error
            );
        }

        auditLog.log(
                actorContext.getActor(),
                BackupAuditLog.ACTION_RESTORE,
                "Backup restaurado: " + fileName
        );

        return true;
    }

    @Override
    public Boolean deleteBackup(String fileName) {

        Path file = resolverArchivoBackup(fileName);

        if (!Files.exists(file)) {
            return false;
        }

        try {

            Files.delete(file);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error deleting backup "
                    + fileName + ":\n"
                    + e.getMessage(),
                    e
            );
        }

        auditLog.log(
                actorContext.getActor(),
                BackupAuditLog.ACTION_DELETE,
                "Backup eliminado: " + fileName
        );

        return true;
    }

    @Override
    public int cleanupOldBackups() {

        BackupProperties.Retention retention = backupProperties.getRetention();

        if (retention == null || !retention.isEnabled()) {
            return 0;
        }

        Path directory = Path.of(backupProperties.getDirectory());

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create backups directory: " + directory,
                    e
            );
        }

        Map<Path, LocalDateTime> backups = new HashMap<>();

        try (var stream = Files.list(directory)) {
            stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".sql.gz"))
                    .forEach(path -> {
                        LocalDateTime created = parseFileNameDate(path.getFileName().toString());
                        if (created != null) {
                            backups.put(path, created);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not read the backups directory",
                    e
            );
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime dailyCutoff = now.minusDays(retention.getDaily());
        LocalDateTime weeklyCutoff = now.minusWeeks(retention.getWeekly());
        LocalDateTime monthlyCutoff = now.minusMonths(retention.getMonthly());

        Set<Path> keep = new HashSet<>();

        backups.forEach((path, created) -> {
            if (!created.isBefore(dailyCutoff)) {
                keep.add(path);
            }
        });

        WeekFields weekFields = WeekFields.ISO;
        Map<String, Path> newestOfWeek = new HashMap<>();
        Map<String, Path> newestOfMonth = new HashMap<>();

        backups.entrySet().stream()
                .sorted(Map.Entry.<Path, LocalDateTime>comparingByValue().reversed())
                .forEach(entry -> {
                    Path path = entry.getKey();
                    LocalDateTime created = entry.getValue();

                    if (!created.isBefore(weeklyCutoff)) {
                        String weekKey = created.get(weekFields.weekBasedYear())
                                + "-"
                                + created.get(weekFields.weekOfWeekBasedYear());
                        newestOfWeek.putIfAbsent(weekKey, path);
                    }

                    if (!created.isBefore(monthlyCutoff)) {
                        String monthKey = created.getYear()
                                + "-"
                                + created.getMonthValue();
                        newestOfMonth.putIfAbsent(monthKey, path);
                    }
                });

        keep.addAll(newestOfWeek.values());
        keep.addAll(newestOfMonth.values());

        int deleted = 0;

        for (Path path : backups.keySet()) {
            if (!keep.contains(path)) {
                try {
                    Files.deleteIfExists(path);
                    deleted++;
                } catch (IOException e) {
                    throw new RuntimeException(
                            "Error deleting backup " + path.getFileName(),
                            e
                    );
                }
            }
        }

        auditLog.log(
                actorContext.getActor(),
                BackupAuditLog.ACTION_CLEANUP,
                "Retention applied: "
                + deleted
                + " file(s) deleted (days="
                + retention.getDaily()
                + ", weeks="
                + retention.getWeekly()
                + ", months="
                + retention.getMonthly()
                + ")"
        );

        return deleted;
    }

    private LocalDateTime parseFileNameDate(String fileName) {
        try {
            String datePart = fileName
                    .substring(
                            "aplicacion_".length(),
                            fileName.length() - ".sql.gz".length()
                    );
            return LocalDateTime.parse(datePart, FILE_DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private Path resolverArchivoBackup(String fileName) {

        Path directory = Path.of(backupProperties.getDirectory())
                .toAbsolutePath()
                .normalize();

        if (fileName == null
                || fileName.isEmpty()
                || fileName.contains("/")
                || fileName.contains("\\")
                || fileName.contains("..")
                || !fileName.endsWith(".sql.gz")) {

            throw new IllegalArgumentException(
                    "Invalid backup name: " + fileName
            );
        }

        Path file = directory.resolve(fileName).normalize();

        if (!file.startsWith(directory)) {

            throw new IllegalArgumentException(
                    "Invalid backup name: " + fileName
            );
        }

        return file;
    }

    private void validarConfiguracionMySql() {
        if (!esAlfanumerico(mySqlProperties.getHost())
                || !esAlfanumerico(mySqlProperties.getUser())
                || !esAlfanumerico(mySqlProperties.getDatabase())) {
            throw new IllegalStateException(
                    "Configuración de MySQL inválida: host/user/database deben ser alfanuméricos"
            );
        }
        if (mySqlProperties.getPort() <= 0 || mySqlProperties.getPort() > 65535) {
            throw new IllegalStateException("Configuración de MySQL inválida: puerto fuera de rango");
        }
    }

    private boolean esAlfanumerico(String value) {
        return value != null && value.matches("[A-Za-z0-9_-]+");
    }

    private String crearBackupConProceso(Path file) {

        ProcessBuilder builder = new ProcessBuilder(
                "mysqldump",
                "--single-transaction",
                "--routines",
                "--triggers",
                "--no-tablespaces",
                "--databases",
                "-h", mySqlProperties.getHost(),
                "-P", String.valueOf(mySqlProperties.getPort()),
                "-u", mySqlProperties.getUser(),
                mySqlProperties.getDatabase()
        );

        builder.environment().put("MYSQL_PWD", mySqlProperties.getPassword());

        try {

            Process process = builder.start();

            String error;
            try (java.util.zip.GZIPOutputStream gzip =
                    new java.util.zip.GZIPOutputStream(Files.newOutputStream(file));
                 java.io.InputStream in = process.getInputStream()) {

                in.transferTo(gzip);
            }

            error = leerError(process.getErrorStream());

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                return error;
            }

            return null;

        } catch (IOException e) {

            throw new RuntimeException("Could not create the backup", e);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException("El comando fue interrumpido", e);
        }
    }

    private String restaurarConProceso(Path file) {

        ProcessBuilder builder = new ProcessBuilder(
                "mysql",
                "-h", mySqlProperties.getHost(),
                "-P", String.valueOf(mySqlProperties.getPort()),
                "-u", mySqlProperties.getUser(),
                mySqlProperties.getDatabase()
        );

        builder.environment().put("MYSQL_PWD", mySqlProperties.getPassword());

        try {

            Process process = builder.start();

            try (java.util.zip.GZIPInputStream gzip =
                    new java.util.zip.GZIPInputStream(Files.newInputStream(file));
                 java.io.OutputStream out = process.getOutputStream()) {

                gzip.transferTo(out);
            }

            String error = leerError(process.getErrorStream());

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                return error;
            }

            return null;

        } catch (IOException e) {

            throw new RuntimeException("Could not restore the backup", e);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException("El comando fue interrumpido", e);
        }
    }

    private String leerError(java.io.InputStream stream) throws IOException {

        StringBuilder error = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            String line;

            while ((line = reader.readLine()) != null) {

                error.append(line).append("\n");
            }
        }

        return error.toString();
    }
}

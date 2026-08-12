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
                    "No se pudo crear el directorio de backups: " + directory,
                    e
            );
        }

        String fileName =
                "aplicacion_"
                + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                + ".sql.gz";

        Path file = directory.resolve(fileName);

        String command = String.format(
                "set -o pipefail; "
                + "mysqldump --single-transaction --routines --triggers --no-tablespaces --databases "
                + "-h %s -P %d -u %s %s | gzip > %s",
                mySqlProperties.getHost(),
                mySqlProperties.getPort(),
                mySqlProperties.getUser(),
                mySqlProperties.getDatabase(),
                file.toAbsolutePath()
        );

        String error = ejecutarComando(command);

        if (error != null) {
            auditLog.log(
                    actorContext.getActor(),
                    BackupAuditLog.ACTION_CREATE,
                    "FALLO al crear backup"
            );
            throw new RuntimeException(
                    "Error creando backup:\n"
                    + error
            );
        }

        auditLog.log(
                actorContext.getActor(),
                BackupAuditLog.ACTION_CREATE,
                "Backup creado: " + fileName
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
                                "Error leyendo backup: "
                                + path.getFileName(),
                                e
                            );
                        }

                    });

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo leer el directorio de backups",
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
                    "El backup no existe: " + fileName
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

        String command = String.format(
                "set -o pipefail; "
                + "gunzip -c %s | mysql -h %s -P %d -u %s %s",
                file.toAbsolutePath(),
                mySqlProperties.getHost(),
                mySqlProperties.getPort(),
                mySqlProperties.getUser(),
                mySqlProperties.getDatabase()
        );

        String error = ejecutarComando(command);

        if (error != null) {
            auditLog.log(
                    actorContext.getActor(),
                    BackupAuditLog.ACTION_RESTORE,
                    "FALLO al restaurar backup: " + fileName
            );
            throw new RuntimeException(
                    "Error restaurando el backup "
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
                    "Error eliminando el backup "
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
                    "No se pudo crear el directorio de backups: " + directory,
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
                    "No se pudo leer el directorio de backups",
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
                            "Error eliminando el backup " + path.getFileName(),
                            e
                    );
                }
            }
        }

        auditLog.log(
                actorContext.getActor(),
                BackupAuditLog.ACTION_CLEANUP,
                "Retención aplicada: "
                + deleted
                + " archivos eliminados (dias="
                + retention.getDaily()
                + ", semanas="
                + retention.getWeekly()
                + ", meses="
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
                    "Nombre de backup no válido: " + fileName
            );
        }

        Path file = directory.resolve(fileName).normalize();

        if (!file.startsWith(directory)) {

            throw new IllegalArgumentException(
                    "Nombre de backup no válido: " + fileName
            );
        }

        return file;
    }

    private String ejecutarComando(String command) {

        ProcessBuilder builder =
                new ProcessBuilder(
                        "bash",
                        "-c",
                        command
                );

        builder.environment().put(
                "MYSQL_PWD",
                mySqlProperties.getPassword()
        );

        try {

            Process process = builder.start();

            StringBuilder error = new StringBuilder();

            try (BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getErrorStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    error.append(line)
                         .append("\n");
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                return error.toString();
            }

            return null;

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo ejecutar el comando",
                    e
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "El comando fue interrumpido",
                    e
            );
        }
    }
}

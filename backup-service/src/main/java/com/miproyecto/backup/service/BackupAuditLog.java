package com.miproyecto.backup.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.miproyecto.backup.config.BackupProperties;

@Service
public class BackupAuditLog {

    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_RESTORE = "RESTORE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_CLEANUP = "CLEANUP";
    public static final String ACTION_DOWNLOAD = "DOWNLOAD";
    public static final String ACTION_SCHEDULED = "SCHEDULED";

    private static final DateTimeFormatter LINE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BackupProperties backupProperties;

    public BackupAuditLog(BackupProperties backupProperties) {
        this.backupProperties = backupProperties;
    }

    public void log(String actor, String action, String detail) {
        String line = String.format(
                "%s | %s | %s | %s",
                LocalDateTime.now().format(LINE_FORMATTER),
                actor,
                action,
                detail
        );
        appendLine(line);
    }

    public String readLog() {
        Path file = Path.of(backupProperties.getLogFile());
        if (!Files.exists(file)) {
            return "";
        }
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo leer el log de backups: " + file,
                    e
            );
        }
    }

    private synchronized void appendLine(String line) {
        Path file = Path.of(backupProperties.getLogFile());

        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo crear el directorio del log: "
                    + file.getParent(),
                    e
            );
        }

        try {
            Files.writeString(
                    file,
                    line + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo escribir en el log de backups: " + file,
                    e
            );
        }
    }
}

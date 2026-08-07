package com.miproyecto.backup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;

import com.miproyecto.backup.config.BackupProperties;
import com.miproyecto.backup.config.MySqlProperties;
import com.miproyecto.backup.model.BackupInfo;
import com.miproyecto.backup.service.MySqlBackupService;

class MySqlBackupServiceTests {

    @TempDir
    Path tempDir;

    private MySqlBackupService service;

    @BeforeEach
    void setUp() {

        BackupProperties backupProperties = new BackupProperties();
        backupProperties.setDirectory(tempDir.toString());

        MySqlProperties mySqlProperties = new MySqlProperties();
        mySqlProperties.setHost("localhost");
        mySqlProperties.setPort(3306);
        mySqlProperties.setDatabase("aplicacion");
        mySqlProperties.setUser("api_user");
        mySqlProperties.setPassword("backup_password");

        service = new MySqlBackupService(backupProperties, mySqlProperties);
    }

    @Test
    void listBackupsReturnsSqlGzFilesInDirectory() throws IOException {

        Files.writeString(
                tempDir.resolve("aplicacion_2026-08-07_10-00-00.sql.gz"),
                "datos"
        );

        Files.writeString(
                tempDir.resolve("notas.txt"),
                "ignorado"
        );

        List<BackupInfo> backups = service.listBackups();

        assertEquals(1, backups.size());
        assertEquals(
                "aplicacion_2026-08-07_10-00-00.sql.gz",
                backups.get(0).getFileName()
        );
    }

    @Test
    void listBackupsCreatesDirectoryWhenMissing() {

        BackupProperties properties = new BackupProperties();
        properties.setDirectory(tempDir.resolve("no-existe").toString());

        MySqlBackupService otro =
                new MySqlBackupService(properties, new MySqlProperties());

        List<BackupInfo> backups = otro.listBackups();

        assertTrue(Files.isDirectory(tempDir.resolve("no-existe")));
        assertTrue(backups.isEmpty());
    }

    @Test
    void downloadBackupReturnsResourceForExistingFile() throws IOException {

        Files.writeString(
                tempDir.resolve("aplicacion_2026-08-07_10-00-00.sql.gz"),
                "datos"
        );

        Resource resource =
                service.downloadBackup("aplicacion_2026-08-07_10-00-00.sql.gz");

        assertEquals(
                "aplicacion_2026-08-07_10-00-00.sql.gz",
                resource.getFilename()
        );
    }

    @Test
    void downloadBackupRejectsTraversalName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.downloadBackup("../../etc/passwd")
        );
    }

    @Test
    void downloadBackupRejectsNonBackupName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.downloadBackup("notas.txt")
        );
    }

    @Test
    void restoreBackupRejectsInvalidName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.restoreBackup("../fuera.sql.gz")
        );
    }

    @Test
    void restoreBackupReturnsFalseWhenFileDoesNotExist() {

        assertFalse(service.restoreBackup("no-existe.sql.gz"));
    }
}

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

    @Test
    void deleteBackupRemovesFile() throws IOException {

        Files.writeString(
                tempDir.resolve("aplicacion_2026-08-07_10-00-00.sql.gz"),
                "datos"
        );

        assertTrue(
                service.deleteBackup("aplicacion_2026-08-07_10-00-00.sql.gz")
        );

        assertFalse(
                Files.exists(tempDir.resolve("aplicacion_2026-08-07_10-00-00.sql.gz"))
        );
    }

    @Test
    void deleteBackupReturnsFalseWhenFileDoesNotExist() {

        assertFalse(service.deleteBackup("no-existe.sql.gz"));
    }

    @Test
    void deleteBackupRejectsInvalidName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteBackup("../fuera.sql.gz")
        );
    }

    @Test
    void cleanupOldBackupsKeepsDailyWindowAndNewestPerWeekAndMonth() throws IOException {

        BackupProperties props = new BackupProperties();
        props.setDirectory(tempDir.toString());
        BackupProperties.Retention retention = new BackupProperties.Retention();
        retention.setDaily(7);
        retention.setWeekly(4);
        retention.setMonthly(12);
        props.setRetention(retention);

        MySqlBackupService cleanupService = new MySqlBackupService(props, new MySqlProperties());

        Path dir = tempDir;
        writeBackup(dir, "aplicacion_2024-01-10_10-00-00.sql.gz");
        writeBackup(dir, "aplicacion_2024-06-10_10-00-00.sql.gz");
        writeBackup(dir, "aplicacion_2024-12-10_10-00-00.sql.gz");
        writeBackup(dir, "aplicacion_2026-01-15_10-00-00.sql.gz");
        writeBackup(dir, "aplicacion_2026-08-01_10-00-00.sql.gz");
        writeBackup(dir, "aplicacion_2026-08-10_10-00-00.sql.gz");

        int deleted = cleanupService.cleanupOldBackups();

        Path oldJan = dir.resolve("aplicacion_2024-01-10_10-00-00.sql.gz");
        Path oldJun = dir.resolve("aplicacion_2024-06-10_10-00-00.sql.gz");
        Path oldDec = dir.resolve("aplicacion_2024-12-10_10-00-00.sql.gz");
        Path recentJan = dir.resolve("aplicacion_2026-01-15_10-00-00.sql.gz");
        Path aug1 = dir.resolve("aplicacion_2026-08-01_10-00-00.sql.gz");
        Path aug10 = dir.resolve("aplicacion_2026-08-10_10-00-00.sql.gz");

        assertEquals(3, deleted);

        assertFalse(Files.exists(oldJan));
        assertFalse(Files.exists(oldJun));
        assertFalse(Files.exists(oldDec));
        assertTrue(Files.exists(recentJan));
        assertTrue(Files.exists(aug1));
        assertTrue(Files.exists(aug10));
    }

    @Test
    void cleanupOldBackupsReturnsZeroWhenDisabled() throws IOException {

        BackupProperties props = new BackupProperties();
        props.setDirectory(tempDir.toString());
        BackupProperties.Retention retention = new BackupProperties.Retention();
        retention.setEnabled(false);
        props.setRetention(retention);

        MySqlBackupService cleanupService = new MySqlBackupService(props, new MySqlProperties());

        Path dir = tempDir;
        writeBackup(dir, "aplicacion_2026-01-15_10-00-00.sql.gz");
        writeBackup(dir, "aplicacion_2026-08-10_10-00-00.sql.gz");

        int deleted = cleanupService.cleanupOldBackups();

        assertEquals(0, deleted);
        assertTrue(Files.exists(dir.resolve("aplicacion_2026-01-15_10-00-00.sql.gz")));
        assertTrue(Files.exists(dir.resolve("aplicacion_2026-08-10_10-00-00.sql.gz")));
    }

    private void writeBackup(Path dir, String fileName) throws IOException {
        Files.writeString(dir.resolve(fileName), "datos");
    }
}

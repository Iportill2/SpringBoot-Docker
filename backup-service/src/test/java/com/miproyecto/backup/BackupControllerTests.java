package com.miproyecto.backup;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.miproyecto.backup.controller.BackupController;
import com.miproyecto.backup.model.BackupInfo;
import com.miproyecto.backup.service.BackupService;

@WebMvcTest(controllers = BackupController.class)
class BackupControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackupService backupService;

    @Test
    void createBackupReturnsPath() throws Exception {
        when(backupService.createBackup())
                .thenReturn("/backups/aplicacion_2026-08-07_10-00-00.sql.gz");

        mockMvc.perform(post("/backups"))
                .andExpect(status().isOk())
                .andExpect(content().string("/backups/aplicacion_2026-08-07_10-00-00.sql.gz"));
    }

    @Test
    void listBackupsReturnsJsonArray() throws Exception {
        BackupInfo info = new BackupInfo(
                "aplicacion_2026-08-07_10-00-00.sql.gz",
                1024,
                LocalDateTime.of(2026, 8, 7, 10, 0));

        when(backupService.listBackups()).thenReturn(List.of(info));

        mockMvc.perform(get("/backups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("aplicacion_2026-08-07_10-00-00.sql.gz"))
                .andExpect(jsonPath("$[0].size").value(1024));
    }

    @Test
    void downloadBackupReturnsResourceBody() throws Exception {
        byte[] data = "contenido".getBytes(StandardCharsets.UTF_8);

        when(backupService.downloadBackup("aplicacion_2026-08-07_10-00-00.sql.gz"))
                .thenReturn(new ByteArrayResource(data));

        mockMvc.perform(get("/backups/aplicacion_2026-08-07_10-00-00.sql.gz"))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertArrayEquals(data, result.getResponse().getContentAsByteArray()));
    }

    @Test
    void restoreBackupReturnsTrue() throws Exception {
        when(backupService.restoreBackup("aplicacion_2026-08-07_10-00-00.sql.gz"))
                .thenReturn(true);

        mockMvc.perform(post("/backups/restore/aplicacion_2026-08-07_10-00-00.sql.gz"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void restoreBackupReturnsFalseWhenFileMissing() throws Exception {
        when(backupService.restoreBackup("no-existe.sql.gz")).thenReturn(false);

        mockMvc.perform(post("/backups/restore/no-existe.sql.gz"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}

package com.miproyecto.clienterest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;

import com.miproyecto.clienterest.controller.BackupController;
import com.miproyecto.clienterest.model.BackupInfo;
import com.miproyecto.clienterest.service.BackupClientService;

@WebMvcTest(controllers = BackupController.class)
class BackupControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BackupClientService backupService;

    @Test
    void createBackupRedirectsToList() throws Exception {
        when(backupService.createBackup()).thenReturn("ok");

        mockMvc.perform(post("/backups/create")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backups"));
    }

    @Test
    void listBackupsShowsViewWithModel() throws Exception {
        BackupInfo backup = new BackupInfo();
        backup.setFileName("backup-2026-08-07.zip");

        when(backupService.listBackups()).thenReturn(List.of(backup));

        mockMvc.perform(get("/backups")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("app/backups"))
                .andExpect(model().attribute("backups", List.of(backup)));
    }

    @Test
    void restoreBackupSuccessShowsMessage() throws Exception {
        when(backupService.restoreBackup("backup-2026-08-07.zip")).thenReturn(true);

        mockMvc.perform(post("/backups/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backups"))
                .andExpect(flash().attribute("message", "Backup restaurado correctamente"));
    }

    @Test
    void restoreBackupFailureShowsError() throws Exception {
        when(backupService.restoreBackup("backup-2026-08-07.zip")).thenReturn(false);

        mockMvc.perform(post("/backups/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backups"))
                .andExpect(flash().attribute("error", "No se pudo restaurar el backup"));
    }

    @Test
    void restoreBackupWithApiErrorShowsDetailedError() throws Exception {
        when(backupService.restoreBackup("backup-2026-08-07.zip"))
                .thenThrow(new RestClientException("conexión rechazada"));

        mockMvc.perform(post("/backups/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backups"))
                .andExpect(flash().attribute(
                        "error", "No se pudo restaurar el backup: conexión rechazada"));
    }

    @Test
    void downloadBackupReturnsAttachmentHeader() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("contenido".getBytes());

        when(backupService.downloadBackup("backup-2026-08-07.zip")).thenReturn(resource);

        mockMvc.perform(get("/backups/download/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"backup-2026-08-07.zip\""));
    }
}

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

        mockMvc.perform(post("/menu/backups/create")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/backups"))
                .andExpect(flash().attribute("popup", "Backup creado correctamente"));
    }

    @Test
    void createBackupDeniedForNonAdmin() throws Exception {
        mockMvc.perform(post("/menu/backups/create")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "EMPLEADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void restoreBackupDeniedForNonAdmin() throws Exception {
        mockMvc.perform(post("/menu/backups/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "EMPLEADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void deleteBackupDeniedForNonAdmin() throws Exception {
        mockMvc.perform(post("/menu/backups/delete/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "EMPLEADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void listBackupsDeniedForNonAdmin() throws Exception {
        mockMvc.perform(get("/menu/backups")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "EMPLEADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void downloadBackupDeniedForNonAdmin() throws Exception {
        mockMvc.perform(get("/menu/backups/download/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "EMPLEADO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void listBackupsShowsViewWithModel() throws Exception {
        BackupInfo backup = new BackupInfo();
        backup.setFileName("backup-2026-08-07.zip");

        when(backupService.listBackups()).thenReturn(List.of(backup));

        mockMvc.perform(get("/menu/backups")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("app/backups"))
                .andExpect(model().attribute("backups", List.of(backup)));
    }

    @Test
    void restoreBackupSuccessShowsPopup() throws Exception {
        when(backupService.restoreBackup("backup-2026-08-07.zip")).thenReturn(true);

        mockMvc.perform(post("/menu/backups/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/backups"))
                .andExpect(flash().attribute("popup", "Backup restaurado correctamente"));
    }

    @Test
    void restoreBackupFailureShowsError() throws Exception {
        when(backupService.restoreBackup("backup-2026-08-07.zip")).thenReturn(false);

        mockMvc.perform(post("/menu/backups/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/backups"))
                .andExpect(flash().attribute("error", "No se pudo restaurar el backup"));
    }

    @Test
    void restoreBackupWithApiErrorShowsDetailedError() throws Exception {
        when(backupService.restoreBackup("backup-2026-08-07.zip"))
                .thenThrow(new RestClientException("conexión rechazada"));

        mockMvc.perform(post("/menu/backups/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/backups"))
                .andExpect(flash().attribute(
                        "error", "No se pudo restaurar el backup: conexión rechazada"));
    }

    @Test
    void downloadBackupReturnsAttachmentHeader() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("contenido".getBytes());

        when(backupService.downloadBackup("backup-2026-08-07.zip")).thenReturn(resource);

        mockMvc.perform(get("/menu/backups/download/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"backup-2026-08-07.zip\""));
    }

    @Test
    void deleteBackupSuccessShowsPopup() throws Exception {
        when(backupService.deleteBackup("backup-2026-08-07.zip")).thenReturn(true);

        mockMvc.perform(post("/menu/backups/delete/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/backups"))
                .andExpect(flash().attribute("popup", "Backup eliminado correctamente"));
    }

    @Test
    void deleteBackupFailureShowsError() throws Exception {
        when(backupService.deleteBackup("backup-2026-08-07.zip")).thenReturn(false);

        mockMvc.perform(post("/menu/backups/delete/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/backups"))
                .andExpect(flash().attribute("error", "No se pudo eliminar el backup"));
    }

    @Test
    void deleteBackupWithApiErrorShowsDetailedError() throws Exception {
        when(backupService.deleteBackup("backup-2026-08-07.zip"))
                .thenThrow(new RestClientException("conexión rechazada"));

        mockMvc.perform(post("/menu/backups/delete/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1)
                        .sessionAttr("role", "ADMIN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/backups"))
                .andExpect(flash().attribute(
                        "error", "No se pudo eliminar el backup: conexión rechazada"));
    }
}

package com.miproyecto.clienterest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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

import com.miproyecto.clienterest.controller.AdminController;
import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.service.AdminService;
import com.miproyecto.clienterest.service.BackupClientService;

@WebMvcTest(controllers = AdminController.class)
class AdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private BackupClientService backupService;

    @Test
    void listUsersShowsAllUsers() throws Exception {
        AdminUserDTO user = new AdminUserDTO();
        user.setId(1);
        user.setUsername("nuevo");

        when(adminService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/menu/admin")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("app/menu-admin"))
                .andExpect(model().attribute("users", List.of(user)));
    }

    @Test
    void createBackupCallsClientAndRedirects() throws Exception {
        mockMvc.perform(post("/menu/admin/backup")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/admin"));

        verify(backupService).createBackup(anyString());
    }

    @Test
    void downloadBackupReturnsAttachmentHeader() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("contenido".getBytes());

        when(backupService.downloadBackup(eq("backup-2026-08-07.zip"), anyString())).thenReturn(resource);

        mockMvc.perform(get("/menu/admin/download/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"backup-2026-08-07.zip\""));
    }

    @Test
    void restoreBackupSuccessShowsMessage() throws Exception {
        when(backupService.restoreBackup(eq("backup-2026-08-07.zip"), anyString())).thenReturn(true);

        mockMvc.perform(post("/menu/admin/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/admin"))
                .andExpect(flash().attribute("message", "Backup restaurado correctamente"));
    }

    @Test
    void restoreBackupFailureShowsError() throws Exception {
        when(backupService.restoreBackup(eq("backup-2026-08-07.zip"), anyString())).thenReturn(false);

        mockMvc.perform(post("/menu/admin/restore/backup-2026-08-07.zip")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/admin"))
                .andExpect(flash().attribute("error", "No se pudo restaurar el backup"));
    }

    @Test
    void approveRedirectsAndCallsService() throws Exception {
        mockMvc.perform(post("/menu/admin/approve/2")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/admin"));

        verify(adminService).approve(2);
    }

    @Test
    void blockRedirectsAndCallsService() throws Exception {
        mockMvc.perform(post("/menu/admin/block/3")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/admin"));

        verify(adminService).block(3);
    }

    @Test
    void reactivateRedirectsAndCallsService() throws Exception {
        mockMvc.perform(post("/menu/admin/reactivate/3")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/admin"));

        verify(adminService).reactivate(3);
    }

    @Test
    void deleteRedirectsAndCallsService() throws Exception {
        mockMvc.perform(post("/menu/admin/delete/4")
                        .sessionAttr("userId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu/admin"));

        verify(adminService).delete(4);
    }
}

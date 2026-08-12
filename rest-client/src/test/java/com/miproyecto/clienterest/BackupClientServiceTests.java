package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.model.BackupInfo;
import com.miproyecto.clienterest.service.BackupClientService;

class BackupClientServiceTests {

    private static final String BASE = "http://localhost:8082";

    @Test
    void listBackupsGetsList() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = builder.build();

        server.expect(requestTo(BASE + "/backups"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"fileName": "backup-1.sql", "size": 1024, "created": "2026-08-07T10:00:00"}]
                        """, MediaType.APPLICATION_JSON));

        BackupClientService service = new BackupClientService(client);

        List<BackupInfo> backups = service.listBackups();

        assertEquals(1, backups.size());
        assertEquals("backup-1.sql", backups.get(0).getFileName());

        server.verify();
    }

    @Test
    void createBackupPostsAndReturnsMessage() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = builder.build();

        server.expect(requestTo(BASE + "/backups"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("backup creado", MediaType.TEXT_PLAIN));

        BackupClientService service = new BackupClientService(client);

        String message = service.createBackup();

        assertEquals("backup creado", message);

        server.verify();
    }

    @Test
    void restoreBackupPostsAndReturnsBoolean() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = builder.build();

        server.expect(requestTo(BASE + "/backups/restore/backup-1.sql"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        BackupClientService service = new BackupClientService(client);

        assertTrue(service.restoreBackup("backup-1.sql"));

        server.verify();
    }

    @Test
    void deleteBackupDeletesAndReturnsBoolean() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = builder.build();

        server.expect(requestTo(BASE + "/backups/backup-1.sql"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        BackupClientService service = new BackupClientService(client);

        assertTrue(service.deleteBackup("backup-1.sql"));

        server.verify();
    }

    @Test
    void downloadBackupGetsResource() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = builder.build();

        server.expect(requestTo(BASE + "/backups/backup-1.sql"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        new ByteArrayResource("datos".getBytes()),
                        MediaType.APPLICATION_OCTET_STREAM));

        BackupClientService service = new BackupClientService(client);

        org.springframework.core.io.Resource resource = service.downloadBackup("backup-1.sql");

        assertNotNull(resource);

        server.verify();
    }
}

package com.miproyecto.clienterest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.TimeEntryDTO;
import com.miproyecto.clienterest.service.TimeEntryService;

class TimeEntryServiceTests {

    private static final String BASE = "http://localhost:8080";

    @AfterEach
    void cleanup() {
        RestClientTestSupport.clearSession();
    }

    @Test
    void startPostsStartUri() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/time-entry/start/1"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer token-abc"))
                .andRespond(withSuccess("""
                        {"id": 1, "date": "2026-08-07", "startTime": "2026-08-07T09:00:00",
                         "endTime": null, "totalMinutesWorked": null}
                        """, MediaType.APPLICATION_JSON));

        TimeEntryService service = new TimeEntryService(client);

        TimeEntryDTO entry = service.start(1);

        assertEquals(1, entry.getId());
        assertNotNull(entry.getStartTime());

        server.verify();
    }

    @Test
    void stopPostsStopUri() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/time-entry/stop/1?pauseMinutes=15"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"id": 1, "date": "2026-08-07", "startTime": "2026-08-07T09:00:00",
                         "endTime": "2026-08-07T17:00:00", "totalMinutesWorked": 480}
                        """, MediaType.APPLICATION_JSON));

        TimeEntryService service = new TimeEntryService(client);

        TimeEntryDTO entry = service.stop(1, 15);

        assertNotNull(entry.getEndTime());
        assertEquals(480, entry.getTotalMinutesWorked());

        server.verify();
    }

    @Test
    void findByMonthGetsEntriesWithQueryParams() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer server = RestClientTestSupport.bindServer(builder);
        RestClient client = RestClientTestSupport.apiRestClient(builder);

        RestClientTestSupport.loginSession("token-abc");

        server.expect(requestTo(BASE + "/api/time-entry/user/1?year=2026&month=8"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id": 1, "date": "2026-08-07", "startTime": "2026-08-07T09:00:00",
                          "endTime": "2026-08-07T17:00:00", "totalMinutesWorked": 480}]
                        """, MediaType.APPLICATION_JSON));

        TimeEntryService service = new TimeEntryService(client);

        List<TimeEntryDTO> entries = service.findByMonth(1, 2026, 8);

        assertEquals(1, entries.size());
        assertEquals(480, entries.get(0).getTotalMinutesWorked());

        server.verify();
    }
}

package com.miproyecto.clienterest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.miproyecto.clienterest.config.JwtRequestInterceptor;

public final class RestClientTestSupport {

    private RestClientTestSupport() {
    }

    public static MockRestServiceServer bindServer(RestClient.Builder builder) {
        return MockRestServiceServer.bindTo(builder).build();
    }

    public static RestClient apiRestClient(RestClient.Builder builder) {
        return builder.requestInterceptor(new JwtRequestInterceptor()).build();
    }

    public static void loginSession(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        request.getSession().setAttribute("jwt", token);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    public static void clearSession() {
        RequestContextHolder.resetRequestAttributes();
    }
}

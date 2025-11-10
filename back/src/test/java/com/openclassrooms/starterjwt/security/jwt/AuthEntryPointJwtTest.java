package com.openclassrooms.starterjwt.security.jwt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class AuthEntryPointJwtTest {

    AuthEntryPointJwt classUnderTest = new AuthEntryPointJwt();

    @Test
    public void testCommence() throws IOException, ServletException {
        // Given
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockedResponse = Mockito.mock(HttpServletResponse.class);
        AuthenticationException mockedAuthException = Mockito.mock(AuthenticationException.class);

        when(mockedRequest.getServletPath()).thenReturn("/api/test");
        when(mockedAuthException.getMessage()).thenReturn("Invalid token");

        // Capture output written to response.getOutputStream()
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                // no-op
            }
        };
        when(mockedResponse.getOutputStream()).thenReturn(servletOutputStream);

        // When
        classUnderTest.commence(mockedRequest, mockedResponse, mockedAuthException);

        // Then: verify headers/status set
        verify(mockedResponse).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(mockedResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Parse JSON written to the output stream and assert fields
        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonBytes = byteArrayOutputStream.toByteArray();
        assertTrue(jsonBytes.length > 0, "Expected JSON body to be written");
        @SuppressWarnings("unchecked")
        Map<String, Object> body = mapper.readValue(jsonBytes, Map.class);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ((Number) body.get("status")).intValue());
        assertEquals("Unauthorized", body.get("error"));
        assertEquals("Invalid token", body.get("message"));
        assertEquals("/api/test", body.get("path"));
    }

}

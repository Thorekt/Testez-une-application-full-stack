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

        // Ceci permet de capturer ce qui est écrit dans le flux de sortie
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
                // inutilisé dans le test
            }
        };
        when(mockedResponse.getOutputStream()).thenReturn(servletOutputStream);

        // When
        classUnderTest.commence(mockedRequest, mockedResponse, mockedAuthException);

        // Then: verifier les status et content type
        verify(mockedResponse).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(mockedResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // ceci permet de vérifier le contenu JSON écrit dans le flux de sortie
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

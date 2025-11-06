package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.Test;

public class LoginRequestTest {

    @Test
    public void testGetAndSetEmail() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        String email = "test@example.com";
        loginRequest.setEmail(email);

        // When
        String result = loginRequest.getEmail();

        // Then
        assert (result.equals(email));
    }

    @Test
    public void testGetAndSetPassword() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        String password = "testpassword";
        loginRequest.setPassword(password);

        // When
        String result = loginRequest.getPassword();

        // Then
        assert (result.equals(password));
    }
}

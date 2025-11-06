package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

public class AuthControllerTest {
    UserRepository mockedUserRepository = Mockito.mock(UserRepository.class);
    AuthenticationManager mockedAuthenticationManager = Mockito.mock(AuthenticationManager.class);
    JwtUtils mockedJwtUtils = Mockito.mock(JwtUtils.class);
    PasswordEncoder mockedPasswordEncoder = Mockito.mock(PasswordEncoder.class);

    AuthController classUnderTest = new AuthController(
            mockedAuthenticationManager,
            mockedPasswordEncoder,
            mockedJwtUtils,
            mockedUserRepository);

    @Test
    public void testAuthenticateUser() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("testpassword");

        // Créer le principal attendu par le controller
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .admin(true)
                .password("pwd")
                .build();

        // Construire un Authentication contenant ce principal
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, // principal
                null, // credentials (sera ignoré)
                userDetails.getAuthorities() // authorities (peut être vide)
        );

        Mockito.when(mockedAuthenticationManager.authenticate(Mockito.any()))
                .thenReturn(authentication);
        Mockito.when(mockedJwtUtils.generateJwtToken(Mockito.any()))
                .thenReturn("mocked-jwt-token");

        // When
        ResponseEntity<?> response = classUnderTest.authenticateUser(loginRequest);

        // Then
        Mockito.verify(mockedAuthenticationManager).authenticate(Mockito.any());
        Mockito.verify(mockedJwtUtils).generateJwtToken(Mockito.any());
        Mockito.verify(mockedUserRepository).findByEmail(loginRequest.getEmail());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testAuthenticateUserAdmin() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("adminpassword");

        // Créer le principal attendu par le controller
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .admin(true)
                .password("pwd")
                .build();

        // Construire un Authentication contenant ce principal
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, // principal
                null, // credentials (sera ignoré)
                userDetails.getAuthorities() // authorities (peut être vide)
        );

        User user = com.openclassrooms.starterjwt.models.User.builder()
                .id(1L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("pwd")
                .admin(true)
                .build();

        Mockito.when(mockedUserRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        Mockito.when(mockedAuthenticationManager.authenticate(Mockito.any()))
                .thenReturn(authentication);
        Mockito.when(mockedJwtUtils.generateJwtToken(Mockito.any()))
                .thenReturn("mocked-jwt-token");

        // When
        ResponseEntity<?> response = classUnderTest.authenticateUser(loginRequest);

        // Then
        Mockito.verify(mockedAuthenticationManager).authenticate(Mockito.any());
        Mockito.verify(mockedJwtUtils).generateJwtToken(Mockito.any());
        Mockito.verify(mockedUserRepository).findByEmail(loginRequest.getEmail());
        assertEquals(200, response.getStatusCode().value());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals(true, jwtResponse.getAdmin());
    }

    @Test
    public void testRegisterUser() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("testpassword");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");

        Mockito.when(mockedUserRepository.existsByEmail(signupRequest.getEmail()))
                .thenReturn(false);

        Mockito.when(mockedUserRepository.save(Mockito.<User>any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(mockedPasswordEncoder.encode(signupRequest.getPassword()))
                .thenReturn("encoded-password");

        // When
        ResponseEntity<?> response = classUnderTest.registerUser(signupRequest);

        // Then
        Mockito.verify(mockedPasswordEncoder).encode(signupRequest.getPassword());
        Mockito.verify(mockedUserRepository).existsByEmail(signupRequest.getEmail());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testRegisterUserEmailExists() {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("testpassword");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");

        Mockito.when(mockedUserRepository.existsByEmail(signupRequest.getEmail()))
                .thenReturn(true);

        // When
        ResponseEntity<?> response = classUnderTest.registerUser(signupRequest);

        // Then
        Mockito.verify(mockedUserRepository).existsByEmail(signupRequest.getEmail());
        assertEquals(400, response.getStatusCode().value());
    }
}

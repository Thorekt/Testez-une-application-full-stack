package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    // mocked to avoid bringing full authentication providers into the test
    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void GivenExistingEmail_WhenRegister_ThenReturnsBadRequest() throws Exception {
        // Given
        String email = "exists@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        String json = "{" +
                "\"email\":\"" + email + "\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Doe\"," +
                "\"password\":\"pwd123\"" +
                "}";

        // When
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        // Then
        MockHttpServletResponse response = result.getResponse();
        org.junit.jupiter.api.Assertions.assertEquals(400, response.getStatus());
        org.junit.jupiter.api.Assertions
                .assertTrue(response.getContentAsString().contains("Error: Email is already taken!"));

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void GivenNewUser_WhenRegister_ThenReturnsOkAndSavedWithEncodedPassword() throws Exception {
        // Given
        String email = "new@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(42L);
            return u;
        });

        String json = mapper.writeValueAsString(new java.util.HashMap<String, Object>() {
            {
                put("email", email);
                put("firstName", "Jane");
                put("lastName", "Doe");
                put("password", "secret");
            }
        });

        // When
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        // Then
        MockHttpServletResponse response = result.getResponse();
        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
        org.junit.jupiter.api.Assertions
                .assertTrue(response.getContentAsString().contains("User registered successfully!"));

        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(
                org.mockito.ArgumentMatchers.argThat((User u) -> passwordEncoder.matches("secret", u.getPassword())));
    }

    @Test
    public void GivenValidCredentials_WhenLogin_ThenReturnsJwtResponse() throws Exception {
        // Given
        String email = "auth@example.com";
        String password = "pw";

        // create UserDetailsImpl principal
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(5L)
                .username(email)
                .firstName("Auth")
                .lastName("User")
                .password(password)
                .admin(false)
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any())).thenReturn(auth);
        when(jwtUtils.generateJwtToken(org.mockito.ArgumentMatchers.eq(auth))).thenReturn("token-xyz");

        // repository used to detect isAdmin in controller
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder()
                .id(5L)
                .email(email)
                .firstName("Auth")
                .lastName("User")
                .password("x")
                .admin(false)
                .build()));

        String json = mapper.writeValueAsString(java.util.Map.of("email", email, "password", password));

        // When
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        // Then
        MockHttpServletResponse response = result.getResponse();
        org.junit.jupiter.api.Assertions.assertEquals(200, response.getStatus());
        org.junit.jupiter.api.Assertions.assertTrue(response.getContentAsString().contains("token-xyz"));

        verify(authenticationManager).authenticate(org.mockito.ArgumentMatchers.any());
        verify(jwtUtils).generateJwtToken(org.mockito.ArgumentMatchers.eq(auth));
    }

    @Test
    public void GivenInvalidAuthentication_WhenLogin_ThenReturnsUnauthorized() throws Exception {
        // Given
        String email = "bad@example.com";
        String password = "pw";

        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Bad credentials") {
                });

        String json = mapper.writeValueAsString(java.util.Map.of("email", email, "password", password));

        // When
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        // Then
        MockHttpServletResponse response = result.getResponse();
        org.junit.jupiter.api.Assertions.assertEquals(401, response.getStatus());
    }
}

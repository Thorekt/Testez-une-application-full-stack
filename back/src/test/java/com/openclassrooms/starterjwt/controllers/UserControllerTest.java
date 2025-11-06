package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;

public class UserControllerTest {
    UserService mockUserService = Mockito.mock(UserService.class);
    UserMapper mockUserMapper = Mockito.mock(UserMapper.class);

    UserController classUnderTest = new UserController(mockUserService, mockUserMapper);

    @Test
    public void testFindById() {
        // Given
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .lastName("Doe")
                .firstName("John")
                .build();

        Mockito.when(mockUserService.findById(userId)).thenReturn(mockUser);

        // When
        ResponseEntity<?> response = classUnderTest.findById(userId.toString());

        // Then
        org.mockito.Mockito.verify(mockUserService).findById(userId);
        org.mockito.Mockito.verify(mockUserMapper).toDto(mockUser);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testFindByIdWhenUserNotFound() {
        // Given
        Long userId = 1L;

        Mockito.when(mockUserService.findById(userId)).thenReturn(null);

        // When
        ResponseEntity<?> response = classUnderTest.findById(userId.toString());

        // Then
        org.mockito.Mockito.verify(mockUserService).findById(userId);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testFindByIdWhenNumberFormatException() {
        // Given
        String invalidUserId = "invalid";

        // When
        ResponseEntity<?> response = classUnderTest.findById(invalidUserId);

        // Then
        assertEquals(400, response.getStatusCode().value());
    }

    // la fonction save() dans UserController est en fait pour supprimer un
    // utilisateur
    @Test
    public void testSave() {
        // Given
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .email("user@example.com")
                .password("password")
                .lastName("Doe")
                .firstName("John")
                .build();

        Mockito.when(mockUserService.findById(userId)).thenReturn(mockUser);

        // préparation d'un security context avec principal UserDetails
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("user@example.com")
                .password("pwd").roles("USER").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // When
        ResponseEntity<?> response = classUnderTest.save(userId.toString());

        // Then
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testSaveWhenUserNotFound() {
        // Given
        Long userId = 1L;
        Mockito.when(mockUserService.findById(userId)).thenReturn(null);

        // When
        ResponseEntity<?> response = classUnderTest.save(userId.toString());

        // Then
        assertEquals(404, response.getStatusCode().value());
        Mockito.verify(mockUserService).findById(userId);
    }

    @Test
    public void testSaveWhenUnauthorized() {
        // Given
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .email("user@example.com")
                .password("password")
                .lastName("Doe")
                .firstName("John")
                .build();

        Mockito.when(mockUserService.findById(userId)).thenReturn(mockUser);

        // préparation d'un security context avec principal différent
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("other@example.com")
                .password("pwd").roles("USER").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // When
        ResponseEntity<?> response = classUnderTest.save(userId.toString());

        // Then
        assertEquals(401, response.getStatusCode().value());
        Mockito.verify(mockUserService).findById(userId);
    }

    @Test
    public void testSaveWhenNumberFormatException() {
        // Given
        String invalidUserId = "invalid";

        // When
        ResponseEntity<?> response = classUnderTest.save(invalidUserId);

        // Then
        assertEquals(400, response.getStatusCode().value());
        Mockito.verify(mockUserService, Mockito.never()).findById(Mockito.anyLong());
    }
}

package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;

public class SessionServiceTest {
    SessionRepository mockSessionRepository = org.mockito.Mockito.mock(SessionRepository.class);
    UserRepository mockUserRepository = org.mockito.Mockito.mock(UserRepository.class);

    SessionService classUnderTest = new SessionService(mockSessionRepository, mockUserRepository);

    @Test
    public void testCreate() {
        // Given
        Session sessionToCreate = Session.builder()
                .name("Session 1")
                .description("Description 1")
                .build();

        // When
        classUnderTest.create(sessionToCreate);

        // Then
        Mockito.verify(mockSessionRepository).save(sessionToCreate);
    }

    @Test
    public void testDelete() {
        // Given
        Long sessionId = 1L;

        // When
        classUnderTest.delete(sessionId);

        // Then
        Mockito.verify(mockSessionRepository).deleteById(sessionId);
    }

    @Test
    public void testFindAll() {
        // When
        classUnderTest.findAll();

        // Then
        Mockito.verify(mockSessionRepository).findAll();
    }

    @Test
    public void testGetById() {
        // Given
        Long sessionId = 1L;

        // When
        classUnderTest.getById(sessionId);

        // Then
        Mockito.verify(mockSessionRepository)
                .findById(sessionId);
    }

    @Test
    public void testUpdate() {
        // Given
        Session sessionToUpdate = Session.builder()
                .id(1L)
                .name("Updated Session")
                .description("Updated Description")
                .build();
        Long sessionId = 2L;

        // When
        classUnderTest.update(sessionId, sessionToUpdate);

        // Then
        assertEquals(sessionToUpdate.getId(), sessionId);

        Mockito.verify(mockSessionRepository).save(sessionToUpdate);
    }

    @Test
    public void testParticipate() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;

        User mockedUser = User.builder()
                .id(userId)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        Session mockedSession = Session.builder()
                .id(sessionId)
                .users(new java.util.ArrayList<>())
                .build();

        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.of(mockedUser));

        when(mockSessionRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.of(mockedSession));

        // When
        classUnderTest.participate(sessionId, userId);

        // Then
        Mockito.verify(mockSessionRepository).findById(sessionId);
        Mockito.verify(mockUserRepository).findById(userId);
        Mockito.verify(mockSessionRepository).save(Mockito.any(Session.class));
    }

    @Test
    public void testParticipateTrownNotFoundExceptionWhenSessionNotFound() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;

        when(mockSessionRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.empty());

        // When

        try {
            classUnderTest.participate(sessionId, userId);
        } catch (Exception e) {
            // Then
            assertEquals(NotFoundException.class, e.getClass());
        }

        // Then
        Mockito.verify(mockSessionRepository).findById(sessionId);
        Mockito.verify(mockUserRepository).findById(userId);
    }

    @Test
    public void testParticipateTrownNotFoundExceptionWhenUserNotFound() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;

        Session mockedSession = Session.builder()
                .id(sessionId)
                .users(new java.util.ArrayList<>())
                .build();

        when(mockSessionRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.of(mockedSession));

        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.empty());

        // When

        try {
            classUnderTest.participate(sessionId, userId);
        } catch (Exception e) {
            // Then
            assertEquals(NotFoundException.class, e.getClass());

        }

        // Then
        Mockito.verify(mockSessionRepository).findById(sessionId);
        Mockito.verify(mockUserRepository).findById(userId);
    }

    @Test
    public void testParticipateTrownBadRequestExceptionWhenAlreadyParticipate() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;

        User mockedUser = User.builder()
                .id(userId)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        Session mockedSession = Session.builder()
                .id(sessionId)
                .users(new java.util.ArrayList<>(java.util.List.of(mockedUser)))
                .build();

        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.of(mockedUser));

        when(mockSessionRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.of(mockedSession));

        // When
        try {
            classUnderTest.participate(sessionId, userId);
        } catch (Exception e) {
            // Then
            assertEquals(BadRequestException.class, e.getClass());
        }

        // Then
        Mockito.verify(mockSessionRepository).findById(sessionId);
        Mockito.verify(mockUserRepository).findById(userId);
    }

    @Test
    public void testNoLongerParticipate() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;
        User mockedUser = User.builder()
                .id(userId)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        Session mockedSession = Session.builder()
                .id(sessionId)
                .users(new java.util.ArrayList<>(java.util.List.of(mockedUser)))
                .build();

        when(mockSessionRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.of(mockedSession));

        // When
        classUnderTest.noLongerParticipate(sessionId, userId);

        // Then
        assertEquals(0, mockedSession.getUsers().size());
        Mockito.verify(mockSessionRepository).findById(sessionId);
        Mockito.verify(mockSessionRepository).save(Mockito.any(Session.class));

    }

    @Test
    public void testNoLongerParticipateTrownNotFoundExceptionWhenSessionNotFound() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;

        when(mockSessionRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.empty());

        // When

        try {
            classUnderTest.noLongerParticipate(sessionId, userId);
        } catch (Exception e) {
            // Then
            assertEquals(NotFoundException.class, e.getClass());
        }

        // Then
        Mockito.verify(mockSessionRepository).findById(sessionId);
    }

    @Test
    public void testNoLongerParticipateTrownBadRequestExceptionWhenNotParticipate() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;

        Session mockedSession = Session.builder()
                .id(sessionId)
                .users(new java.util.ArrayList<>())
                .build();

        when(mockSessionRepository.findById(any(Long.class)))
                .thenReturn(java.util.Optional.of(mockedSession));

        // When
        try {
            classUnderTest.noLongerParticipate(sessionId, userId);
        } catch (Exception e) {
            // Then
            assertEquals(BadRequestException.class, e.getClass());
        }

        // Then
        Mockito.verify(mockSessionRepository).findById(sessionId);
    }

}

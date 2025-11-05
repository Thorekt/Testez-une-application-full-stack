package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import com.openclassrooms.starterjwt.models.Session;

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

        // When
        classUnderTest.participate(sessionId, userId);

        // Then
        Mockito.verify(mockSessionRepository).findById(sessionId);
        Mockito.verify(mockUserRepository).findById(userId);
        Mockito.verify(mockSessionRepository).save(Mockito.any(Session.class));
    }

}

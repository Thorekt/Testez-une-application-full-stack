package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;

public class SessionControllerTest {
    SessionMapper mockedSessionMapper = Mockito.mock(SessionMapper.class);
    SessionService mockedSessionService = Mockito.mock(SessionService.class);

    SessionController sessionController = new SessionController(mockedSessionService, mockedSessionMapper);

    @Test
    public void testFindById() {
        // Given
        Long sessionId = 1L;
        Session session = Session.builder().id(sessionId).build();

        Mockito.when(mockedSessionService.getById(sessionId)).thenReturn(session);

        // When
        ResponseEntity<?> response = sessionController.findById(sessionId.toString());

        // Then
        Mockito.verify(mockedSessionService).getById(sessionId);
        assertEquals(200, response.getStatusCode().value());

    }

    @Test
    public void testFindByIdNotFound() {
        // Given
        Long sessionId = 1L;

        Mockito.when(mockedSessionService.getById(sessionId)).thenReturn(null);

        // When
        ResponseEntity<?> response = sessionController.findById(sessionId.toString());

        // Then
        Mockito.verify(mockedSessionService).getById(sessionId);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testFindByIdInvalidId() {
        // Given
        String invalidSessionId = "invalid";

        // When
        ResponseEntity<?> response = sessionController.findById(invalidSessionId);

        // Then
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testFindAll() {
        // Given

        // When
        ResponseEntity<?> response = sessionController.findAll();

        // Then
        Mockito.verify(mockedSessionService).findAll();
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testCreate() {
        // Given
        Session session = Session.builder().id(1L).build();
        SessionDto sessionDto = mockedSessionMapper.toDto(session);

        // When
        ResponseEntity<?> response = sessionController.create(sessionDto);

        // Then
        Mockito.verify(mockedSessionService).create(mockedSessionMapper.toEntity(sessionDto));
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testUpdate() {
        // Given
        Long sessionId = 1L;
        Session session = Session.builder().id(sessionId).build();
        SessionDto sessionDto = mockedSessionMapper.toDto(session);

        // When
        ResponseEntity<?> response = sessionController.update(sessionId.toString(), sessionDto);

        // Then
        Mockito.verify(mockedSessionService).update(sessionId, mockedSessionMapper.toEntity(sessionDto));
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testUpdateInvalidId() {
        // Given
        String invalidSessionId = "invalid";
        SessionDto sessionDto = new SessionDto();

        // When
        ResponseEntity<?> response = sessionController.update(invalidSessionId, sessionDto);

        // Then
        assertEquals(400, response.getStatusCode().value());
    }

    // la fonction save est utilisée pour supprimer une session

    @Test
    public void testSave() {
        // Given
        Long sessionId = 1L;
        Session session = Session.builder().id(sessionId).build();
        Mockito.when(mockedSessionService.getById(sessionId)).thenReturn(session);

        // When
        ResponseEntity<?> response = sessionController.save(sessionId.toString());

        // Then
        Mockito.verify(mockedSessionService).getById(sessionId);
        Mockito.verify(mockedSessionService).delete(sessionId);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testSaveNotFound() {
        // Given
        Long sessionId = 1L;
        Mockito.when(mockedSessionService.getById(sessionId)).thenReturn(null);

        // When
        ResponseEntity<?> response = sessionController.save(sessionId.toString());

        // Then
        Mockito.verify(mockedSessionService).getById(sessionId);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testSaveInvalidId() {
        // Given
        String invalidSessionId = "invalid";

        // When
        ResponseEntity<?> response = sessionController.save(invalidSessionId);

        // Then
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testParticipate() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;

        // When
        ResponseEntity<?> response = sessionController.participate(sessionId.toString(), userId.toString());

        // Then
        Mockito.verify(mockedSessionService).participate(sessionId, userId);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testParticipateInvalidIds() {
        // Given
        String invalidSessionId = "invalid";
        String invalidUserId = "invalid";

        // When
        ResponseEntity<?> response = sessionController.participate(invalidSessionId, invalidUserId);

        // Then
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testNoLongerParticipate() {
        // Given
        Long sessionId = 1L;
        Long userId = 2L;

        // When
        ResponseEntity<?> response = sessionController.noLongerParticipate(sessionId.toString(), userId.toString());

        // Then
        Mockito.verify(mockedSessionService).noLongerParticipate(sessionId, userId);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testNoLongerParticipateInvalidIds() {
        // Given
        String invalidSessionId = "invalid";
        String invalidUserId = "invalid";

        // When
        ResponseEntity<?> response = sessionController.noLongerParticipate(invalidSessionId, invalidUserId);

        // Then
        assertEquals(400, response.getStatusCode().value());
    }
}

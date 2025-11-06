package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

public class TeacherControllerTest {
    TeacherService mockedTeacherService = Mockito.mock(TeacherService.class);
    TeacherMapper teacherMapper = Mockito.mock(TeacherMapper.class);

    TeacherController classUnderTest = new TeacherController(mockedTeacherService, teacherMapper);

    @Test
    public void testFindById() {
        // Given
        Long teacherId = 1L;
        Teacher mockTeacher = Teacher.builder()
                .id(teacherId)
                .build();

        Mockito.when(mockedTeacherService.findById(teacherId)).thenReturn(mockTeacher);

        // When
        ResponseEntity<?> response = classUnderTest.findById(teacherId.toString());

        // Then
        Mockito.verify(mockedTeacherService, Mockito.times(1)).findById(teacherId);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testFindByIdNotFound() {
        // Given
        Long teacherId = 2L;

        Mockito.when(mockedTeacherService.findById(teacherId)).thenReturn(null);

        // When
        ResponseEntity<?> response = classUnderTest.findById(teacherId.toString());

        // Then
        Mockito.verify(mockedTeacherService, Mockito.times(1)).findById(teacherId);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testFindByIdInvalidId() {
        // Given
        String invalidId = "invalid";

        // When
        ResponseEntity<?> response = classUnderTest.findById(invalidId);

        // Then
        Mockito.verify(mockedTeacherService, Mockito.never()).findById(Mockito.anyLong());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void testFindAll() {
        // When
        ResponseEntity<?> response = classUnderTest.findAll();

        // Then
        Mockito.verify(mockedTeacherService, Mockito.times(1)).findAll();
        assertEquals(200, response.getStatusCode().value());
    }
}

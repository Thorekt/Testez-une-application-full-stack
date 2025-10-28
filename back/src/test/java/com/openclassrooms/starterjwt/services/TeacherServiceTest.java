package com.openclassrooms.starterjwt.services;

import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.repository.TeacherRepository;

public class TeacherServiceTest {

    TeacherRepository mockTeacherRepository = org.mockito.Mockito.mock(TeacherRepository.class);

    TeacherService classUnderTest = new TeacherService(mockTeacherRepository);

    @Test
    public void testFindAll() {
        // Given

        // When
        classUnderTest.findAll();

        // Then
        org.mockito.Mockito.verify(mockTeacherRepository).findAll();
    }

    @Test
    public void testFindById() {
        // Given
        Long teacherId = 1L;

        // When
        classUnderTest.findById(teacherId);

        // Then
        org.mockito.Mockito.verify(mockTeacherRepository).findById(teacherId);
    }

}

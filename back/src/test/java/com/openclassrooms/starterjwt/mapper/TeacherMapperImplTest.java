package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;

/**
 * Unit tests for the generated MapStruct implementation TeacherMapperImpl.
 *
 * Tests cover: toEntity/toDto for single objects and lists, including null
 * inputs
 * and null elements in lists.
 */
public class TeacherMapperImplTest {

    @Test
    public void ToEntityWithNullDtoReturnsNull() {
        // Given
        TeacherMapperImpl mapper = new TeacherMapperImpl();

        // When / Then
        assertNull(mapper.toEntity((TeacherDto) null));
    }

    @Test
    public void ToEntityMapsFields() {
        // Given
        TeacherMapperImpl mapper = new TeacherMapperImpl();
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setFirstName("Jean");
        dto.setLastName("Dupont");
        dto.setCreatedAt(null);
        dto.setUpdatedAt(null);

        // When
        Teacher entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("Jean", entity.getFirstName());
        assertEquals("Dupont", entity.getLastName());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    public void ToEntityListHandlesNullAndListMapping() {
        // Given
        TeacherMapperImpl mapper = new TeacherMapperImpl();

        // When / Then - null list -> null
        assertNull(mapper.toEntity((List<TeacherDto>) null));

        // Given - list with one dto and one null
        TeacherDto dto = new TeacherDto();
        dto.setId(2L);
        dto.setFirstName("A");
        dto.setLastName("B");

        // When
        List<Teacher> entities = mapper.toEntity(Arrays.asList(dto, null));

        // Then
        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertNotNull(entities.get(0));
        assertEquals(2L, entities.get(0).getId());
        assertNull(entities.get(1));
    }

    @Test
    public void ToDtoWithNullEntityReturnsNull() {
        // Given
        TeacherMapperImpl mapper = new TeacherMapperImpl();

        // When / Then
        assertNull(mapper.toDto((Teacher) null));
    }

    @Test
    public void ToDtoMapsFields() {
        // Given
        TeacherMapperImpl mapper = new TeacherMapperImpl();
        Teacher teacher = Teacher.builder().id(3L).firstName("Marie").lastName("Curie").build();

        // When
        TeacherDto dto = mapper.toDto(teacher);

        // Then
        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals("Marie", dto.getFirstName());
        assertEquals("Curie", dto.getLastName());
    }

    @Test
    public void ToDtoListHandlesNullAndListMapping() {
        // Given
        TeacherMapperImpl mapper = new TeacherMapperImpl();

        // When / Then - null list -> null
        assertNull(mapper.toDto((List<Teacher>) null));

        // Given - list with entity and null
        Teacher t = Teacher.builder().id(4L).firstName("T").lastName("L").build();

        // When
        List<TeacherDto> dtos = mapper.toDto(Arrays.asList(null, t));

        // Then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertNull(dtos.get(0));
        assertNotNull(dtos.get(1));
        assertEquals(4L, dtos.get(1).getId());
    }
}

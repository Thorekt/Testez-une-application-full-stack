package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;

/**
 * Unit tests for the generated MapStruct implementation UserMapperImpl.
 *
 * Tests cover: toEntity/toDto for single objects and lists, including null
 * inputs
 * and null elements in lists.
 */
public class UserMapperImplTest {

    @Test
    public void ToEntityWithNullDtoReturnsNull() {
        // Given
        UserMapperImpl mapper = new UserMapperImpl();

        // When / Then
        assertNull(mapper.toEntity((UserDto) null));
    }

    @Test
    public void ToEntityMapsFields() {
        // Given
        UserMapperImpl mapper = new UserMapperImpl();
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("secret");
        dto.setAdmin(true);
        dto.setCreatedAt(null);
        dto.setUpdatedAt(null);

        // When
        User entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("John", entity.getFirstName());
        assertEquals("Doe", entity.getLastName());
        assertEquals("john.doe@example.com", entity.getEmail());
        assertEquals("secret", entity.getPassword());
        assertTrue(entity.isAdmin());
    }

    @Test
    public void ToEntityListHandlesNullAndListMapping() {
        // Given
        UserMapperImpl mapper = new UserMapperImpl();

        // When / Then - null list -> null
        assertNull(mapper.toEntity((List<UserDto>) null));

        // Given - list with one dto and one null
        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setEmail("a@b");
        dto.setPassword("p");
        dto.setAdmin(false);

        // When
        List<User> entities = mapper.toEntity(Arrays.asList(dto, null));

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
        UserMapperImpl mapper = new UserMapperImpl();

        // When / Then
        assertNull(mapper.toDto((User) null));
    }

    @Test
    public void ToDtoMapsFields() {
        // Given
        UserMapperImpl mapper = new UserMapperImpl();
        User user = User.builder().id(3L).firstName("Jane").lastName("Roe").email("jane.roe@example.com").password("pw")
                .admin(false).build();

        // When
        UserDto dto = mapper.toDto(user);

        // Then
        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Roe", dto.getLastName());
        assertEquals("jane.roe@example.com", dto.getEmail());
        assertEquals("pw", dto.getPassword());
        assertFalse(dto.isAdmin());
    }

    @Test
    public void ToDtoListHandlesNullAndListMapping() {
        // Given
        UserMapperImpl mapper = new UserMapperImpl();

        // When / Then - null list -> null
        assertNull(mapper.toDto((List<User>) null));

        // Given - list with entity and null
        User u = User.builder().id(4L).firstName("U").lastName("L").email("u@l").password("p").admin(true).build();

        // When
        List<UserDto> dtos = mapper.toDto(Arrays.asList(null, u));

        // Then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertNull(dtos.get(0));
        assertNotNull(dtos.get(1));
        assertEquals(4L, dtos.get(1).getId());
        assertTrue(dtos.get(1).isAdmin());
    }
}

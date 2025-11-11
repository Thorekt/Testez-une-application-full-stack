package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

/**
 * Unit tests for the generated MapStruct implementation SessionMapperImpl.
 *
 * We inject mocked services (teacherService, userService) into the generated
 * mapper using reflection to control the lookup behaviour.
 */
public class SessionMapperImplTest {
    // Helper to inject mocks into mapper
    private void injectServices(SessionMapperImpl mapper, TeacherService teacherService, UserService userService)
            throws Exception {
        Field tField = mapper.getClass().getSuperclass().getDeclaredField("teacherService");
        tField.setAccessible(true);
        tField.set(mapper, teacherService);

        Field uField = mapper.getClass().getSuperclass().getDeclaredField("userService");
        uField.setAccessible(true);
        uField.set(mapper, userService);
    }

    @Test
    public void ToEntityMapsFieldsAndResolvesRelations() throws Exception {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        TeacherService teacherService = mock(TeacherService.class);
        UserService userService = mock(UserService.class);
        injectServices(mapper, teacherService, userService);

        Teacher teacher = Teacher.builder().id(1L).firstName("T").lastName("L").build();
        User user10 = User.builder().id(10L).firstName("U").lastName("One").email("user10@example.com").password("p10")
                .build();
        User user11 = User.builder().id(11L).firstName("U").lastName("Two").email("user11@example.com").password("p11")
                .build();

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(10L)).thenReturn(user10);
        when(userService.findById(11L)).thenReturn(user11);

        SessionDto dto = new SessionDto();
        dto.setId(5L);
        dto.setName("Yoga");
        dto.setDescription("Desc");
        dto.setTeacher_id(1L);
        dto.setUsers(Arrays.asList(10L, 11L));

        // When
        Session entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(5L, entity.getId());
        assertEquals("Yoga", entity.getName());
        assertNotNull(entity.getTeacher());
        assertEquals(1L, entity.getTeacher().getId());
        assertNotNull(entity.getUsers());
        assertEquals(2, entity.getUsers().size());
    }

    @Test
    public void ToEntityWithNullDtoReturnsNull() {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();

        // When / Then
        assertNull(mapper.toEntity((SessionDto) null));
    }

    @Test
    public void ToEntityWithNullUsersResultsEmptyUsersList() throws Exception {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        TeacherService teacherService = mock(TeacherService.class);
        UserService userService = mock(UserService.class);
        injectServices(mapper, teacherService, userService);

        Teacher teacher = Teacher.builder().id(1L).firstName("T").lastName("L").build();
        when(teacherService.findById(1L)).thenReturn(teacher);

        SessionDto dto = new SessionDto();
        dto.setId(6L);
        dto.setName("Solo");
        dto.setTeacher_id(1L);
        dto.setUsers(null); // should map to empty list

        // When
        Session entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertNotNull(entity.getUsers());
        assertTrue(entity.getUsers().isEmpty());
    }

    @Test
    public void ToEntityListMethodsHandleNullAndListMapping() throws Exception {
        SessionMapperImpl mapper = new SessionMapperImpl();
        // Given / When / Then - null list -> null
        assertNull(mapper.toEntity((java.util.List<SessionDto>) null));

        // Given - simple mapping of list
        TeacherService teacherService = mock(TeacherService.class);
        UserService userService = mock(UserService.class);
        injectServices(mapper, teacherService, userService);

        SessionDto dto = new SessionDto();
        dto.setId(7L);
        dto.setName("L1");

        // When
        java.util.List<Session> entities = mapper.toEntity(Arrays.asList(dto));

        // Then
        assertNotNull(entities);
        assertEquals(1, entities.size());
    }

    @Test
    public void ToDtoMapsFieldsAndUsers() throws Exception {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();

        Teacher teacher = Teacher.builder().id(2L).firstName("TT").lastName("LL").build();
        User user10 = User.builder().id(10L).firstName("U").lastName("One").email("user10@example.com").password("p10")
                .build();
        User user11 = User.builder().id(11L).firstName("U").lastName("Two").email("user11@example.com").password("p11")
                .build();

        Session session = Session.builder().id(8L).name("Back").teacher(teacher).users(Arrays.asList(user10, user11))
                .build();

        // When
        SessionDto dto = mapper.toDto(session);

        // Then
        assertNotNull(dto);
        assertEquals(8L, dto.getId());
        assertEquals("Back", dto.getName());
        assertEquals(2L, dto.getTeacher_id());
        assertEquals(Arrays.asList(10L, 11L), dto.getUsers());
    }

    @Test
    public void ToDtoWithNullSessionReturnsNull() {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();

        // When / Then
        assertNull(mapper.toDto((Session) null));
    }

    @Test
    public void ToDtoWithNullUsersMapsEmptyList() {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        Session session = Session.builder().id(9L).name("NoUsers").users(null).build();

        // When
        SessionDto dto = mapper.toDto(session);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.getUsers());
        assertTrue(dto.getUsers().isEmpty());
    }

    @Test
    public void ToDtoListMethodsHandleNullAndListMapping() {
        SessionMapperImpl mapper = new SessionMapperImpl();
        // Given / When / Then - null list -> null
        assertNull(mapper.toDto((java.util.List<Session>) null));

        // Given - simple mapping of list
        Teacher teacher = Teacher.builder().id(3L).firstName("TT").lastName("LL").build();
        User user = User.builder().id(20L).firstName("U").lastName("X").email("u@example.com").password("p").build();
        Session s = Session.builder().id(15L).name("L2").teacher(teacher).users(Arrays.asList(user)).build();

        // When
        java.util.List<SessionDto> dtos = mapper.toDto(Arrays.asList(s));

        // Then
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(15L, dtos.get(0).getId());
    }

    @Test
    public void ToDtoWithTeacherButNullIdResultsNullTeacherId() {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        // teacher present but id null -> teacher_id should be null
        Teacher teacher = Teacher.builder().id(null).firstName("NoId").build();
        Session session = Session.builder().id(16L).name("TnullId").teacher(teacher).build();

        // When
        SessionDto dto = mapper.toDto(session);

        // Then
        assertNotNull(dto);
        assertNull(dto.getTeacher_id());
    }

    @Test
    public void ToDtoListWithNullEntryPreservesNullEntry() {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        Teacher teacher = Teacher.builder().id(4L).firstName("TT").lastName("LL").build();
        Session session = Session.builder().id(17L).name("HasOne").teacher(teacher).build();

        // When
        java.util.List<SessionDto> dtos = mapper.toDto(Arrays.asList((Session) null, session));

        // Then
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertNull(dtos.get(0));
        assertNotNull(dtos.get(1));
        assertEquals(17L, dtos.get(1).getId());
    }

    @Test
    public void ToDtoWithUserHavingNullIdResultsNullInUsersList() {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        Teacher teacher = Teacher.builder().id(5L).firstName("TT").lastName("LL").build();
        User userNullId = User.builder().id(null).firstName("U").lastName("N").email("u@x").password("p").build();
        Session session = Session.builder().id(18L).name("UserNullId").teacher(teacher).users(Arrays.asList(userNullId))
                .build();

        // When
        SessionDto dto = mapper.toDto(session);

        // Then
        assertNotNull(dto);
        assertNotNull(dto.getUsers());
        assertEquals(1, dto.getUsers().size());
        assertNull(dto.getUsers().get(0));
    }

    @Test
    public void ToDtoWithTeacherNullSetsTeacherIdNull() {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        // teacher is null -> teacher_id should be null
        Session session = Session.builder().id(12L).name("NoTeacher").teacher(null).users(Arrays.asList()).build();

        // When
        SessionDto dto = mapper.toDto(session);

        // Then
        assertNotNull(dto);
        assertNull(dto.getTeacher_id());
    }

    @Test
    public void ToEntityWithNullTeacherIdSetsTeacherNull() throws Exception {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        TeacherService teacherService = mock(TeacherService.class);
        UserService userService = mock(UserService.class);
        injectServices(mapper, teacherService, userService);

        SessionDto dto = new SessionDto();
        dto.setId(13L);
        dto.setName("NoTeacherId");
        dto.setTeacher_id(null);
        dto.setUsers(Arrays.asList());

        // When
        Session entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertNull(entity.getTeacher());
    }

    @Test
    public void ToEntityWhenUserServiceReturnsNullInListPreservesNullEntry() throws Exception {
        // Given
        SessionMapperImpl mapper = new SessionMapperImpl();
        TeacherService teacherService = mock(TeacherService.class);
        UserService userService = mock(UserService.class);
        injectServices(mapper, teacherService, userService);

        // teacher ok
        when(teacherService.findById(2L)).thenReturn(Teacher.builder().id(2L).build());
        // userService returns null for the id
        when(userService.findById(42L)).thenReturn(null);

        SessionDto dto = new SessionDto();
        dto.setId(14L);
        dto.setName("UserNull");
        dto.setTeacher_id(2L);
        dto.setUsers(Arrays.asList(42L));

        // When
        Session entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertNotNull(entity.getUsers());
        assertEquals(1, entity.getUsers().size());
        assertNull(entity.getUsers().get(0));
    }

}

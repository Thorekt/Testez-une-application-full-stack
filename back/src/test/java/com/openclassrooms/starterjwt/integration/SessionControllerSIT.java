package com.openclassrooms.starterjwt.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerSIT {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private SessionRepository sessionRepository;

        @MockitoBean
        private TeacherRepository teacherRepository;

        @MockitoBean
        private UserRepository userRepository;

        @Test
        @WithMockUser
        public void givenSessionController_whenFindById_withValidId_thenReturnsSession() throws Exception {
                // Given
                Long id = 1L;
                Date date = new Date();
                Session session = Session.builder()
                                .id(id)
                                .name("Yoga Session")
                                .date(date)
                                .description("Relaxing yoga class")
                                .build();

                when(sessionRepository.findById(id)).thenReturn(Optional.of(session));

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/session/" + id))
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(200, response.getStatus());
                assertTrue(response.getContentAsString().contains("Yoga Session"));
                assertTrue(response.getContentAsString().contains("Relaxing yoga class"));

                verify(sessionRepository).findById(id);
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenFindById_withInvalidId_thenReturnsNotFound() throws Exception {
                // Given
                Long id = 999L;

                when(sessionRepository.findById(id)).thenReturn(java.util.Optional.empty());

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/session/" + id))
                                .andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(404, response.getStatus());

                verify(sessionRepository).findById(id);
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenFindById_withInvalidFormatId_thenReturnsBadRequest() throws Exception {
                // Given
                String invalidId = "abc";

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/session/" + invalidId))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(400, response.getStatus());
        }

        @Test
        public void givenSessionController_whenFindById_withNoUserLoggedIn_thenReturnsUnauthorized() throws Exception {
                // Given
                Long id = 1L;

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/session/" + id))
                                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(401, response.getStatus());
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenFindAll_thenReturnsListOfSessions() throws Exception {
                // Given
                Date date = new Date();
                Session s1 = Session.builder().id(1L).name("Morning Yoga").date(date).description("A.m.").build();
                Session s2 = Session.builder().id(2L).name("Evening Yoga").date(date).description("P.m.").build();

                when(sessionRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/session"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(200, response.getStatus());
                assertTrue(response.getContentAsString().contains("Morning Yoga"));
                assertTrue(response.getContentAsString().contains("Evening Yoga"));

                verify(sessionRepository).findAll();
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenFindAll_withNoSessions_thenReturnsEmptyList() throws Exception {
                // Given
                when(sessionRepository.findAll()).thenReturn(Collections.emptyList());

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/session"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(200, response.getStatus());
                assertTrue(response.getContentAsString().contains("[]"));

                verify(sessionRepository).findAll();
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenCreate_thenReturnsCreatedSession() throws Exception {
                // Given
                Date date = new Date();
                Session saved = Session.builder().id(10L).name("New Session").date(date).description("Desc").build();

                when(teacherRepository.findById(1L))
                                .thenReturn(Optional.of(Teacher.builder().id(1L).firstName("T").lastName("L").build()));
                when(sessionRepository.save(ArgumentMatchers.any(Session.class))).thenReturn(saved);

                String json = "{\"name\":\"New Session\",\"date\":" + date.getTime()
                                + ",\"teacher_id\":1,\"description\":\"Desc\"}";

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/session")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(200, response.getStatus());
                assertTrue(response.getContentAsString().contains("New Session"));

                verify(sessionRepository).save(ArgumentMatchers.any(Session.class));
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenUpdate_thenReturnsUpdatedSession() throws Exception {
                // Given
                Long id = 5L;
                Date date = new Date();
                Session updated = Session.builder().id(id).name("Updated Session").date(date).description("Updated")
                                .build();

                when(teacherRepository.findById(1L))
                                .thenReturn(Optional.of(Teacher.builder().id(1L).firstName("T").lastName("L").build()));
                when(sessionRepository.save(ArgumentMatchers.any(Session.class))).thenReturn(updated);

                String json = "{\"name\":\"Updated Session\",\"date\":" + date.getTime()
                                + ",\"teacher_id\":1,\"description\":\"Updated\"}";

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.put("/api/session/" + id)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(200, response.getStatus());
                assertTrue(response.getContentAsString().contains("Updated Session"));

                verify(sessionRepository).save(ArgumentMatchers.any(Session.class));
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenDelete_thenReturnOk() throws Exception {
                // Given
                Long id = 3L;
                Date date = new Date();
                Session session = Session.builder().id(id).name("To Delete").date(date).description("D").build();

                when(sessionRepository.findById(id)).thenReturn(Optional.of(session));

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/api/session/" + id))
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(200, response.getStatus());

                verify(sessionRepository).findById(id);
                verify(sessionRepository).deleteById(ArgumentMatchers.eq(id));
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenDelete_withInvalidId_thenReturnNotFound() throws Exception {
                // Given
                Long id = 999L;
                when(sessionRepository.findById(id)).thenReturn(java.util.Optional.empty());

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/api/session/" + id))
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(404, response.getStatus());

                verify(sessionRepository).findById(id);
                verify(sessionRepository, never()).deleteById(ArgumentMatchers.anyLong());
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenParticipate_thenReturnOk() throws Exception {
                // Given
                Long id = 7L;
                Long userId = 2L;
                Date date = new Date();
                Session session = Session.builder().id(id).name("S").date(date).description("D")
                                .users(new java.util.ArrayList<>()).build();
                com.openclassrooms.starterjwt.models.User user = com.openclassrooms.starterjwt.models.User.builder()
                                .id(userId)
                                .email("u@x.com").firstName("F").lastName("L").password("p").admin(false).build();

                when(sessionRepository.findById(id)).thenReturn(Optional.of(session));
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/session/" + id + "/participate/" + userId))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(200, response.getStatus());
        }

        @Test
        @WithMockUser
        public void givenSessionController_whenNoLongerParticipate_thenReturnOk() throws Exception {
                // Given
                Long id = 7L;
                Long userId = 2L;
                Date date = new Date();
                com.openclassrooms.starterjwt.models.User user = com.openclassrooms.starterjwt.models.User.builder()
                                .id(userId)
                                .email("u@x.com").firstName("F").lastName("L").password("p").admin(false).build();
                Session session = Session.builder().id(id).name("S").date(date).description("D")
                                .users(new java.util.ArrayList<>(java.util.List.of(user))).build();

                when(sessionRepository.findById(id)).thenReturn(Optional.of(session));
                when(userRepository.findById(userId)).thenReturn(Optional.of(user));

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/api/session/" + id + "/participate/" + userId))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();
                assertEquals(200, response.getStatus());
        }
}

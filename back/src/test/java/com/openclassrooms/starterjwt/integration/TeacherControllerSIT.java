package com.openclassrooms.starterjwt.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

@SpringBootTest
@AutoConfigureMockMvc
class TeacherControllerSIT {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private TeacherRepository teacherRepository;

        @Test
        @WithMockUser
        void givenTeacherController_whenFindById_withValidId_thenReturnsTeacher() throws Exception {
                // Given
                Long id = 1L;
                Teacher teacher = Teacher.builder()
                                .id(id)
                                .firstName("John")
                                .lastName("Doe")
                                .build();

                when(teacherRepository.findById(id))
                                .thenReturn(Optional.of(teacher));

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/teacher/" + id)).andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(200, response.getStatus());
                assertTrue(response.getContentAsString().contains("John"));
                assertTrue(response.getContentAsString().contains("Doe"));

                verify(teacherRepository).findById(id);
        }

        @Test
        @WithMockUser
        void givenTeacherController_whenFindById_withInvalidId_thenReturnsNotFound() throws Exception {
                // Given
                Long id = 999L;

                when(teacherRepository.findById(id))
                                .thenReturn(Optional.empty());

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/teacher/" + id))
                                .andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(404, response.getStatus());
                verify(teacherRepository).findById(id);
        }

        @Test
        @WithMockUser
        void givenTeacherController_whenFindById_withInvalidFormatId_thenReturnsBadRequest() throws Exception {
                // Given
                String invalidId = "abc";

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/teacher/" + invalidId))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(400, response.getStatus());
        }

        @Test
        void givenTeacherController_whenFindById_withNoUserLoggedIn_thenReturnsUnauthorized() throws Exception {
                // Given
                Long id = 1L;

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/teacher/" + id))
                                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(401, response.getStatus());
        }

        @Test
        @WithMockUser
        public void givenTeacherController_whenFindAll_thenReturnsListOfTeachers() throws Exception {
                // Given
                Teacher teacher1 = Teacher.builder()
                                .id(1L)
                                .firstName("Jane")
                                .lastName("Smith")
                                .build();

                Teacher teacher2 = Teacher.builder()
                                .id(2L)
                                .firstName("Bob")
                                .lastName("Johnson")
                                .build();

                when(teacherRepository.findAll())
                                .thenReturn(Arrays.asList(teacher1, teacher2));

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/teacher"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(200, response.getStatus());
                assertTrue(response.getContentAsString().contains("Jane"));
                assertTrue(response.getContentAsString().contains("Smith"));
                assertTrue(response.getContentAsString().contains("Bob"));
                assertTrue(response.getContentAsString().contains("Johnson"));

                verify(teacherRepository).findAll();
        }

        @Test
        @WithMockUser
        public void givenTeacherController_whenFindAll_withNoTeachers_thenReturnsEmptyListOfTeachers()
                        throws Exception {
                // Given

                when(teacherRepository.findAll())
                                .thenReturn(Arrays.asList());

                // When
                MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/teacher"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                // Then
                MockHttpServletResponse response = result.getResponse();

                assertEquals(200, response.getStatus());
                assertTrue(response.getContentAsString().contains("[]"));

                verify(teacherRepository).findAll();
        }

}

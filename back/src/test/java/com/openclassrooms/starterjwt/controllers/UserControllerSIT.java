package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerSIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    public void givenUserController_whenFindById_withValidId_thenReturnsUser() throws Exception {
        // Given
        Long id = 1L;
        User user = User.builder()
                .id(id)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("password123")
                .admin(false)
                .build();

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        // When
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/" + id))
                .andReturn();

        // Then
        MockHttpServletResponse response = result.getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentAsString().contains("Jane"));
        assertTrue(response.getContentAsString().contains("Smith"));
        assertTrue(response.getContentAsString().contains("jane.smith@example.com"));

        verify(userRepository).findById(id);
    }

    @Test
    @WithMockUser
    public void givenUserController_whenFindById_withInvalidId_thenReturnsNotFound() throws Exception {
        // Given
        Long id = 999L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        // When
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/" + id))
                .andReturn();

        // Then
        MockHttpServletResponse response = result.getResponse();

        assertEquals(404, response.getStatus());

        verify(userRepository).findById(id);
    }

    @Test
    @WithMockUser
    public void givenUserController_whenFindById_withInvalidFormatId_thenReturnsBadRequest() throws Exception {
        // Given
        String invalidId = "abc";

        // When
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/" + invalidId))
                .andReturn();

        // Then
        MockHttpServletResponse response = result.getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    public void givenUserController_whenFindById_withNoUserLoggedIn_thenReturnsUnauthorized() throws Exception {
        // Given
        Long id = 1L;

        // When
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/user/" + id))
                .andReturn();

        // Then
        MockHttpServletResponse response = result.getResponse();

        assertEquals(401, response.getStatus());
    }

}

package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl service;

    @Test
    void loadUserByUsernameWhenUserExistsReturnsUserDetailsImpl() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(42L);
        when(user.getEmail()).thenReturn("bob@example.com");
        when(user.getFirstName()).thenReturn("Bob");
        when(user.getLastName()).thenReturn("Builder");
        when(user.getPassword()).thenReturn("hashed");

        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));

        UserDetails ud = service.loadUserByUsername("bob@example.com");
        assertNotNull(ud);
        assertTrue(ud instanceof UserDetailsImpl);
        UserDetailsImpl impl = (UserDetailsImpl) ud;

        assertEquals(42L, impl.getId());
        assertEquals("bob@example.com", impl.getUsername());
        assertEquals("Bob", impl.getFirstName());
        assertEquals("Builder", impl.getLastName());
        assertEquals("hashed", impl.getPassword());
    }

    @Test
    void loadUserByUsernameWhenNotFoundThrowsUsernameNotFoundException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@example.com"));
    }
}

package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.Test;

public class UserDetailsImplTest {

    @Test
    void gettersAndDefaultUserDetailsFlags() {
        UserDetailsImpl u = UserDetailsImpl.builder()
                .id(1L)
                .username("alice")
                .firstName("Alice")
                .lastName("Smith")
                .admin(true)
                .password("secret")
                .build();

        assertEquals(1L, u.getId());
        assertEquals("alice", u.getUsername());
        assertEquals("Alice", u.getFirstName());
        assertEquals("Smith", u.getLastName());
        assertTrue(u.getAdmin());
        assertEquals("secret", u.getPassword());

        Collection<?> authorities = u.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty(), "Expected no authorities by default");

        assertTrue(u.isAccountNonExpired());
        assertTrue(u.isAccountNonLocked());
        assertTrue(u.isCredentialsNonExpired());
        assertTrue(u.isEnabled());
    }

    @Test
    void equalsUsesId() {
        UserDetailsImpl a = UserDetailsImpl.builder().id(1L).username("a").password("p").build();
        UserDetailsImpl b = UserDetailsImpl.builder().id(1L).username("b").password("q").build();
        UserDetailsImpl c = UserDetailsImpl.builder().id(2L).username("c").password("r").build();

        assertEquals(a, b, "Instances with same id should be equal");
        assertNotEquals(a, c, "Instances with different id should not be equal");
        assertNotEquals(a, null);
        assertNotEquals(a, "some string");
    }

    @Test
    void equalsWhenBothIdsNull() {
        UserDetailsImpl x = UserDetailsImpl.builder().id(null).username("x").password("p").build();
        UserDetailsImpl y = UserDetailsImpl.builder().id(null).username("y").password("q").build();

        // Objects.equals(null, null) == true -> equals should treat two null ids as
        // equal
        assertEquals(x, y);
    }
}

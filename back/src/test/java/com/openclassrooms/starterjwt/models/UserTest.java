package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.starterjwt.models.User;

public class UserTest {

    @Test
    public void testUserBuilder() {
        Long id = 1L;
        String email = "test@example.com";
        String lastName = "Doe";
        String firstName = "John";
        String password = "password123";
        boolean admin = true;
        User user = User.builder()
                .id(id)
                .email(email)
                .lastName(lastName)
                .firstName(firstName)
                .password(password)
                .admin(admin)
                .build();

        assertEquals(id, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(lastName, user.getLastName());
        assertEquals(firstName, user.getFirstName());
        assertEquals(password, user.getPassword());
        assertEquals(admin, user.isAdmin());
    }

}

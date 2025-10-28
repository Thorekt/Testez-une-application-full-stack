package com.openclassrooms.starterjwt.services;

import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.repository.UserRepository;

public class UserServiceTest {

    UserRepository mockUserRepository = org.mockito.Mockito.mock(UserRepository.class);

    UserService classUnderTest = new UserService(mockUserRepository);

    @Test
    public void testDelete() {
        // given
        Long userId = 1L;

        // when
        classUnderTest.delete(userId);

        // then
        org.mockito.Mockito.verify(mockUserRepository).deleteById(userId);
    }

    @Test
    public void testFindById() {
        // given
        Long userId = 1L;

        // when
        classUnderTest.findById(userId);

        // then
        org.mockito.Mockito.verify(mockUserRepository).findById(userId);
    }
}

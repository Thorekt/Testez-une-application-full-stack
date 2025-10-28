package com.openclassrooms.starterjwt.services;

import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.repository.UserRepository;

public class UserServiceTest {

    UserRepository mockUserRepository = org.mockito.Mockito.mock(UserRepository.class);

    UserService classUnderTest = new UserService(mockUserRepository);

    @Test
    public void testDelete() {
        // given

        // when
        classUnderTest.delete(1L);

        // then
        org.mockito.Mockito.verify(mockUserRepository).deleteById(1L);
    }

    @Test
    public void testFindById() {
        // given

        // when
        classUnderTest.findById(1L);

        // then
        org.mockito.Mockito.verify(mockUserRepository).findById(1L);
    }
}

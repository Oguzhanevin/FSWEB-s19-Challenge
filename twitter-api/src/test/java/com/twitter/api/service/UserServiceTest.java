package com.twitter.api.service;

import com.twitter.api.entity.User;
import com.twitter.api.repository.UserRepository;
import com.twitter.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void givenUserObject_whenSaveUser_thenReturnUserObject() {
        // given
        given(userRepository.save(user)).willReturn(user);

        // when
        User savedUser = userService.saveUser(user);

        // then
        assertThat(savedUser).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void givenUserId_whenFindById_thenReturnUserObject() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        User foundUser = userService.findById(1L);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(1L);
    }

    @Test
    public void givenNonExistentUserId_whenFindById_thenThrowException() {
        // given
        given(userRepository.findById(2L)).willReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> userService.findById(2L));
    }

    @Test
    public void givenUsername_whenFindByUsername_thenReturnUserObject() {
        // given
        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

        // when
        Optional<User> foundUser = userService.findByUsername("testuser");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    public void whenFindAllUsers_thenReturnUsersList() {
        // given
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");
        
        given(userRepository.findAll()).willReturn(Arrays.asList(user, user2));

        // when
        List<User> users = userService.findAllUsers();

        // then
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2);
    }
}

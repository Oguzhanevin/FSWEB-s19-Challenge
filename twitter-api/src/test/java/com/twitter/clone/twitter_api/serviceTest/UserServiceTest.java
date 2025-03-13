package com.twitter.clone.twitter_api.serviceTest;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import com.twitter.clone.twitter_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User anotherUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Resetting mocks to ensure clean state for each test
        reset(userRepository, passwordEncoder);

        testUser = createUser(1L, "testUser", "test@example.com", "encodedPassword", Role.USER);
        anotherUser = createUser(2L, "anotherUser", "another@example.com", "encodedPassword", Role.USER);
        adminUser = createUser(3L, "adminUser", null, "encodedPassword", Role.ADMIN);

        setupMockRepository();
    }

    private User createUser(Long id, String username, String email, String password, Role role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    private void setupMockRepository() {
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        lenient().when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        lenient().when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        lenient().when(userRepository.findById(99L)).thenReturn(Optional.empty());

        lenient().when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        lenient().when(userRepository.findByUsername("anotherUser")).thenReturn(Optional.of(anotherUser));
        lenient().when(userRepository.findByUsername("adminUser")).thenReturn(Optional.of(adminUser));

        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void testSaveUser() {
        User newUser = new User("newUser", "rawPassword", "new@example.com", Role.USER, new ArrayList<>());

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User savedUser = userService.saveUser(newUser);

        assertNotNull(savedUser);
        assertEquals("newUser", savedUser.getUsername());
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        User retrievedUser = userService.getUserById(1L);

        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void testFollowUser() {
        when(userRepository.isFollowing(1L, 2L)).thenReturn(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));

        userService.followUser(1L, 2L);

        verify(userRepository, times(1)).save(testUser);
        verify(userRepository, times(1)).save(anotherUser);
    }

    @Test
    void testUnfollowUser() {
        when(userRepository.isFollowing(1L, 2L)).thenReturn(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));

        userService.unfollowUser(1L, 2L);

        verify(userRepository, times(1)).save(testUser);
        verify(userRepository, times(1)).save(anotherUser);
    }

    @Test
    void testDeleteUser_SuccessByOwner() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testUser");

        userService.deleteUser(1L, mockUserDetails);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUser_Unauthorized() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testUser");
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));

        assertThrows(UnauthorizedAccessException.class, () -> userService.deleteUser(2L, mockUserDetails));

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testGetFollowersCount() {
        when(userRepository.getFollowersCount(1L)).thenReturn(5L);

        long count = userService.getFollowersCount(1L);

        assertEquals(5L, count);
        verify(userRepository, times(1)).getFollowersCount(1L);
    }

    @Test
    void testSearchUsers() {
        List<User> mockUsers = List.of(testUser, anotherUser);
        when(userRepository.searchUsers("test")).thenReturn(mockUsers);

        List<User> searchResults = userService.searchUsers("test");

        assertNotNull(searchResults);
        assertEquals(2, searchResults.size());
        verify(userRepository, times(1)).searchUsers("test");
    }
}

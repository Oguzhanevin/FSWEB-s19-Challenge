package com.twitter.clone.twitter_api.serviceTest;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateEmailException;
import com.twitter.clone.twitter_api.exception.DuplicateUsernameException;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import com.twitter.clone.twitter_api.security.JwtUtil;
import com.twitter.clone.twitter_api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        reset(userRepository, passwordEncoder, jwtUtil, authenticationManager);

        // Test verisi olarak bir kullanıcı oluşturuluyor
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);

        // Mock işlemleri, testlerde kullanılan metotları taklit ediyor
        lenient().when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        lenient().when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        lenient().when(jwtUtil.generateToken(anyString())).thenReturn("mocked-jwt-token");
    }

    @Test
    void testRegisterUser_Success() {
        // Yeni bir kullanıcı kaydederken kullanıcı adı ve e-posta kontrol ediliyor
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        // Kullanıcı kaydederken dönen JWT token'ı kontrol ediliyor
        String jwtToken = authService.registerUser("newUser", "new@example.com", "rawPassword");

        assertNotNull(jwtToken);  // Token'ın null olmaması gerektiği kontrol ediliyor
        assertEquals("mocked-jwt-token", jwtToken);  // Dönen token'ın doğru olduğuna bakılıyor

        // Şifre kodlama ve kullanıcı kaydetme işlemlerinin bir kez çağrıldığını doğruluyoruz
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_DuplicateUsername() {
        // Aynı kullanıcı adıyla kayıt yapılmaya çalışıldığında
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        // DuplicateUsernameException fırlatılmasını bekliyoruz
        assertThrows(DuplicateUsernameException.class, () -> authService.registerUser("testUser", "unique@example.com", "password"));
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        // Aynı e-posta ile kayıt yapılmaya çalışıldığında
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // DuplicateEmailException fırlatılmasını bekliyoruz
        assertThrows(DuplicateEmailException.class, () -> authService.registerUser("uniqueUser", "test@example.com", "password"));
    }

    @Test
    void testLoginUser_Success() {
        // Kullanıcı başarılı bir şekilde giriş yaparsa
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("testUser")).thenReturn(Optional.of(testUser));

        // JWT token'ın başarılı bir şekilde döndüğünü doğruluyoruz
        String jwtToken = authService.loginUser("testUser", "rawPassword");

        assertNotNull(jwtToken);  // Token'ın null olmaması gerektiğini kontrol ediyoruz
        assertEquals("mocked-jwt-token", jwtToken);  // Dönen token'ın doğru olduğuna bakıyoruz

        // AuthenticationManager'ın ve JwtUtil'in doğru bir şekilde çağrıldığını doğruluyoruz
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("testUser");
    }

    @Test
    void testLoginUser_BadCredentials() {
        // Geçersiz kullanıcı adı veya şifre girildiğinde
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Geçersiz kullanıcı adı veya şifre!"));

        // BadCredentialsException fırlatılmasını bekliyoruz
        assertThrows(BadCredentialsException.class, () -> authService.loginUser("testUser", "wrongPassword"));
    }

    @Test
    void testLoginUser_UserNotFound() {
        // Kullanıcı adı veya e-posta ile kullanıcı bulunamazsa
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("unknownUser")).thenReturn(Optional.empty());

        // UserNotFoundException fırlatılmasını bekliyoruz
        assertThrows(UserNotFoundException.class, () -> authService.loginUser("unknownUser", "password"));
    }

    @Test
    void testGetCurrentUser_Success() {
        // Geçerli kullanıcıyı almak için güvenlik bağlamı ayarlanıyor
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        // Kullanıcıyı alıyoruz ve doğrulama yapıyoruz
        User currentUser = authService.getCurrentUser();

        assertNotNull(currentUser);  // Kullanıcının null olmaması gerektiğini kontrol ediyoruz
        assertEquals("testUser", currentUser.getUsername());  // Kullanıcı adının doğru olduğuna bakıyoruz

        // UserRepository'in bir kez çağrıldığını doğruluyoruz
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        // Geçerli kullanıcı adı olmayan bir kullanıcıyı almak için güvenlik bağlamı ayarlanıyor
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknownUser");
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        // UserNotFoundException fırlatılmasını bekliyoruz
        assertThrows(UserNotFoundException.class, () -> authService.getCurrentUser());
    }
}

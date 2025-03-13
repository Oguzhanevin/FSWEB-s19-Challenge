package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.security.JwtUtil;
import com.twitter.clone.twitter_api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // Kullanıcı kayıt işlemi
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String token = authService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Kayıt başarılı! JWT: " + token);
    }

    // Kullanıcı giriş işlemi
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        String token = authService.loginUser(user.getUsername(), user.getPassword());
        return ResponseEntity.ok("Giriş başarılı! JWT: " + token);
    }

    // Çıkış işlemi, ancak JWT stateless olduğu için backend tarafında yapılmaz
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Çıkış işlemi frontend tarafından yönetilmelidir.");
    }

    // Kullanıcının giriş yapıp yapmadığını kontrol eden endpoint
    @GetMapping("/check")
    public ResponseEntity<String> checkAuth(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı giriş yapmamış.");
        }
        return ResponseEntity.ok("Giriş yapan kullanıcı: " + jwtUtil.extractUsername(token));
    }

    // Header'dan Bearer token'ı çeken yardımcı metot
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }
}

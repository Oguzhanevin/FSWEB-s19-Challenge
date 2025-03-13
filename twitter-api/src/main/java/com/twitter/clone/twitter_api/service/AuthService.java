package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateEmailException;
import com.twitter.clone.twitter_api.exception.DuplicateUsernameException;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import com.twitter.clone.twitter_api.security.JwtUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * AuthService sınıfı, kullanıcı kimlik doğrulama ve kayıt işlemlerini yönetir.
 */
@Service
@Getter
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * AuthService yapıcı metodu.
     * @param authenticationManager Kimlik doğrulama yöneticisi
     * @param userRepository Kullanıcı veritabanı erişimi
     * @param passwordEncoder Şifre şifreleme işlemleri
     * @param jwtUtil JWT token yönetimi
     */
    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Yeni bir kullanıcı kaydeder.
     * @param username Kullanıcı adı
     * @param email Kullanıcı e-posta adresi
     * @param password Kullanıcı şifresi
     * @return Kullanıcı için oluşturulan JWT token
     * @throws DuplicateUsernameException Eğer kullanıcı adı daha önce alınmışsa
     * @throws DuplicateEmailException Eğer e-posta adresi zaten kullanılıyorsa
     */
    public String registerUser(String username, String email, String password) {
        // Kullanıcı adı kontrolü
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUsernameException("Bu kullanıcı adı zaten kullanımda.");
        }

        // E-posta adresi kontrolü
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Bu e-posta adresi zaten kayıtlı.");
        }

        // Şifreyi güvenli hale getir ve kullanıcıyı oluştur
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, email, Role.USER, Collections.emptyList());

        // Kullanıcıyı veritabanına kaydet
        userRepository.save(user);

        // Kullanıcı için JWT token oluştur ve döndür
        return jwtUtil.generateToken(username);
    }

    /**
     * Kullanıcıyı kimlik doğrulamadan geçirir ve giriş işlemini gerçekleştirir.
     * @param usernameOrEmail Kullanıcı adı veya e-posta
     * @param password Kullanıcı şifresi
     * @return Başarılı giriş sonrası JWT token
     * @throws UserNotFoundException Eğer kullanıcı bulunamazsa
     * @throws BadCredentialsException Eğer giriş bilgileri yanlışsa
     */
    public String loginUser(String usernameOrEmail, String password) {
        try {
            // Kullanıcıyı doğrula
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
            );

            // Yetkilendirme bilgisini güvenlik bağlamına ekle
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Kullanıcıyı veritabanından al
            User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                    .orElseThrow(() -> new UserNotFoundException("Giriş başarısız! Kullanıcı bulunamadı."));

            // Başarılı giriş sonrası JWT token oluştur ve döndür
            return jwtUtil.generateToken(user.getUsername());

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Geçersiz kullanıcı adı veya şifre!");
        }
    }

    /**
     * Şu an giriş yapmış olan kullanıcıyı getirir.
     * @return Giriş yapmış olan kullanıcı
     * @throws UserNotFoundException Eğer giriş yapan kullanıcı bulunamazsa
     */
    public User getCurrentUser() {
        // Mevcut oturumdaki kullanıcı adını al
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Kullanıcıyı veritabanında ara ve döndür
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Giriş yapan kullanıcı bulunamadı."));
    }
}

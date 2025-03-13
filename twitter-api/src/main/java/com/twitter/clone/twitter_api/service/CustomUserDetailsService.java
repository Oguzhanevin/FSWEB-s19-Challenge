package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * CustomUserDetailsService, kullanıcı kimlik doğrulama işlemlerini yöneten servistir.
 * Spring Security'nin UserDetailsService arayüzünü implemente eder.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Kullanıcı verilerine erişim için bağımlılığı enjekte eden yapıcı metod.
     * @param userRepository Kullanıcı veritabanı erişimi
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Verilen kullanıcı adı ile kullanıcıyı yükler.
     * @param username Kullanıcı adı
     * @return Kullanıcı detayları
     * @throws UsernameNotFoundException Eğer kullanıcı bulunamazsa
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Kullanıcıyı veritabanından bul
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // Kullanıcının rolünü GrantedAuthority listesine çevir
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Spring Security'nin UserDetails nesnesini oluştur
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}

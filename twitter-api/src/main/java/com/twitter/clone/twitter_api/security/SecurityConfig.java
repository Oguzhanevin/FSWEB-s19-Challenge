package com.twitter.clone.twitter_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Uygulamanın güvenlik yapılandırmasını yöneten sınıf.
 * JWT kimlik doğrulaması ve yetkilendirme kurallarını içerir.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * SecurityConfig yapıcı metodu.
     * @param jwtUtil JWT işlemleri için yardımcı sınıf
     * @param userDetailsService Kullanıcı bilgilerini yükleyen servis
     */
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Kullanıcı şifrelerini güvenli bir şekilde saklamak için BCrypt şifreleyicisini sağlar.
     * @return BCryptPasswordEncoder nesnesi
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager nesnesini yapılandırır.
     * @param authenticationConfiguration Authentication yapılandırması
     * @return AuthenticationManager nesnesi
     * @throws Exception Yönetilemeyen hata durumlarında
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * JWT kimlik doğrulama filtresini oluşturur.
     * @return JwtFilter nesnesi
     */
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil, userDetailsService);
    }

    /**
     * Uygulamanın güvenlik filtre zincirini oluşturur ve konfigüre eder.
     * @param http HttpSecurity nesnesi
     * @return SecurityFilterChain nesnesi
     * @throws Exception Yönetilemeyen hata durumlarında
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF korumasını devre dışı bırak
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Kimlik doğrulama işlemleri serbest
                        .requestMatchers("/tweet/**").authenticated() // Tweet işlemleri için yetkilendirme gerekli
                        .anyRequest().authenticated() // Diğer tüm istekler yetkilendirme gerektirir
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT stateless çalışır, oturum yönetimi yok
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class) // JWT kimlik doğrulama filtresini ekle
                .formLogin(form -> form.disable()); // Varsayılan giriş formunu devre dışı bırak

        return http.build();
    }
}

package com.twitter.clone.twitter_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT (JSON Web Token) doğrulaması yapan güvenlik filtresi.
 * Her istekte çalışarak kimlik doğrulamasını gerçekleştirir.
 */
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * JwtFilter yapıcı metodu.
     * @param jwtUtil JWT işlemleri için yardımcı sınıf
     * @param userDetailsService Kullanıcı bilgilerini yüklemek için servis
     */
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Her istekte çalışarak JWT'yi doğrular ve kullanıcıyı güvenlik bağlamına ekler.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // İstekten JWT'yi al
        String token = extractTokenFromHeader(request);

        if (token != null) {
            // Token'dan kullanıcı adını çıkar
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Token geçerliyse güvenlik bağlamına kullanıcıyı ekle
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                Collections.singleton(new SimpleGrantedAuthority("USER")));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // İstek zincirine devam et
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP isteğinin Authorization başlığından JWT'yi alır.
     * @param request HTTP isteği
     * @return JWT token veya null
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization başlığı "Bearer " ile başlıyorsa token'ı çıkar
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}

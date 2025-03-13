package com.twitter.clone.twitter_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * JWT işlemlerini yöneten yardımcı sınıf.
 * Token oluşturma, çözme ve doğrulama işlemlerini içerir.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey; // JWT için kullanılan gizli anahtar

    @Value("${jwt.expiration}")
    private long expirationTime; // Token süresi (ms cinsinden)

    /**
     * Belirtilen kullanıcı adı için bir JWT token oluşturur.
     * @param username Kullanıcı adı
     * @return Oluşturulan JWT token
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Token'a kullanıcı adını ekler
                .setIssuedAt(new Date()) // Oluşturulma zamanını ekler
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Geçerlilik süresi belirlenir
                .signWith(SignatureAlgorithm.HS256, secretKey) // Token şifrelenir
                .compact();
    }

    /**
     * JWT token'dan kullanıcı adını çıkarır.
     * @param token JWT token
     * @return Kullanıcı adı
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * JWT token içindeki belirli bir bilgiyi alır.
     * @param token JWT token
     * @param claimsResolver İstenen bilgi
     * @return Çıkarılan bilgi
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * JWT token'daki tüm bilgileri çıkarır.
     * @param token JWT token
     * @return Claims nesnesi
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey) // İmza doğrulaması için anahtar belirlenir
                .parseClaimsJws(token) // Token çözümlenir
                .getBody();
    }

    /**
     * JWT token'ın süresinin dolup dolmadığını kontrol eder.
     * @param token JWT token
     * @return Token süresi dolmuşsa true, aksi halde false
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // Token süresi dolmuşsa ExpiredJwtException fırlatılır
        }
    }

    /**
     * JWT token'dan geçerlilik süresini alır.
     * @param token JWT token
     * @return Token'ın geçerlilik tarihi
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * JWT token'ın geçerli olup olmadığını doğrular.
     * @param token JWT token
     * @param userDetails Kullanıcı bilgileri
     * @return Token geçerliyse true, değilse false
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}

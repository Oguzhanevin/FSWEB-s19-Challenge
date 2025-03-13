package com.twitter.clone.twitter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Bu sınıf, uygulamada oluşabilecek tüm istisnaları merkezi bir noktada ele alır
 * ve uygun HTTP yanıtlarını döndürerek hata yönetimini standartlaştırır.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Yetkisiz erişim durumlarında devreye giren hata yakalayıcı.
     * @param ex UnauthorizedAccessException türündeki istisna
     * @return HTTP 403 Forbidden yanıtı ve hata mesajı
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Kullanıcı bulunamadığında çağrılan hata yakalayıcı.
     * @param ex UserNotFoundException türündeki istisna
     * @return HTTP 404 Not Found yanıtı ve hata mesajı
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Aynı kullanıcı adının tekrar kullanılması durumunda çağrılan hata yakalayıcı.
     * @param ex DuplicateUsernameException türündeki istisna
     * @return HTTP 400 Bad Request yanıtı ve hata mesajı
     */
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<String> handleDuplicateUsername(DuplicateUsernameException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Aynı e-posta adresinin tekrar kullanılması durumunda çağrılan hata yakalayıcı.
     * @param ex DuplicateEmailException türündeki istisna
     * @return HTTP 400 Bad Request yanıtı ve hata mesajı
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Belirtilen tweet bulunamadığında çağrılan hata yakalayıcı.
     * @param ex TweetNotFoundException türündeki istisna
     * @return HTTP 404 Not Found yanıtı ve hata mesajı
     */
    @ExceptionHandler(TweetNotFoundException.class)
    public ResponseEntity<String> handleTweetNotFound(TweetNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Bir tweeti birden fazla kez beğenmeye çalışıldığında çağrılan hata yakalayıcı.
     * @param ex DuplicateLikeException türündeki istisna
     * @return HTTP 400 Bad Request yanıtı ve hata mesajı
     */
    @ExceptionHandler(DuplicateLikeException.class)
    public ResponseEntity<String> handleDuplicateLike(DuplicateLikeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Aynı tweetin tekrar retweet edilmesi durumunda çağrılan hata yakalayıcı.
     * @param ex DuplicateRetweetException türündeki istisna
     * @return HTTP 400 Bad Request yanıtı ve hata mesajı
     */
    @ExceptionHandler(DuplicateRetweetException.class)
    public ResponseEntity<String> handleDuplicateRetweet(DuplicateRetweetException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Belirtilen yorum bulunamadığında çağrılan hata yakalayıcı.
     * @param ex CommentNotFoundException türündeki istisna
     * @return HTTP 404 Not Found yanıtı ve hata mesajı
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFound(CommentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Form doğrulama hataları meydana geldiğinde çalışır.
     * @param ex MethodArgumentNotValidException türündeki hata
     * @return HTTP 400 Bad Request ve hatalı alanlara dair mesajlar
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Beklenmeyen genel hataları ele alan hata yakalayıcı.
     * @param ex Exception türündeki hata
     * @return HTTP 500 Internal Server Error ve genel hata mesajı
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Bilinmeyen bir hata oluştu: " + ex.getMessage());
    }
}

package com.twitter.clone.twitter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Kullanıcı bir tweeti birden fazla kez beğenmeye çalıştığında fırlatılan özel istisna sınıfıdır.
 * Bu sınıf, aynı kullanıcı tarafından aynı tweetin tekrar beğenilmesi durumunda HTTP 409 hatası döndürmek için kullanılır.
 */
@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409 Conflict hatasını döndürmesi için
public class DuplicateLikeException extends RuntimeException {

    /**
     * Belirtilen özel hata mesajı ile istisna oluşturur.
     * @param message Hata mesajı
     */
    public DuplicateLikeException(String message) {
        super(message);
    }

    /**
     * Varsayılan hata mesajı ile istisna oluşturur.
     */
    public DuplicateLikeException() {
        super("Bu tweeti zaten beğendiniz"); // Varsayılan hata mesajı
    }
}

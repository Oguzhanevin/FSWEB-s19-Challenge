package com.twitter.clone.twitter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Kullanıcı aynı tweeti birden fazla kez retweet yapmaya çalıştığında fırlatılan özel istisna sınıfıdır.
 * Bu sınıf, aynı kullanıcı tarafından aynı tweetin tekrar retweet edilmesi durumunda HTTP 409 hatası döndürmek için kullanılır.
 */
@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409 Conflict hatasını döndürmesi için
public class DuplicateRetweetException extends RuntimeException {

    /**
     * Belirtilen özel hata mesajı ile istisna oluşturur.
     * @param message Hata mesajı
     */
    public DuplicateRetweetException(String message) {
        super(message);
    }

    /**
     * Varsayılan hata mesajı ile istisna oluşturur.
     */
    public DuplicateRetweetException() {
        super("Bu tweeti zaten retweet yaptınız"); // Varsayılan hata mesajı
    }
}

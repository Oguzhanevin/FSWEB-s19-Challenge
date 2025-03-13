package com.twitter.clone.twitter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Bir beğeni (Like) bulunamadığında fırlatılan özel istisna sınıfıdır.
 * HTTP 404 (Not Found) hatası döndürür.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class LikeNotFoundException extends RuntimeException {

    /**
     * Hata mesajı ile birlikte istisna nesnesini oluşturur.
     * @param message Hata mesajı
     */
    public LikeNotFoundException(String message) {
        super(message);
    }

    /**
     * Varsayılan hata mesajı ile istisna nesnesini oluşturur.
     */
    public LikeNotFoundException() {
        super("Beğeni bulunamadı");
    }
}

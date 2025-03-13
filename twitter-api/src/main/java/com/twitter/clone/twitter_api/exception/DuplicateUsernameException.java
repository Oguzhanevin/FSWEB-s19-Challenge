package com.twitter.clone.twitter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Kullanıcı adı zaten kullanılıyorsa fırlatılan özel istisna sınıfıdır.
 * Bu sınıf, aynı kullanıcı adıyla yeni bir hesap açılmak istendiğinde HTTP 409 hatası döndürmek için kullanılır.
 */
@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409 Conflict hatasını döndürmesi için
public class DuplicateUsernameException extends RuntimeException {

    /**
     * Belirtilen özel hata mesajı ile istisna oluşturur.
     * @param message Hata mesajı
     */
    public DuplicateUsernameException(String message) {
        super(message);
    }

    /**
     * Varsayılan hata mesajı ile istisna oluşturur.
     */
    public DuplicateUsernameException() {
        super("Bu kullanıcı adı zaten kullanımda"); // Varsayılan hata mesajı
    }
}

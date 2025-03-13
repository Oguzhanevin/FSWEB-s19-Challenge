package com.twitter.clone.twitter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * E-posta adresi zaten kullanıldığında fırlatılan özel istisna sınıfıdır.
 * Bu sınıf, bir kullanıcı zaten kayıtlı bir e-posta adresini kullanmaya çalıştığında HTTP 409 hatası döndürmek için kullanılır.
 */
@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409 Conflict hatasını döndürmesi için
public class DuplicateEmailException extends RuntimeException {

    /**
     * Belirtilen özel hata mesajı ile istisna oluşturur.
     * @param message Hata mesajı
     */
    public DuplicateEmailException(String message) {
        super(message);
    }

    /**
     * Varsayılan hata mesajı ile istisna oluşturur.
     */
    public DuplicateEmailException() {
        super("Bu e-posta adresi zaten kullanımda"); // Varsayılan hata mesajı
    }
}

package com.twitter.clone.twitter_api.exception;

/**
 * Yetkisiz erişim girişimlerinde fırlatılan özel istisna sınıfıdır.
 * Kullanıcının yetkisiz bir işlemi gerçekleştirmeye çalıştığında kullanılır.
 */
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * Hata mesajı ile birlikte istisna nesnesini oluşturur.
     * @param message Hata mesajı
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    /**
     * Varsayılan hata mesajı ile istisna nesnesini oluşturur.
     */
    public UnauthorizedAccessException() {
        super("Yetkisiz erişim girişimi");
    }
}

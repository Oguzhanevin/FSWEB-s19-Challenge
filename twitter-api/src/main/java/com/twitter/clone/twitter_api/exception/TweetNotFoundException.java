package com.twitter.clone.twitter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 durum kodu döndürmesi için
public class TweetNotFoundException extends RuntimeException {

    /**
     * Özel hata mesajı ile istisna fırlatır.
     * @param message Hata mesajı
     */
    public TweetNotFoundException(String message) {
        super(message);
    }

    /**
     * Varsayılan hata mesajı ile istisna fırlatır.
     */
    public TweetNotFoundException() {
        super("Tweet bulunamadı"); // Varsayılan hata mesajı
    }
}

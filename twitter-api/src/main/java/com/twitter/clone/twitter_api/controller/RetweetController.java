package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.Retweet;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateRetweetException;
import com.twitter.clone.twitter_api.exception.RetweetNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import com.twitter.clone.twitter_api.service.RetweetService;
import com.twitter.clone.twitter_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweets")
public class RetweetController {
    private final RetweetService retweetService;
    private final UserService userService;
    private final TweetRepository tweetRepository;

    public RetweetController(RetweetService retweetService, UserService userService, TweetRepository tweetRepository) {
        this.retweetService = retweetService;
        this.userService = userService;
        this.tweetRepository = tweetRepository;
    }

    /**
     * Bir tweet'i retweet etme işlemi
     * @param tweetId Retweet edilecek tweet'in ID'si
     * @param userDetails Giriş yapan kullanıcının bilgileri
     * @return Oluşturulan retweet bilgisi veya hata mesajı
     */
    @PostMapping("/{tweetId}")
    public ResponseEntity<?> createRetweet(@PathVariable Long tweetId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticateUser(userDetails);
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("Tweet bulunamadı!"));

        if (!tweet.isActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bu tweet aktif olmadığı için retweet edilemez!");
        }

        try {
            Retweet retweet = retweetService.addRetweet(tweetId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(retweet);
        } catch (DuplicateRetweetException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bu tweet zaten retweet edilmiş.");
        }
    }

    /**
     * Bir retweet'i silme işlemi
     * @param retweetId Silinecek retweet'in ID'si
     * @param userDetails Giriş yapan kullanıcının bilgileri
     * @return İşlem sonucu mesajı veya hata mesajı
     */
    @DeleteMapping("/{retweetId}")
    public ResponseEntity<?> deleteRetweet(@PathVariable Long retweetId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticateUser(userDetails);

        try {
            retweetService.removeRetweet(retweetId, user);
            return ResponseEntity.ok("Retweet başarıyla silindi.");
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu retweet'i silme yetkiniz yok!");
        } catch (RetweetNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Retweet bulunamadı.");
        }
    }

    /**
     * Kullanıcı kimliğini doğrulayan yardımcı metot
     * @param userDetails Giriş yapan kullanıcının bilgileri
     * @return Doğrulanan kullanıcı nesnesi
     */
    private User authenticateUser(UserDetails userDetails) {
        return userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Yetkisiz işlem! Kullanıcı bulunamadı."));
    }
}

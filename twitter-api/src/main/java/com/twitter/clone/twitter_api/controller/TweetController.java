package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    // Constructor-based dependency injection
    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    /**
     * Yeni bir tweet oluşturur.
     * @param tweet Kullanıcının oluşturmak istediği tweet objesi
     * @param userDetails Kimlik doğrulama bilgileri
     * @return Oluşturulan tweet
     */
    @PostMapping
    public ResponseEntity<Tweet> createTweet(@RequestBody @Valid Tweet tweet, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = tweetService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı!"));

        tweet.setUser(user);
        Tweet createdTweet = tweetService.createTweet(tweet);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTweet);
    }

    /**
     * Kullanıcının tüm tweetlerini getirir.
     * @param principal Kimlik doğrulama bilgileri
     * @return Kullanıcının tweet listesi
     */
    @GetMapping("/user")
    public ResponseEntity<List<Tweet>> getUserTweets(@AuthenticationPrincipal UserDetails principal) {
        User user = tweetService.findUserByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Yetkisiz işlem! Kullanıcı bulunamadı."));

        List<Tweet> tweets = tweetService.getTweetsByUser(user);
        return ResponseEntity.ok(tweets);
    }

    /**
     * Belirli bir tweet'i ID'ye göre getirir.
     * @param id Tweet'in ID'si
     * @return Tweet bilgisi veya 404 hatası
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tweet> getTweetById(@PathVariable Long id) {
        return tweetService.getTweetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Bir tweet'in içeriğini günceller.
     * @param id Güncellenecek tweet'in ID'si
     * @param updatedTweet Güncellenmiş tweet objesi
     * @param principal Kimlik doğrulama bilgileri
     * @return Güncellenmiş tweet veya hata mesajı
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tweet> updateTweet(@PathVariable Long id, @RequestBody @Valid Tweet updatedTweet,
                                             @AuthenticationPrincipal UserDetails principal) {
        User user = tweetService.findUserByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Yetkisiz işlem!"));

        Tweet tweet = tweetService.getTweetById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tweet bulunamadı."));

        // Kullanıcı, kendi tweetini mi güncelliyor veya admin mi?
        if (!tweet.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Tweet updated = tweetService.updateTweet(id, updatedTweet.getContent());
        return ResponseEntity.ok(updated);
    }

    /**
     * Belirli bir tweet'i siler.
     * @param id Silinecek tweet'in ID'si
     * @param principal Kimlik doğrulama bilgileri
     * @return İşlem sonucu
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User user = tweetService.findUserByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Yetkisiz işlem!"));

        Tweet tweet = tweetService.getTweetById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tweet bulunamadı."));

        // Kullanıcı kendi tweetini mi siliyor veya admin mi?
        if (!tweet.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        tweetService.deleteTweet(id);
        return ResponseEntity.noContent().build();
    }
}

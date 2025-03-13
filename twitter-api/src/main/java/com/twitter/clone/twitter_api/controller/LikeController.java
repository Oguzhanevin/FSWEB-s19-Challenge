package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.Like;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateLikeException;
import com.twitter.clone.twitter_api.exception.LikeNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.service.LikeService;
import com.twitter.clone.twitter_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;
    private final UserService userService;

    public LikeController(LikeService likeService, UserService userService) {
        this.likeService = likeService;
        this.userService = userService;
    }

    // Bir tweeti beğenme işlemi
    @PostMapping("/tweet/{tweetId}")
    public ResponseEntity<?> likeTweet(@PathVariable Long tweetId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        try {
            Like newLike = likeService.addLike(tweetId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newLike);
        } catch (DuplicateLikeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bu tweet zaten beğenilmiş.");
        }
    }

    // Bir tweetin beğenisini kaldırma işlemi
    @DeleteMapping("/tweet/{tweetId}")
    public ResponseEntity<?> unlikeTweet(@PathVariable Long tweetId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        User user = getAuthenticatedUser(userDetails);
        try {
            likeService.removeLike(tweetId, user);
            return ResponseEntity.ok("Beğeni başarıyla kaldırıldı.");
        } catch (LikeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Beğeni bulunamadı.");
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu beğeniyi kaldırma yetkiniz bulunmamaktadır.");
        }
    }

    // Kullanıcıyı doğrulayan yardımcı metot
    private User getAuthenticatedUser(UserDetails userDetails) {
        return userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Yetkisiz erişim! Kullanıcı bulunamadı."));
    }
}

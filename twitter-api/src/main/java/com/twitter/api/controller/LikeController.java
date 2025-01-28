package com.twitter.api.controller;

import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import com.twitter.api.repository.TweetRepository;
import com.twitter.api.repository.UserRepository;
import com.twitter.api.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    // Like a tweet
    @PostMapping("/like")
    public ResponseEntity<Map<String, String>> likeTweet(@RequestParam Long tweetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + tweetId));
        
        // Check if user already liked the tweet
        if (likeService.hasUserLikedTweet(tweetId, user.getId())) {
            return new ResponseEntity<>(Map.of("message", "Tweet already liked"), HttpStatus.BAD_REQUEST);
        }
        
        likeService.likeTweet(tweet, user);
        
        return ResponseEntity.ok(Map.of("message", "Tweet liked successfully"));
    }
    
    // Unlike a tweet (dislike)
    @PostMapping("/dislike")
    public ResponseEntity<Map<String, String>> unlikeTweet(@RequestParam Long tweetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + tweetId));
        
        // Check if user has liked the tweet
        if (!likeService.hasUserLikedTweet(tweetId, user.getId())) {
            return new ResponseEntity<>(Map.of("message", "Tweet not liked yet"), HttpStatus.BAD_REQUEST);
        }
        
        likeService.unlikeTweet(tweet, user);
        
        return ResponseEntity.ok(Map.of("message", "Tweet unliked successfully"));
    }
    
    // Get like count for a tweet
    @GetMapping("/like/count")
    public ResponseEntity<Map<String, Integer>> getLikeCount(@RequestParam Long tweetId) {
        // Check if tweet exists
        tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + tweetId));
        
        int likeCount = likeService.getLikeCount(tweetId);
        
        return ResponseEntity.ok(Map.of("count", likeCount));
    }
    
    // Check if user has liked a tweet
    @GetMapping("/like/check")
    public ResponseEntity<Map<String, Boolean>> checkIfUserLikedTweet(@RequestParam Long tweetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if tweet exists
        tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + tweetId));
        
        boolean hasLiked = likeService.hasUserLikedTweet(tweetId, user.getId());
        
        return ResponseEntity.ok(Map.of("liked", hasLiked));
    }
}

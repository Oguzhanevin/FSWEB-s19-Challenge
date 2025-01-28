package com.twitter.api.controller;

import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import com.twitter.api.repository.TweetRepository;
import com.twitter.api.repository.UserRepository;
import com.twitter.api.service.RetweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/retweet")
public class RetweetController {

    @Autowired
    private RetweetService retweetService;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    // Retweet a tweet
    @PostMapping
    public ResponseEntity<Map<String, String>> retweetTweet(@RequestParam Long tweetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Tweet originalTweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + tweetId));
        
        // Check if user already retweeted the tweet
        if (retweetService.hasUserRetweeted(tweetId, user.getId())) {
            return new ResponseEntity<>(Map.of("message", "Tweet already retweeted"), HttpStatus.BAD_REQUEST);
        }
        
        retweetService.retweetTweet(originalTweet, user);
        
        return ResponseEntity.ok(Map.of("message", "Tweet retweeted successfully"));
    }
    
    // Delete a retweet
    @DeleteMapping("/{retweetId}")
    public ResponseEntity<Map<String, String>> deleteRetweet(@PathVariable Long retweetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        retweetService.deleteRetweet(retweetId, user.getId());
        
        return ResponseEntity.ok(Map.of("message", "Retweet deleted successfully"));
    }
    
    // Get retweet count for a tweet
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getRetweetCount(@RequestParam Long tweetId) {
        // Check if tweet exists
        tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + tweetId));
        
        int retweetCount = retweetService.getRetweetCount(tweetId);
        
        return ResponseEntity.ok(Map.of("count", retweetCount));
    }
    
    // Check if user has retweeted a tweet
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkIfUserRetweetedTweet(@RequestParam Long tweetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if tweet exists
        tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + tweetId));
        
        boolean hasRetweeted = retweetService.hasUserRetweeted(tweetId, user.getId());
        
        return ResponseEntity.ok(Map.of("retweeted", hasRetweeted));
    }
}

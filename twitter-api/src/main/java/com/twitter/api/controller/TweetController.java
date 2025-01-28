package com.twitter.api.controller;

import com.twitter.api.dto.TweetDto;
import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import com.twitter.api.repository.UserRepository;
import com.twitter.api.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tweet")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    @Autowired
    private UserRepository userRepository;

    // Create a new tweet
    @PostMapping
    public ResponseEntity<TweetDto> createTweet(@Valid @RequestBody TweetDto tweetDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweet = new Tweet();
        tweet.setContent(tweetDto.getContent());
        tweet.setUser(user);
        tweet.setCreatedAt(LocalDateTime.now());

        Tweet savedTweet = tweetService.saveTweet(tweet);
        
        return new ResponseEntity<>(mapToDto(savedTweet), HttpStatus.CREATED);
    }

    // Get all tweets by user ID
    @GetMapping("/findByUserId")
    public ResponseEntity<List<TweetDto>> getTweetsByUserId(@RequestParam Long userId) {
        List<Tweet> tweets = tweetService.findAllByUserId(userId);
        
        List<TweetDto> tweetDtos = tweets.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(tweetDtos);
    }

    // Get tweet by ID
    @GetMapping("/findById")
    public ResponseEntity<TweetDto> getTweetById(@RequestParam Long id) {
        Tweet tweet = tweetService.findById(id);
        return ResponseEntity.ok(mapToDto(tweet));
    }

    // Update tweet
    @PutMapping("/{id}")
    public ResponseEntity<TweetDto> updateTweet(@PathVariable Long id, 
                                                @Valid @RequestBody TweetDto tweetDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tweet tweetRequest = new Tweet();
        tweetRequest.setContent(tweetDto.getContent());

        Tweet updatedTweet = tweetService.updateTweet(id, tweetRequest, user.getId());
        
        return ResponseEntity.ok(mapToDto(updatedTweet));
    }

    // Delete tweet
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTweet(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isDeleted = tweetService.deleteTweet(id, user.getId());
        
        if (isDeleted) {
            return ResponseEntity.ok("Tweet deleted successfully");
        } else {
            return new ResponseEntity<>("You are not authorized to delete this tweet", 
                                       HttpStatus.UNAUTHORIZED);
        }
    }

    // Helper method to convert Entity to DTO
    private TweetDto mapToDto(Tweet tweet) {
        TweetDto tweetDto = new TweetDto();
        tweetDto.setId(tweet.getId());
        tweetDto.setContent(tweet.getContent());
        tweetDto.setCreatedAt(tweet.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Set user information
        if (tweet.getUser() != null) {
            com.twitter.api.dto.UserDto userDto = new com.twitter.api.dto.UserDto();
            userDto.setId(tweet.getUser().getId());
            userDto.setUsername(tweet.getUser().getUsername());
            userDto.setEmail(tweet.getUser().getEmail());
            userDto.setCreatedAt(tweet.getUser().getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            tweetDto.setUser(userDto);
        }
        
        // Additional stats can be added here
        // tweetDto.setCommentCount(commentRepository.countByTweetId(tweet.getId()));
        // tweetDto.setLikeCount(likeRepository.countByTweetId(tweet.getId()));
        // tweetDto.setRetweetCount(retweetRepository.countByOriginalTweetId(tweet.getId()));
        
        return tweetDto;
    }
}

package com.twitter.api.controller;

import com.twitter.api.dto.CommentDto;
import com.twitter.api.entity.Comment;
import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import com.twitter.api.repository.TweetRepository;
import com.twitter.api.repository.UserRepository;
import com.twitter.api.service.CommentService;
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
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    // Create comment
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Tweet tweet = tweetRepository.findById(commentDto.getTweetId())
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + commentDto.getTweetId()));
        
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);
        comment.setTweet(tweet);
        comment.setCreatedAt(LocalDateTime.now());
        
        Comment savedComment = commentService.saveComment(comment);
        
        return new ResponseEntity<>(mapToDto(savedComment), HttpStatus.CREATED);
    }
    
    // Update comment
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, 
                                                   @Valid @RequestBody CommentDto commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Comment commentRequest = new Comment();
        commentRequest.setContent(commentDto.getContent());
        
        Comment updatedComment = commentService.updateComment(id, commentRequest, user.getId());
        
        return ResponseEntity.ok(mapToDto(updatedComment));
    }
    
    // Delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean isDeleted = commentService.deleteComment(id, user.getId());
        
        if (isDeleted) {
            return ResponseEntity.ok("Comment deleted successfully");
        } else {
            return new ResponseEntity<>("You are not authorized to delete this comment", 
                                       HttpStatus.UNAUTHORIZED);
        }
    }
    
    // Get comments by tweet ID
    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<CommentDto>> getCommentsByTweetId(@PathVariable Long tweetId) {
        List<Comment> comments = commentService.findAllByTweetId(tweetId);
        
        List<CommentDto> commentDtos = comments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(commentDtos);
    }
    
    // Helper method to convert Entity to DTO
    private CommentDto mapToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setTweetId(comment.getTweet().getId());
        commentDto.setCreatedAt(comment.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Set user information
        if (comment.getUser() != null) {
            com.twitter.api.dto.UserDto userDto = new com.twitter.api.dto.UserDto();
            userDto.setId(comment.getUser().getId());
            userDto.setUsername(comment.getUser().getUsername());
            userDto.setEmail(comment.getUser().getEmail());
            userDto.setCreatedAt(comment.getUser().getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            commentDto.setUser(userDto);
        }
        
        return commentDto;
    }
}

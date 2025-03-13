package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.dto.CommentRequest;
import com.twitter.clone.twitter_api.entity.Comment;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.service.CommentService;
import com.twitter.clone.twitter_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    // Yeni yorum oluşturma
    @PostMapping
    public ResponseEntity<Comment> addComment(@Valid @RequestBody CommentRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(userDetails);
        Comment newComment = commentService.addComment(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComment);
    }

    // Yorumu düzenleme
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> editComment(@PathVariable Long commentId,
                                               @RequestBody CommentRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(userDetails);
        Comment modifiedComment = commentService.updateComment(commentId, request.getContent(), user);
        return ResponseEntity.ok(modifiedComment);
    }

    // Yorumu kaldırma
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = validateUser(userDetails);
        commentService.deleteComment(commentId, user);
        return ResponseEntity.noContent().build();
    }

    // Kullanıcı kimliğini doğrulayan yardımcı metot
    private User validateUser(UserDetails userDetails) {
        return userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkiniz yok!"));
    }
}

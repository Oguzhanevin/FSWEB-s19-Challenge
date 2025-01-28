package com.twitter.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    
    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 280, message = "Comment cannot exceed 280 characters")
    private String content;
    
    @NotNull(message = "Tweet ID is required")
    private Long tweetId;
    
    private UserDto user;
    private String createdAt;
}

package com.twitter.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TweetDto {
    private Long id;
    
    @NotBlank(message = "Tweet content cannot be empty")
    @Size(max = 280, message = "Tweet cannot exceed 280 characters")
    private String content;
    
    private UserDto user;
    private String createdAt;
    private int commentCount;
    private int likeCount;
    private int retweetCount;
}

package com.twitter.clone.twitter_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Long tweetId;
    private String content;
}
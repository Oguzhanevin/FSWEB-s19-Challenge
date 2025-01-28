package com.twitter.api.service;

import com.twitter.api.entity.Comment;
import com.twitter.api.entity.Tweet;

import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment);
    Comment findById(Long id);
    List<Comment> findAllByTweet(Tweet tweet);
    List<Comment> findAllByTweetId(Long tweetId);
    boolean deleteComment(Long id, Long userId);
    Comment updateComment(Long id, Comment commentRequest, Long userId);
}

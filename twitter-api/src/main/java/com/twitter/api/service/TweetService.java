package com.twitter.api.service;

import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;

import java.util.List;

public interface TweetService {
    Tweet saveTweet(Tweet tweet);
    Tweet findById(Long id);
    List<Tweet> findAllTweets();
    List<Tweet> findAllByUser(User user);
    List<Tweet> findAllByUserId(Long userId);
    boolean deleteTweet(Long id, Long userId);
    Tweet updateTweet(Long id, Tweet tweetRequest, Long userId);
}

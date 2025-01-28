package com.twitter.api.service;

import com.twitter.api.entity.Retweet;
import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;

public interface RetweetService {
    Retweet retweetTweet(Tweet originalTweet, User user);
    void deleteRetweet(Long retweetId, Long userId);
    boolean hasUserRetweeted(Long tweetId, Long userId);
    int getRetweetCount(Long tweetId);
}

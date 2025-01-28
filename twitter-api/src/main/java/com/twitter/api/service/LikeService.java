package com.twitter.api.service;

import com.twitter.api.entity.Like;
import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;

public interface LikeService {
    Like likeTweet(Tweet tweet, User user);
    void unlikeTweet(Tweet tweet, User user);
    boolean hasUserLikedTweet(Long tweetId, Long userId);
    int getLikeCount(Long tweetId);
}

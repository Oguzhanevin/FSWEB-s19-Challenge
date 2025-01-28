package com.twitter.api.service.impl;

import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import com.twitter.api.repository.TweetRepository;
import com.twitter.api.service.TweetService;
import com.twitter.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserService userService;

    @Autowired
    public TweetServiceImpl(TweetRepository tweetRepository, UserService userService) {
        this.tweetRepository = tweetRepository;
        this.userService = userService;
    }

    @Override
    public Tweet saveTweet(Tweet tweet) {
        return tweetRepository.save(tweet);
    }

    @Override
    public Tweet findById(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tweet not found with id: " + id));
    }

    @Override
    public List<Tweet> findAllTweets() {
        return tweetRepository.findAll();
    }

    @Override
    public List<Tweet> findAllByUser(User user) {
        return tweetRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Tweet> findAllByUserId(Long userId) {
        return tweetRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public boolean deleteTweet(Long id, Long userId) {
        Tweet tweet = findById(id);
        
        // Check if the user is the owner of the tweet
        if (!tweet.getUser().getId().equals(userId)) {
            return false;
        }
        
        tweetRepository.delete(tweet);
        return true;
    }

    @Override
    public Tweet updateTweet(Long id, Tweet tweetRequest, Long userId) {
        Tweet tweet = findById(id);
        
        // Check if the user is the owner of the tweet
        if (!tweet.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this tweet");
        }
        
        tweet.setContent(tweetRequest.getContent());
        return tweetRepository.save(tweet);
    }
}

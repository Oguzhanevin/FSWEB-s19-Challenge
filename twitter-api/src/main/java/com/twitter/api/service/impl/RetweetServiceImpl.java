package com.twitter.api.service.impl;

import com.twitter.api.entity.Retweet;
import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import com.twitter.api.exception.ResourceNotFoundException;
import com.twitter.api.repository.RetweetRepository;
import com.twitter.api.service.RetweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RetweetServiceImpl implements RetweetService {

    @Autowired
    private RetweetRepository retweetRepository;

    @Override
    public Retweet retweetTweet(Tweet originalTweet, User user) {
        Retweet retweet = new Retweet();
        retweet.setOriginalTweet(originalTweet);
        retweet.setUser(user);
        retweet.setCreatedAt(LocalDateTime.now());
        
        return retweetRepository.save(retweet);
    }

    @Override
    public void deleteRetweet(Long retweetId, Long userId) {
        Retweet retweet = retweetRepository.findById(retweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Retweet", "id", retweetId));
        
        // Check if the user is the owner of the retweet
        if (!retweet.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this retweet");
        }
        
        retweetRepository.delete(retweet);
    }

    @Override
    public boolean hasUserRetweeted(Long tweetId, Long userId) {
        return retweetRepository.existsByUserIdAndOriginalTweetId(userId, tweetId);
    }

    @Override
    public int getRetweetCount(Long tweetId) {
        return retweetRepository.countByOriginalTweetId(tweetId);
    }
}

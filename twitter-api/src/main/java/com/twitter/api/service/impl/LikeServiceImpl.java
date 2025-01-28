package com.twitter.api.service.impl;

import com.twitter.api.entity.Like;
import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import com.twitter.api.repository.LikeRepository;
import com.twitter.api.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Override
    public Like likeTweet(Tweet tweet, User user) {
        Like like = new Like();
        like.setTweet(tweet);
        like.setUser(user);
        like.setCreatedAt(LocalDateTime.now());
        
        return likeRepository.save(like);
    }

    @Override
    public void unlikeTweet(Tweet tweet, User user) {
        Optional<Like> likeOptional = likeRepository.findByUserAndTweet(user, tweet);
        
        likeOptional.ifPresent(like -> likeRepository.delete(like));
    }

    @Override
    public boolean hasUserLikedTweet(Long tweetId, Long userId) {
        return likeRepository.existsByUserIdAndTweetId(userId, tweetId);
    }

    @Override
    public int getLikeCount(Long tweetId) {
        return likeRepository.countByTweetId(tweetId);
    }
}

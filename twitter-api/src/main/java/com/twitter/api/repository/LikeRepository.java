package com.twitter.api.repository;

import com.twitter.api.entity.Like;
import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndTweet(User user, Tweet tweet);
    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);
    int countByTweetId(Long tweetId);
}

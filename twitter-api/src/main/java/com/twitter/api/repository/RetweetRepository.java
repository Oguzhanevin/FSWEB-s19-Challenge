package com.twitter.api.repository;

import com.twitter.api.entity.Retweet;
import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    Optional<Retweet> findByUserAndOriginalTweet(User user, Tweet originalTweet);
    boolean existsByUserIdAndOriginalTweetId(Long userId, Long originalTweetId);
    int countByOriginalTweetId(Long originalTweetId);
}

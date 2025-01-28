package com.twitter.api.repository;

import com.twitter.api.entity.Comment;
import com.twitter.api.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTweetOrderByCreatedAtDesc(Tweet tweet);
    List<Comment> findAllByTweetIdOrderByCreatedAtDesc(Long tweetId);
}

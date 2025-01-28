package com.twitter.api.service.impl;

import com.twitter.api.entity.Comment;
import com.twitter.api.entity.Tweet;
import com.twitter.api.exception.ResourceNotFoundException;
import com.twitter.api.repository.CommentRepository;
import com.twitter.api.repository.TweetRepository;
import com.twitter.api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
    }

    @Override
    public List<Comment> findAllByTweet(Tweet tweet) {
        return commentRepository.findAllByTweetOrderByCreatedAtDesc(tweet);
    }

    @Override
    public List<Comment> findAllByTweetId(Long tweetId) {
        return commentRepository.findAllByTweetIdOrderByCreatedAtDesc(tweetId);
    }

    @Override
    public boolean deleteComment(Long id, Long userId) {
        Comment comment = findById(id);
        
        // Check if user is the owner of the comment or the tweet
        if (comment.getUser().getId().equals(userId) || 
            comment.getTweet().getUser().getId().equals(userId)) {
            commentRepository.delete(comment);
            return true;
        }
        
        return false;
    }

    @Override
    public Comment updateComment(Long id, Comment commentRequest, Long userId) {
        Comment comment = findById(id);
        
        // Check if user is the owner of the comment
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this comment");
        }
        
        comment.setContent(commentRequest.getContent());
        
        return commentRepository.save(comment);
    }
}

package com.twitter.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "retweets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "original_tweet_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Retweet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "original_tweet_id", nullable = false)
    private Tweet originalTweet;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}

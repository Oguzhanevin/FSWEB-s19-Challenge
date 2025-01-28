package com.twitter.api.service;

import com.twitter.api.entity.Tweet;
import com.twitter.api.entity.User;
import com.twitter.api.repository.TweetRepository;
import com.twitter.api.service.impl.TweetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TweetServiceTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TweetServiceImpl tweetService;

    private User user;
    private Tweet tweet;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setCreatedAt(LocalDateTime.now());

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setContent("Test tweet content");
        tweet.setUser(user);
        tweet.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void givenTweetObject_whenSaveTweet_thenReturnTweetObject() {
        // given
        given(tweetRepository.save(tweet)).willReturn(tweet);

        // when
        Tweet savedTweet = tweetService.saveTweet(tweet);

        // then
        assertThat(savedTweet).isNotNull();
        verify(tweetRepository, times(1)).save(any(Tweet.class));
    }

    @Test
    public void givenTweetId_whenFindById_thenReturnTweetObject() {
        // given
        given(tweetRepository.findById(1L)).willReturn(Optional.of(tweet));

        // when
        Tweet foundTweet = tweetService.findById(1L);

        // then
        assertThat(foundTweet).isNotNull();
        assertThat(foundTweet.getId()).isEqualTo(1L);
    }

    @Test
    public void givenNonExistentTweetId_whenFindById_thenThrowException() {
        // given
        given(tweetRepository.findById(2L)).willReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> tweetService.findById(2L));
    }

    @Test
    public void givenUserId_whenFindAllByUserId_thenReturnTweetsList() {
        // given
        Tweet tweet2 = new Tweet();
        tweet2.setId(2L);
        tweet2.setContent("Another test tweet");
        tweet2.setUser(user);
        
        given(tweetRepository.findAllByUserIdOrderByCreatedAtDesc(1L)).willReturn(Arrays.asList(tweet, tweet2));

        // when
        List<Tweet> tweets = tweetService.findAllByUserId(1L);

        // then
        assertThat(tweets).isNotNull();
        assertThat(tweets.size()).isEqualTo(2);
    }

    @Test
    public void givenTweetIdAndUserId_whenDeleteTweet_thenReturnTrue() {
        // given
        given(tweetRepository.findById(1L)).willReturn(Optional.of(tweet));

        // when
        boolean result = tweetService.deleteTweet(1L, 1L);

        // then
        assertThat(result).isTrue();
        verify(tweetRepository, times(1)).delete(tweet);
    }

    @Test
    public void givenTweetIdAndDifferentUserId_whenDeleteTweet_thenReturnFalse() {
        // given
        given(tweetRepository.findById(1L)).willReturn(Optional.of(tweet));

        // when
        boolean result = tweetService.deleteTweet(1L, 2L);

        // then
        assertThat(result).isFalse();
        verify(tweetRepository, never()).delete(any(Tweet.class));
    }
}

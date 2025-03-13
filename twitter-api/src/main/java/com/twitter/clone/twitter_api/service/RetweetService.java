package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.entity.Retweet;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateRetweetException;
import com.twitter.clone.twitter_api.exception.RetweetNotFoundException;
import com.twitter.clone.twitter_api.exception.TweetNotFoundException;
import com.twitter.clone.twitter_api.repository.RetweetRepository;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * RetweetService, kullanıcıların tweetleri retweet yapmasını ve kaldırmasını yönetir.
 */
@Service
public class RetweetService {

    private final RetweetRepository retweetRepository;
    private final TweetRepository tweetRepository;

    /**
     * RetweetService bağımlılıklarını enjekte eden yapıcı metod.
     * @param retweetRepository Retweet işlemleri için repository
     * @param tweetRepository Tweet işlemleri için repository
     */
    @Autowired
    public RetweetService(RetweetRepository retweetRepository, TweetRepository tweetRepository) {
        this.retweetRepository = retweetRepository;
        this.tweetRepository = tweetRepository;
    }

    /**
     * Bir tweeti retweet yapar.
     * @param tweetId Retweet yapılacak tweetin ID'si
     * @param requestUser Retweet yapan kullanıcı
     * @return Oluşturulan Retweet nesnesi
     * @throws TweetNotFoundException Eğer tweet bulunamazsa
     * @throws DuplicateRetweetException Eğer tweet zaten retweet edilmişse
     */
    public Retweet addRetweet(Long tweetId, User requestUser) {
        // Tweeti veritabanından bul
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı."));

        // Kullanıcı zaten retweet yapmış mı kontrol et
        if (retweetRepository.existsByUserIdAndTweetId(requestUser.getId(), tweetId)) {
            throw new DuplicateRetweetException("Bu tweet zaten retweet edildi.");
        }

        // Yeni retweet oluştur ve kaydet
        Retweet retweet = Retweet.builder()
                .tweet(tweet)
                .user(requestUser)
                .build();

        return retweetRepository.save(retweet);
    }

    /**
     * Belirli bir tweetin retweetlerini getirir.
     * @param tweetId Retweetleri getirilecek tweetin ID'si
     * @return Retweet listesi
     */
    public List<Retweet> getRetweetsByTweetId(Long tweetId) {
        return retweetRepository.findByTweetIdOrderByCreatedAtDesc(tweetId);
    }

    /**
     * Kullanıcının belirli bir tweeti retweet edip etmediğini kontrol eder.
     * @param userId Kullanıcının ID'si
     * @param tweetId Tweetin ID'si
     * @return Retweet (varsa)
     */
    public Optional<Retweet> getRetweetByUserAndTweet(Long userId, Long tweetId) {
        return retweetRepository.findByUserIdAndTweetId(userId, tweetId);
    }

    /**
     * Bir tweetten retweet'i kaldırır.
     * @param tweetId Retweet'in kaldırılacağı tweetin ID'si
     * @param requestUser Retweet'i kaldıran kullanıcı
     * @throws RetweetNotFoundException Eğer retweet bulunamazsa
     */
    public void removeRetweet(Long tweetId, User requestUser) {
        Retweet retweet = retweetRepository.findByUserIdAndTweetId(requestUser.getId(), tweetId)
                .orElseThrow(() -> new RetweetNotFoundException("Bu retweet bulunamadı."));

        retweetRepository.delete(retweet);
    }
}

package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.entity.Like;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateLikeException;
import com.twitter.clone.twitter_api.exception.LikeNotFoundException;
import com.twitter.clone.twitter_api.exception.TweetNotFoundException;
import com.twitter.clone.twitter_api.repository.LikeRepository;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * LikeService, tweetlere beğeni ekleme, kaldırma ve listeleme işlemlerini yönetir.
 */
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final TweetRepository tweetRepository;

    /**
     * LikeService bağımlılıklarını enjekte eden yapıcı metod.
     * @param likeRepository Beğeni işlemleri için repository
     * @param tweetRepository Tweet işlemleri için repository
     */
    @Autowired
    public LikeService(LikeRepository likeRepository, TweetRepository tweetRepository) {
        this.likeRepository = likeRepository;
        this.tweetRepository = tweetRepository;
    }

    /**
     * Belirtilen tweeti beğenir.
     * @param tweetId Beğenilecek tweetin ID'si
     * @param requestUser Beğeni yapan kullanıcı
     * @return Oluşturulan Like nesnesi
     * @throws TweetNotFoundException Eğer tweet bulunamazsa
     * @throws DuplicateLikeException Eğer tweet daha önce beğenilmişse
     */
    public Like addLike(Long tweetId, User requestUser) {
        // Tweeti veritabanından bul
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı."));

        // Kullanıcı zaten tweeti beğenmiş mi kontrol et
        if (likeRepository.findByUserIdAndTweetId(requestUser.getId(), tweetId).isPresent()) {
            throw new DuplicateLikeException("Bu tweet zaten beğenildi.");
        }

        // Yeni beğeni oluştur ve kaydet
        Like like = Like.builder()
                .tweet(tweet)
                .user(requestUser)
                .build();

        return likeRepository.save(like);
    }

    /**
     * Bir tweetin tüm beğenilerini getirir.
     * @param tweetId Beğenileri getirilecek tweetin ID'si
     * @return Beğeni listesi
     */
    public List<Like> getLikesByTweetId(Long tweetId) {
        return likeRepository.findByTweetIdOrderByCreatedAtDesc(tweetId);
    }

    /**
     * Kullanıcının belirli bir tweeti beğenip beğenmediğini kontrol eder.
     * @param userId Kullanıcının ID'si
     * @param tweetId Tweetin ID'si
     * @return Beğeni (varsa)
     */
    public Optional<Like> getLikeByUserAndTweet(Long userId, Long tweetId) {
        return likeRepository.findByUserIdAndTweetId(userId, tweetId);
    }

    /**
     * Belirtilen tweetten beğeniyi kaldırır.
     * @param tweetId Beğeninin kaldırılacağı tweetin ID'si
     * @param requestUser Beğeniyi kaldıran kullanıcı
     * @throws LikeNotFoundException Eğer beğeni bulunamazsa
     */
    public void removeLike(Long tweetId, User requestUser) {
        Like like = likeRepository.findByUserIdAndTweetId(requestUser.getId(), tweetId)
                .orElseThrow(() -> new LikeNotFoundException("Bu tweet için beğeni bulunamadı."));

        likeRepository.delete(like);
    }
}

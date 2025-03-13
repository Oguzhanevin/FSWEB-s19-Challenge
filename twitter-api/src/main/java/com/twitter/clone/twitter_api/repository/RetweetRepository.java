package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Retweet işlemleriyle ilgili veritabanı erişim katmanını yöneten repository arabirimi.
 */
@Repository
public interface RetweetRepository extends JpaRepository<Retweet, Long> {

    /**
     * Belirtilen tweet ID'sine ait retweetleri, oluşturulma tarihine göre azalan sıralama ile getirir.
     * @param tweetId Tweet kimliği
     * @return Sıralanmış retweet listesi
     */
    List<Retweet> findByTweetIdOrderByCreatedAtDesc(Long tweetId);

    /**
     * Belirtilen kullanıcı ID'sine ait retweetleri, oluşturulma tarihine göre azalan sıralama ile getirir.
     * @param userId Kullanıcı kimliği
     * @return Sıralanmış retweet listesi
     */
    List<Retweet> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Belirtilen tweet ID'sine ait tüm retweetleri getirir.
     * @param tweetId Tweet kimliği
     * @return Retweet listesi
     */
    List<Retweet> findByTweetId(Long tweetId);

    /**
     * Belirtilen kullanıcı ID'sine ait tüm retweetleri getirir.
     * @param userId Kullanıcı kimliği
     * @return Retweet listesi
     */
    List<Retweet> findByUserId(Long userId);

    /**
     * Belirtilen kullanıcı ve tweet ID'sine sahip retweeti getirir.
     * @param userId Kullanıcı kimliği
     * @param tweetId Tweet kimliği
     * @return Retweet nesnesi (varsa)
     */
    Optional<Retweet> findByUserIdAndTweetId(Long userId, Long tweetId);

    /**
     * Belirtilen kullanıcı tarafından belirtilen tweetin retweet edilip edilmediğini kontrol eder.
     * @param userId Kullanıcı kimliği
     * @param tweetId Tweet kimliği
     * @return Eğer kullanıcı tweeti retweet etmişse true, aksi halde false döner.
     */
    @Query("SELECT COUNT(r) > 0 FROM Retweet r WHERE r.user.id = :userId AND r.tweet.id = :tweetId")
    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);
}

package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Beğeniler (Like) ile ilgili veritabanı işlemlerini yöneten repository arabirimi.
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * Belirtilen tweet ID'sine ait beğenileri, oluşturulma tarihine göre azalan sıralama ile getirir.
     * @param tweetId Tweet kimliği
     * @return Sıralanmış beğeni listesi
     */
    List<Like> findByTweetIdOrderByCreatedAtDesc(Long tweetId);

    /**
     * Belirtilen kullanıcı ID'sine ait beğenileri, oluşturulma tarihine göre azalan sıralama ile getirir.
     * @param userId Kullanıcı kimliği
     * @return Sıralanmış beğeni listesi
     */
    List<Like> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Belirtilen kullanıcı ID'sine ait tüm beğenileri getirir.
     * @param userId Kullanıcı kimliği
     * @return Beğeni listesi
     */
    List<Like> findByUserId(Long userId);

    /**
     * Belirtilen kullanıcı ve tweet ID'sine sahip beğeniyi getirir.
     * @param userId Kullanıcı kimliği
     * @param tweetId Tweet kimliği
     * @return Beğeni nesnesi (varsa)
     */
    Optional<Like> findByUserIdAndTweetId(Long userId, Long tweetId);
}

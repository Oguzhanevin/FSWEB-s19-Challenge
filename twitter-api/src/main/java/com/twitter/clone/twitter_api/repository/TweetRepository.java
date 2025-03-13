package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Tweet işlemleriyle ilgili veritabanı erişim katmanını yöneten repository arabirimi.
 */
@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    /**
     * Belirtilen kullanıcıya ait tweetleri, oluşturulma tarihine göre azalan sıralama ile getirir.
     * @param userId Kullanıcı kimliği
     * @return Sıralanmış tweet listesi
     */
    List<Tweet> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Tüm tweetleri oluşturulma tarihine göre azalan sıralama ile getirir.
     * @return Sıralanmış tweet listesi
     */
    @Query("SELECT t FROM Tweet t ORDER BY t.createdAt DESC")
    List<Tweet> findAllTweetsSortedByDate();
}

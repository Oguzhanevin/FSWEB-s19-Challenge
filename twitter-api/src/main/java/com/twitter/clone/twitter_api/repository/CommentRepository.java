package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Yorumlar (Comment) ile ilgili veritabanı işlemlerini yöneten repository arabirimi.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Belirtilen tweet ID'sine ait yorumları oluşturulma tarihine göre azalan sıralama ile getirir.
     * @param tweetId Tweet'in kimliği
     * @return Sıralı yorum listesi
     */
    List<Comment> findByTweetIdOrderByCreatedAtDesc(Long tweetId);

    /**
     * Belirtilen kullanıcı ID'sine ait yorumları oluşturulma tarihine göre azalan sıralama ile getirir.
     * @param userId Kullanıcı kimliği
     * @return Sıralı yorum listesi
     */
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Belirtilen ID ve kullanıcı ID'sine sahip yorumu getirir.
     * @param id Yorum kimliği
     * @param userId Kullanıcı kimliği
     * @return Yorum nesnesi (varsa)
     */
    Optional<Comment> findByIdAndUserId(Long id, Long userId);

    /**
     * Bir yorumu, hem yorum ID'si hem de tweet sahibinin ID'sine göre getirir.
     * @param commentId Yorum kimliği
     * @param tweetOwnerId Tweet sahibinin kimliği
     * @return Yorum nesnesi (varsa)
     */
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.tweet.user.id = :tweetOwnerId")
    Optional<Comment> findByIdAndTweetUserId(Long commentId, Long tweetOwnerId);
}

package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Kullanıcı işlemleriyle ilgili veritabanı erişim katmanını yöneten repository arabirimi.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Kullanıcı adına göre kullanıcıyı getirir.
     * @param username Kullanıcı adı
     * @return Kullanıcı (Eğer varsa)
     */
    Optional<User> findByUsername(String username);

    /**
     * E-posta adresine göre kullanıcıyı getirir.
     * @param email Kullanıcının e-posta adresi
     * @return Kullanıcı (Eğer varsa)
     */
    Optional<User> findByEmail(String email);

    /**
     * Kullanıcı adı veya e-posta adresine göre kullanıcıyı getirir.
     * @param identifier Kullanıcı adı veya e-posta adresi
     * @return Kullanıcı (Eğer varsa)
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(String identifier);

    /**
     * Kullanıcı adına veya e-posta adresine göre arama yapar.
     * @param query Arama sorgusu (parçalı eşleşme için)
     * @return Kullanıcı listesi
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:query% OR u.email LIKE %:query%")
    List<User> searchUsers(String query);

    /**
     * Bir kullanıcının başka bir kullanıcıyı takip edip etmediğini kontrol eder.
     * @param userId Kontrol eden kullanıcının kimliği
     * @param targetUserId Hedef kullanıcının kimliği
     * @return Takip ediliyorsa true, değilse false
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM User u JOIN u.following f WHERE u.id = :userId AND f.id = :targetUserId")
    boolean isFollowing(Long userId, Long targetUserId);

    /**
     * Kullanıcının takipçi sayısını döndürür.
     * @param userId Kullanıcı kimliği
     * @return Takipçi sayısı
     */
    @Query("SELECT COUNT(f) FROM User u JOIN u.followers f WHERE u.id = :userId")
    long getFollowersCount(Long userId);
}

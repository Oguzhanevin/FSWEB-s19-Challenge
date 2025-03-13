package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.TweetNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import com.twitter.clone.twitter_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * TweetService, tweet oluşturma, güncelleme, silme ve listeleme işlemlerini yönetir.
 */
@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    /**
     * TweetService bağımlılıklarını enjekte eden yapıcı metod.
     * @param tweetRepository Tweet işlemleri için repository
     * @param userRepository Kullanıcı işlemleri için repository
     */
    @Autowired
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    /**
     * Geçerli olarak oturum açmış kullanıcıyı döndürür.
     * @return Authenticated User
     * @throws UnauthorizedAccessException Eğer kullanıcı bulunamazsa
     */
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("Giriş yapan kullanıcı bulunamadı."));
    }

    /**
     * Yeni bir tweet oluşturur.
     * @param tweet Oluşturulacak tweet nesnesi
     * @return Kaydedilen tweet
     */
    public Tweet createTweet(Tweet tweet) {
        User currentUser = getAuthenticatedUser();
        tweet.setUser(currentUser);
        return tweetRepository.save(tweet);
    }

    /**
     * Belirli bir kullanıcının attığı tüm tweetleri getirir.
     * @param requestUser Tweetleri getirilecek kullanıcı
     * @return Kullanıcının attığı tweetlerin listesi
     */
    public List<Tweet> getTweetsByUser(User requestUser) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(requestUser.getId());
    }

    /**
     * Belirli bir tweeti ID'ye göre getirir.
     * @param id Tweetin ID'si
     * @return Tweet nesnesi (varsa)
     */
    public Optional<Tweet> getTweetById(Long id) {
        return tweetRepository.findById(id);
    }

    /**
     * Belirli bir kullanıcının tweetlerini kullanıcı ID'sine göre getirir.
     * @param userId Kullanıcının ID'si
     * @return Kullanıcının attığı tweetlerin listesi
     */
    public List<Tweet> getTweetsByUserId(Long userId) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Bir tweeti siler (aktifliğini false yaparak pasif hale getirir).
     * @param tweetId Silinecek tweetin ID'si
     * @throws TweetNotFoundException Eğer tweet bulunamazsa
     * @throws UnauthorizedAccessException Eğer kullanıcı yetkisizse
     */
    public void deleteTweet(Long tweetId) {
        User currentUser = getAuthenticatedUser();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı."));

        // Yalnızca tweet sahibi veya admin silme işlemi yapabilir
        if (!tweet.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu tweet'i silme yetkiniz yok!");
        }

        tweet.setActive(false);
        tweetRepository.save(tweet);
    }

    /**
     * Bir tweeti günceller.
     * @param tweetId Güncellenecek tweetin ID'si
     * @param newContent Yeni tweet içeriği
     * @return Güncellenmiş tweet
     * @throws TweetNotFoundException Eğer tweet bulunamazsa
     * @throws UnauthorizedAccessException Eğer kullanıcı yetkisizse
     */
    public Tweet updateTweet(Long tweetId, String newContent) {
        User currentUser = getAuthenticatedUser();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı."));

        // Yalnızca tweet sahibi veya admin güncelleme yapabilir
        if (!tweet.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu tweet'i güncelleme yetkiniz yok!");
        }

        tweet.setContent(newContent);
        return tweetRepository.save(tweet);
    }

    /**
     * Kullanıcı adını kullanarak bir kullanıcıyı bulur.
     * @param username Kullanıcının adı
     * @return Kullanıcı (varsa)
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

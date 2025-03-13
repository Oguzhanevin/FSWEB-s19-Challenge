package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserService, kullanıcı yönetimiyle ilgili işlemleri yönetir.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ✅ BCryptPasswordEncoder yerine PasswordEncoder kullan

    /**
     * UserService bağımlılıklarını enjekte eden yapıcı metod.
     * @param userRepository Kullanıcı işlemleri için repository
     * @param passwordEncoder Kullanıcı şifrelerini güvenli hale getirmek için encoder
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) { // ✅ BCryptPasswordEncoder yerine PasswordEncoder
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Yeni bir kullanıcı kaydeder.
     * @param user Kaydedilecek kullanıcı
     * @return Kaydedilen kullanıcı
     */
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Sistemdeki tüm kullanıcıları getirir.
     * @return Kullanıcı listesi
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Belirtilen ID'ye sahip kullanıcıyı getirir.
     * @param id Kullanıcı ID'si
     * @return Kullanıcı nesnesi
     * @throws UserNotFoundException Kullanıcı bulunamazsa
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));
    }

    /**
     * Belirtilen kullanıcı adını kullanan kullanıcıyı getirir.
     * @param username Kullanıcı adı
     * @return Kullanıcı nesnesi
     * @throws UserNotFoundException Kullanıcı bulunamazsa
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));
    }

    /**
     * Kullanıcı adını kullanarak bir kullanıcıyı opsiyonel olarak bulur.
     * @param username Kullanıcı adı
     * @return Opsiyonel kullanıcı nesnesi
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Mevcut oturum açmış kullanıcıyı getirir.
     * @param userDetails Authentication üzerinden alınan kullanıcı bilgisi
     * @return Giriş yapan kullanıcı
     * @throws UserNotFoundException Eğer giriş yapan kullanıcı bulunamazsa
     */
    public User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Giriş yapan kullanıcı bulunamadı."));
    }

    /**
     * Bir kullanıcıyı siler.
     * @param id Silinecek kullanıcının ID'si
     * @param userDetails Mevcut giriş yapan kullanıcı bilgisi
     * @throws UserNotFoundException Kullanıcı bulunamazsa
     * @throws UnauthorizedAccessException Kullanıcı yetkisizse
     */
    public void deleteUser(Long id, UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        User userToDelete = getUserById(id);

        if (!currentUser.getId().equals(userToDelete.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu işlemi gerçekleştirme yetkiniz yok!");
        }
        userRepository.delete(userToDelete);
    }

    /**
     * Bir kullanıcının bilgilerini günceller.
     * @param id Güncellenecek kullanıcının ID'si
     * @param updatedUser Güncellenmiş kullanıcı bilgileri
     * @param userDetails Mevcut giriş yapan kullanıcı bilgisi
     * @return Güncellenmiş kullanıcı nesnesi
     * @throws UserNotFoundException Kullanıcı bulunamazsa
     * @throws UnauthorizedAccessException Kullanıcı yetkisizse
     */
    public User updateUser(Long id, User updatedUser, UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        User userToUpdate = getUserById(id);

        if (!currentUser.getId().equals(userToUpdate.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu işlemi gerçekleştirme yetkiniz yok!");
        }

        userToUpdate.setUsername(updatedUser.getUsername());
        userToUpdate.setEmail(updatedUser.getEmail());

        if (!updatedUser.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userToUpdate.setRole(updatedUser.getRole());

        return userRepository.save(userToUpdate);
    }

    /**
     * Bir kullanıcıyı takip eder.
     * @param currentUserId Takip eden kullanıcının ID'si
     * @param targetUserId Takip edilecek kullanıcının ID'si
     * @throws RuntimeException Kullanıcı zaten takip ediliyorsa
     */
    public void followUser(Long currentUserId, Long targetUserId) {
        User currentUser = getUserById(currentUserId);
        User targetUser = getUserById(targetUserId);

        if (userRepository.isFollowing(currentUserId, targetUserId)) {
            throw new RuntimeException("Zaten takip ediliyor.");
        }

        currentUser.getFollowing().add(targetUser);
        targetUser.getFollowers().add(currentUser);

        userRepository.save(currentUser);
        userRepository.save(targetUser);
    }

    /**
     * Bir kullanıcıyı takipten çıkarır.
     * @param currentUserId Takibi bırakan kullanıcının ID'si
     * @param targetUserId Takibi bırakılacak kullanıcının ID'si
     * @throws RuntimeException Eğer kullanıcı zaten takip edilmiyorsa
     */
    public void unfollowUser(Long currentUserId, Long targetUserId) {
        User currentUser = getUserById(currentUserId);
        User targetUser = getUserById(targetUserId);

        if (!userRepository.isFollowing(currentUserId, targetUserId)) {
            throw new RuntimeException("Takip edilmeyen bir kullanıcı takipten çıkarılamaz.");
        }

        currentUser.getFollowing().remove(targetUser);
        targetUser.getFollowers().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(targetUser);
    }

    /**
     * Belirtilen kullanıcının takipçi sayısını getirir.
     * @param userId Kullanıcı ID'si
     * @return Takipçi sayısı
     */
    public long getFollowersCount(Long userId) {
        return userRepository.getFollowersCount(userId);
    }

    /**
     * Belirtilen kullanıcının takip ettiği kişi sayısını getirir.
     * @param userId Kullanıcı ID'si
     * @return Takip edilen kişi sayısı
     */
    public long getFollowingCount(Long userId) {
        return getUserById(userId).getFollowing().size();
    }

    /**
     * Kullanıcıları belirli bir sorguya göre arar.
     * @param query Arama kelimesi
     * @return Eşleşen kullanıcılar listesi
     */
    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }
}

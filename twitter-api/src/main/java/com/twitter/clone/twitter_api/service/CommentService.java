package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.dto.CommentRequest;
import com.twitter.clone.twitter_api.entity.Comment;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.CommentNotFoundException;
import com.twitter.clone.twitter_api.exception.TweetNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.CommentRepository;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CommentService, yorum (comment) işlemlerini yöneten servis katmanıdır.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;

    /**
     * CommentService yapıcı metodu.
     * @param commentRepository Yorum veritabanı erişimi
     * @param tweetRepository Tweet veritabanı erişimi
     */
    @Autowired
    public CommentService(CommentRepository commentRepository, TweetRepository tweetRepository) {
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
    }

    /**
     * Belirtilen tweet'e yorum ekler.
     * @param request Yorum isteği (içerik ve tweetId içerir)
     * @param user Yorumu ekleyen kullanıcı
     * @return Eklenen yorum nesnesi
     * @throws TweetNotFoundException Eğer tweet bulunamazsa
     */
    public Comment addComment(CommentRequest request, User user) {
        // Tweet ID'ye göre tweeti bul, yoksa hata fırlat
        Tweet tweet = tweetRepository.findById(request.getTweetId())
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı!"));

        // Yeni yorum nesnesi oluştur ve kaydet
        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .tweet(tweet)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    /**
     * Mevcut bir yorumu günceller.
     * @param commentId Güncellenecek yorumun ID'si
     * @param newContent Yeni yorum içeriği
     * @param requestUser Güncelleme talebinde bulunan kullanıcı
     * @return Güncellenmiş yorum nesnesi
     * @throws CommentNotFoundException Eğer yorum bulunamazsa
     * @throws UnauthorizedAccessException Eğer kullanıcı yetkili değilse
     */
    public Comment updateComment(Long commentId, String newContent, User requestUser) {
        // Yorumun var olup olmadığını kontrol et
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));

        // Kullanıcı, yorumu güncelleme yetkisine sahip mi?
        if (!comment.getUser().getId().equals(requestUser.getId())) {
            throw new UnauthorizedAccessException("Bu yorumu güncelleme yetkiniz yok!");
        }

        // Yorum içeriğini güncelle ve kaydet
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    /**
     * Belirtilen tweet'e ait tüm yorumları getirir.
     * @param tweetId Tweet ID'si
     * @return Yorum listesi
     */
    public List<Comment> getCommentsByTweetId(Long tweetId) {
        return commentRepository.findByTweetIdOrderByCreatedAtDesc(tweetId);
    }

    /**
     * Belirli bir yorumun detaylarını getirir.
     * @param commentId Yorum ID'si
     * @return İlgili yorum nesnesi
     * @throws CommentNotFoundException Eğer yorum bulunamazsa
     */
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));
    }

    /**
     * Bir yorumu siler. Yorumu sadece yorum sahibi veya tweet sahibi silebilir.
     * @param commentId Silinecek yorumun ID'si
     * @param requestUser Silme işlemini gerçekleştiren kullanıcı
     * @throws CommentNotFoundException Eğer yorum bulunamazsa
     * @throws UnauthorizedAccessException Eğer kullanıcı yetkili değilse
     */
    public void deleteComment(Long commentId, User requestUser) {
        // Yorumun var olup olmadığını kontrol et
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));

        // Yorumu yapan kullanıcı ve tweet sahibi bilgilerini al
        Tweet tweet = comment.getTweet();
        boolean isCommentOwner = comment.getUser().getId().equals(requestUser.getId());
        boolean isTweetOwner = tweet.getUser().getId().equals(requestUser.getId());

        // Eğer kullanıcı ne yorum sahibi ne de tweet sahibi ise, yetkisiz erişim hatası fırlat
        if (!isCommentOwner && !isTweetOwner) {
            throw new UnauthorizedAccessException("Bu yorumu silme yetkiniz yok!");
        }

        // Yorumu sil
        commentRepository.delete(comment);
    }
}

package com.twitter.clone.twitter_api.serviceTest;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import com.twitter.clone.twitter_api.repository.UserRepository;
import com.twitter.clone.twitter_api.service.TweetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TweetServiceTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TweetService tweetService;

    private User testUser;
    private User adminUser;
    private Tweet testTweet;

    @BeforeEach
    void setUp() {
        // Test verileri oluşturuluyor: kullanıcılar ve tweet
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setRole(Role.USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("adminUser");
        adminUser.setRole(Role.ADMIN);

        testTweet = new Tweet();
        testTweet.setId(1L);
        testTweet.setContent("Hello Twitter!");
        testTweet.setUser(testUser);
        testTweet.setActive(true);

        // Güvenlik bağlamını mock'layarak test için geçerli kullanıcı simüle ediliyor
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext); // Test için güvenlik bağlamı ayarlanıyor
    }

    @AfterEach
    void tearDown() {
        // Her testten sonra mock'lar sıfırlanıyor, böylece testler arasında veri sızıntısı olmuyor
        reset(tweetRepository, userRepository, securityContext, authentication);
    }

    @Test
    void testCreateTweet() {
        // Tweet oluşturma davranışını mock'la
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser)); // testUser'ı döndür
        when(tweetRepository.save(any(Tweet.class))).thenReturn(testTweet);

        // Tweet oluşturma servisi çağrılıyor
        Tweet createdTweet = tweetService.createTweet(testTweet);

        // Tweet'in başarılı bir şekilde oluşturulduğunu doğrula
        assertNotNull(createdTweet);
        assertEquals("Hello Twitter!", createdTweet.getContent());
        assertEquals(testUser, createdTweet.getUser()); // Kullanıcının doğru şekilde ayarlandığını kontrol et

        // save metodunun bir kez çağrıldığını ve kullanıcı aramasının yapıldığını doğrula
        verify(tweetRepository, times(1)).save(any(Tweet.class));
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testGetTweetById_Success() {
        // Tweet'in ID'ye göre alınması için davranışı mock'la
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet)); // Tweet mevcut

        // Tweet servisi ile tweet'i al
        Optional<Tweet> retrievedTweet = tweetService.getTweetById(1L);

        // Tweet'in mevcut olduğunu doğrula
        assertTrue(retrievedTweet.isPresent()); // Tweet var
        assertEquals("Hello Twitter!", retrievedTweet.get().getContent()); // İçeriği doğrula

        // Repository'nin doğru bir şekilde çağrıldığını doğrula
        verify(tweetRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTweetById_NotFound() {
        // Tweet bulunamadığında davranışını mock'la
        when(tweetRepository.findById(2L)).thenReturn(Optional.empty()); // Tweet bulunamıyor

        // Tweet servisi ile tweet'i almaya çalış
        Optional<Tweet> retrievedTweet = tweetService.getTweetById(2L);

        // Tweet'in bulunmadığını doğrula
        assertFalse(retrievedTweet.isPresent()); // Tweet bulunamadıysa Optional.empty dönecek

        // Repository'nin bir kez çağrıldığını doğrula
        verify(tweetRepository, times(1)).findById(2L);
    }

    @Test
    void testUpdateTweet_Success() {
        // Tweet güncelleme davranışını mock'la
        when(authentication.getName()).thenReturn("testUser"); // testUser'ı mock'la
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser)); // testUser'ı döndür
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet)); // Tweet'i bul ve döndür
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(invocation -> {
            // Invoke parametrelerini kullanarak güncellenmiş tweet'i döndürüyoruz
            Tweet tweet = invocation.getArgument(0);
            tweet.setContent("Updated Content"); // İçeriği güncelliyoruz
            return tweet;
        });

        // Tweet içeriğini güncelle
        Tweet updatedTweet = tweetService.updateTweet(1L, "Updated Content");

        // Tweet'in güncellenmiş olduğuna dair doğrulama
        assertNotNull(updatedTweet); // Güncellenmiş tweet null olmamalı
        assertEquals("Updated Content", updatedTweet.getContent()); // Tweet'in içeriği güncellenmeli
        verify(tweetRepository, times(1)).save(any(Tweet.class)); // save metodunun bir kez çağrıldığını doğrula
    }

    @Test
    void testUpdateTweet_NotAuthorized() {
        // Başka bir kullanıcı tarafından tweet güncellenmeye çalışıldığında davranışını test et
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUsername("anotherUser");

        when(authentication.getName()).thenReturn("anotherUser"); // Başka bir kullanıcıyı mock'la
        when(userRepository.findByUsername("anotherUser")).thenReturn(Optional.of(anotherUser)); // Başka kullanıcıyı döndür
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet)); // Tweet'i bul ve döndür

        // UnauthorizedAccessException'ın fırlatıldığını doğrula
        assertThrows(UnauthorizedAccessException.class, () -> tweetService.updateTweet(1L, "Updated Content"));
        verify(tweetRepository, never()).save(any(Tweet.class)); // save metodunun çağrılmadığını doğrula
    }

    @Test
    void testDeleteTweet_SuccessByOwner() {
        // Tweet sahibinin tweet silme işlemini test et
        when(authentication.getName()).thenReturn("testUser"); // testUser'ı mock'la
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser)); // testUser'ı döndür
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet)); // Tweet'i bul ve döndür

        // Tweet silme işlemi
        tweetService.deleteTweet(1L);

        // Tweet'in aktif olmadığını (pasif olduğunu) kontrol et
        assertFalse(testTweet.isActive(), "Tweet should be inactive after deletion");

        // Tweet'i kaydetme işlemi doğrulaması
        verify(tweetRepository, times(1)).save(testTweet); // save metodunun bir kez çağrıldığını doğrula
    }

    @Test
    void testDeleteTweet_SuccessByAdmin() {
        // Admin kullanıcı adını mock'la
        when(authentication.getName()).thenReturn("adminUser"); // adminUser'ı mock'la
        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.of(adminUser)); // adminUser'ı döndür
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet)); // Tweet'i bul ve döndür

        // Tweet silme işlemi
        tweetService.deleteTweet(1L);

        // Tweet'in aktif olmadığını kontrol et (pasif olmalı)
        assertFalse(testTweet.isActive(), "Tweet should be inactive after deletion");

        // Tweet kaydetme işlemi doğrulaması
        verify(tweetRepository, times(1)).save(testTweet); // save metodunun bir kez çağrıldığını doğrula
    }

    @Test
    void testDeleteTweet_NotAuthorized() {
        // Başka bir kullanıcı tarafından tweet silinmeye çalışıldığında davranışını test et
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUsername("anotherUser");

        when(authentication.getName()).thenReturn("anotherUser"); // Başka bir kullanıcıyı mock'la
        when(userRepository.findByUsername("anotherUser")).thenReturn(Optional.of(anotherUser)); // Başka kullanıcıyı döndür
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet)); // Tweet'i bul ve döndür

        // UnauthorizedAccessException'ın fırlatıldığını doğrula
        assertThrows(UnauthorizedAccessException.class, () -> tweetService.deleteTweet(1L));
        verify(tweetRepository, never()).save(any(Tweet.class)); // save metodunun çağrılmadığını doğrula
    }
}

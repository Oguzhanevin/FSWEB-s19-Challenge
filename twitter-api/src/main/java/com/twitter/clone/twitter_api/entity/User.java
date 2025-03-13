package com.twitter.clone.twitter_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tweets", "followers", "following"})
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Kullanıcı benzersiz kimliği

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Column(nullable = false, unique = true)
    private String username; // Kullanıcı adı, benzersiz olmalı

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    @Column(nullable = false)
    private String password; // Kullanıcı şifresi

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    @Column(nullable = false, unique = true)
    private String email; // Kullanıcı email adresi, benzersiz olmalı

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // Kullanıcının rolü (ADMIN, USER vb.)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Tweet> tweets = new ArrayList<>(); // Kullanıcının tweetleri

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    @JsonIgnore
    private List<User> followers = new ArrayList<>(); // Kullanıcının takipçileri

    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    @JsonIgnore
    private List<User> following = new ArrayList<>(); // Kullanıcının takip ettiği kişiler

    // Parametreli constructor
    public User(String username, String password, String email, Role role, List<Tweet> tweets) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.tweets = tweets != null ? tweets : new ArrayList<>();
    }
}

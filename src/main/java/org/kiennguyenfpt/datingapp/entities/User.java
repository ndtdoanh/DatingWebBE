package org.kiennguyenfpt.datingapp.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.kiennguyenfpt.datingapp.enums.UserStatus;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = true)
    private String avatar;

    @Column(nullable = true)
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;

    @UpdateTimestamp
    @Column(name = "last_login")
    private Timestamp lastLogin;

    @Column(name = "login_count", nullable = false)
    private int loginCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Profile profile; // Một người dùng chỉ có một hồ sơ

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Like> likes; // Danh sách các lượt thích của người dùng

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages; // Danh sách tin nhắn của người dùng

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserRole> userRoles; // Danh sách vai trò của người dùng

    @OneToOne
    @JoinColumn(name = "location_id")
    private UserLocation userLocation;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_blocked",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_user_id")
    )
    private List<User> blockedUsers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "liked_user_id")
    )
    private Set<User> likedUsers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_matches",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "matched_user_id")
    )
    private Set<User> matches = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_dislikes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "disliked_user_id")
    )
    private Set<User> dislikedUsers = new HashSet<>();

    //@Column(name = "profile_updated", nullable = false)
    //private boolean profileUpdated = false;

    public boolean isSecondLogin() {
        return loginCount == 1;
    }

    @Column
    private int dailySwipeCount; // Số lần quẹt trong ngày

    @Column
    private LocalDate lastSwipeReset = LocalDate.now(); // Ngày cuối cùng reset số lần quẹt

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && firstLogin == user.firstLogin && loginCount == user.loginCount && Objects.equals(email, user.email) && Objects.equals(passwordHash, user.passwordHash) && Objects.equals(avatar, user.avatar) && Objects.equals(phone, user.phone) && Objects.equals(createdAt, user.createdAt) && Objects.equals(lastLogin, user.lastLogin) && status == user.status && Objects.equals(profile, user.profile) && Objects.equals(likes, user.likes) && Objects.equals(messages, user.messages) && Objects.equals(userRoles, user.userRoles) && Objects.equals(friends, user.friends) && Objects.equals(blockedUsers, user.blockedUsers) && Objects.equals(likedUsers, user.likedUsers) && Objects.equals(matches, user.matches) && Objects.equals(dislikedUsers, user.dislikedUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, passwordHash, avatar, phone, createdAt, firstLogin, lastLogin, loginCount, status, profile, likes, messages, userRoles, friends, blockedUsers, likedUsers, matches, dislikedUsers);
    }
}

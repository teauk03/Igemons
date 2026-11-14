package com.igemons.adapter.out.persistence.entity;

import com.igemons.domain.users.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "password_hash", nullable = false, length = 72)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected UserJpaEntity() {}

    public static UserJpaEntity from(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        if (user.getId() != null) entity.id = user.getId().value();
        entity.email = user.getEmail();
        entity.nickname = user.getNickname();
        entity.profileImageUrl = user.getProfileImageUrl();
        entity.passwordHash = user.getPasswordHash();
        entity.role = user.getRole();
        entity.status = user.getStatus();
        entity.lastLoginAt = user.getLastLoginAt();
        entity.createdAt = user.getCreatedAt();
        return entity;
    }

    public User toDomain() {
        return User.restore(
            UserId.of(id), email, nickname,
            profileImageUrl, passwordHash, role, status, lastLoginAt, createdAt
        );
    }

    public Long getId() { return id; }
}

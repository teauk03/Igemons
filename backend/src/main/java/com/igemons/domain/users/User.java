package com.igemons.domain.users;

import java.time.LocalDateTime;
import java.util.Objects;

import com.igemons.domain.DomainException;
import com.igemons.domain.Guard;

public class User {

    private final UserId id;
    private String email;                 // 로그인 ID (고유)
    private String nickname;
    private String profileImageUrl;
    private String passwordHash;          // BCrypt 해시
    private final UserRole role;
    private UserStatus status;
    private LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;

    private User(UserId id, String email, String nickname, String profileImageUrl,
                 String passwordHash, UserRole role, UserStatus status,
                 LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        this.id = id;
        this.email = Guard.requireText(email, "email", 255);
        this.nickname = Guard.requireText(nickname, "nickname", 50);
        this.profileImageUrl = Guard.optionalText(profileImageUrl, "profileImageUrl", 500);
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role);
        this.status = Objects.requireNonNull(status);
        this.lastLoginAt = lastLoginAt;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    // 팩토리

    // 로컬 회원가입
    public static User registerLocal(String email, String nickname, String passwordHash, LocalDateTime now) {
        return new User(null, email, nickname, null, passwordHash,
                UserRole.USER, UserStatus.ACTIVE, now, now);
    }

    // adapter에서 DB 값을 도메인으로 복원할 때만 사용
    public static User restore(UserId id, String email, String nickname, String profileImageUrl,
                               String passwordHash, UserRole role, UserStatus status,
                               LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        Objects.requireNonNull(id, "restore requires id");
        return new User(id, email, nickname, profileImageUrl, passwordHash,
                role, status, lastLoginAt, createdAt);
    }

    // 행위

    public void recordLogin(LocalDateTime now) {
        assertActive();
        this.lastLoginAt = now;
    }

    public void withdraw() {
        assertActive();
        this.status = UserStatus.WITHDRAWN;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isNew() {
        return id == null;
    }

    private void assertActive() {
        if (!isActive()) {
            throw new DomainException("탈퇴한 사용자입니다.");
        }
    }

    // getter
    public UserId getId() { return id; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

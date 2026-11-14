package com.igemons.domain.users;

import java.time.LocalDateTime;
import java.util.Objects;

import com.igemons.domain.DomainException;
import com.igemons.domain.Guard;

public class User {
  
    private final UserId id;                 // 신규 생성 시 null 
    private final AuthProvider provider;
    private final String providerId;
    private String email;                    // 카카오 동의 항목 미동의 시 null
    private String nickname;
    private String profileImageUrl;
    private final UserRole role;
    private UserStatus status;
    private LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
 
    private User(UserId id, AuthProvider provider, String providerId, String email,
                 String nickname, String profileImageUrl, UserRole role, UserStatus status,
                 LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        this.id = id;
        this.provider = Objects.requireNonNull(provider);
        this.providerId = Guard.requireText(providerId, "providerId", 64);
        this.email = Guard.optionalText(email, "email", 255);
        this.nickname = Guard.requireText(nickname, "nickname", 50);
        this.profileImageUrl = Guard.optionalText(profileImageUrl, "profileImageUrl", 500);
        this.role = Objects.requireNonNull(role);
        this.status = Objects.requireNonNull(status);
        this.lastLoginAt = lastLoginAt;
        this.createdAt = Objects.requireNonNull(createdAt);
    }
 
    // 팩토리 
 
    // 카카오 최초 로그인 = 회원가입 
    public static User registerFromKakao(String kakaoId, String email, String nickname,
                                         String profileImageUrl, LocalDateTime now) {
        return new User(null, AuthProvider.KAKAO, kakaoId, email, nickname, profileImageUrl,
                UserRole.USER, UserStatus.ACTIVE, now, now);
    }
 
    // adapter에서 DB 값을 도메인으로 복원할 때만 사용 
    public static User restore(UserId id, AuthProvider provider, String providerId, String email,
                               String nickname, String profileImageUrl, UserRole role, UserStatus status,
                               LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        Objects.requireNonNull(id, "restore requires id");
        return new User(id, provider, providerId, email, nickname, profileImageUrl,
                role, status, lastLoginAt, createdAt);
    }
 
    // 행위 


    /**
     * 마지막 로그인 시간을 기록
     * @param now 현재 시간
     */
    public void recordLogin(LocalDateTime now) {
        assertActive();
        this.lastLoginAt = now;
    }
 
    /**
     * 카카오 프로필 동기화
     * @param email 이메일 (동의하지 않으면 null)
     * @param nickname 닉네임
     * @param profileImageUrl 프로필 이미지 URL (동의하지 않으면 null)
     */
    public void syncKakaoProfile(String email, String nickname, String profileImageUrl) {
        assertActive();
        this.email = Guard.optionalText(email, "email", 255);
        this.nickname = Guard.requireText(nickname, "nickname", 50);
        this.profileImageUrl = Guard.optionalText(profileImageUrl, "profileImageUrl", 500);
    }
    
    /**
     * 회원 탈퇴
     */
    public void withdraw() {
        assertActive();
        this.status = UserStatus.WITHDRAWN;
    }
 
    /**
     * 활성화된 사용자인지 확인
     * @return 활성화되었으면 true, 아니면 false
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
 
    /**
     * 신규 생성 여부 확인
     * @return 신규 생성이면 true, 아니면 false
     */
    public boolean isNew() {
        return id == null;
    }
    
    /**
     * 활성화된 사용자 여부 검증
     * @throws DomainException 탈퇴한 사용자이면 발생
     */
    private void assertActive() {
        if (!isActive()) {
            throw new DomainException("탈퇴한 사용자입니다.");
        }
    }
    
    // getter
    public UserId getId() { return id; }
    public AuthProvider getProvider() { return provider; }
    public String getProviderId() { return providerId; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
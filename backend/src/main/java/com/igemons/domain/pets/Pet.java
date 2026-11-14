package com.igemons.domain.pets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import com.igemons.domain.DomainException;
import com.igemons.domain.Guard;
import com.igemons.domain.users.UserId;

public class Pet {
    private final PetId id;                  // 신규 생성 시 null
    private final UserId ownerId;
    private String name;
    private String species;
    private String morph;
    private PetGender gender;
    private LocalDate birthDate;
    private LocalDate adoptedDate;
    private String profileImageUrl;
    private String memo;
    private LocalDateTime deletedAt;
    private final LocalDateTime createdAt;
 
    private Pet(PetId id, UserId ownerId, String name, String species, String morph,
                PetGender gender, LocalDate birthDate, LocalDate adoptedDate,
                String profileImageUrl, String memo, LocalDateTime deletedAt,
                LocalDateTime createdAt) {
        this.id = id;
        this.ownerId = Objects.requireNonNull(ownerId);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.deletedAt = deletedAt;
        applyProfile(name, species, morph, gender, birthDate, adoptedDate, profileImageUrl, memo);
    }
 
    // ---------- 팩토리 ----------
 
    public static Pet register(UserId ownerId, String name, String species, String morph,
                               PetGender gender, LocalDate birthDate, LocalDate adoptedDate,
                               LocalDateTime now) {
        return new Pet(null, ownerId, name, species, morph, gender, birthDate, adoptedDate,
                null, null, null, now);
    }
 
    public static Pet restore(PetId id, UserId ownerId, String name, String species, String morph,
                              PetGender gender, LocalDate birthDate, LocalDate adoptedDate,
                              String profileImageUrl, String memo, LocalDateTime deletedAt,
                              LocalDateTime createdAt) {
        Objects.requireNonNull(id, "restore requires id");
        return new Pet(id, ownerId, name, species, morph, gender, birthDate, adoptedDate,
                profileImageUrl, memo, deletedAt, createdAt);
    }
 
    // 행위
 
    public void updateProfile(UserId requester, String name, String species, String morph,
                              PetGender gender, LocalDate birthDate, LocalDate adoptedDate,
                              String profileImageUrl, String memo) {
        verifyAccessibleBy(requester);
        applyProfile(name, species, morph, gender, birthDate, adoptedDate, profileImageUrl, memo);
    }
 
    public void delete(UserId requester, LocalDateTime now) {
        verifyAccessibleBy(requester);
        this.deletedAt = now;
    }
 
    /**
     * 요청자가 펫의 소유자인지 검증
     * @param requester 요청자
     * @throws PetAccessDeniedException 요청자가 소유자가 아니면 발생
     * @throws DomainException 삭제된 펫이면 발생
     */
    public void verifyAccessibleBy(UserId requester) {
        if (!ownerId.equals(requester)) {
            throw new PetAccessDeniedException();
        }
        if (isDeleted()) {
            throw new DomainException("삭제된 반려동물입니다.");
        }
    }
    
    /**
     * 삭제 여부 확인
     * @return 삭제되었으면 true, 아니면 false
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
 
    /**
     * 신규 생성 여부 확인
     * @return 신규 생성이면 true, 아니면 false
     */
    public boolean isNew() {
        return id == null;
    }
    
    /**
     * 펫 프로필을 적용
     * @param name 이름
     * @param species 종
     * @param morph 품종
     * @param gender 성별
     * @param birthDate 출생일
     * @param adoptedDate 입양일
     * @param profileImageUrl 프로필 이미지 URL
     * @param memo 메모
     */
    private void applyProfile(String name, String species, String morph, PetGender gender,
                              LocalDate birthDate, LocalDate adoptedDate,
                              String profileImageUrl, String memo) {
        if (birthDate != null && adoptedDate != null) {
            Guard.require(!birthDate.isAfter(adoptedDate), "입양일은 출생일보다 빠를 수 없습니다.");
        }
        this.name = Guard.requireText(name, "name", 50);
        this.species = Guard.requireText(species, "species", 100);
        this.morph = Guard.optionalText(morph, "morph", 100);
        this.gender = (gender == null) ? PetGender.UNKNOWN : gender;
        this.birthDate = birthDate;
        this.adoptedDate = adoptedDate;
        this.profileImageUrl = Guard.optionalText(profileImageUrl, "profileImageUrl", 500);
        this.memo = Guard.optionalText(memo, "memo", 1000);
    }
 
    // getter 
    public PetId getId() { return id; }
    public UserId getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getMorph() { return morph; }
    public PetGender getGender() { return gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public LocalDate getAdoptedDate() { return adoptedDate; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getMemo() { return memo; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
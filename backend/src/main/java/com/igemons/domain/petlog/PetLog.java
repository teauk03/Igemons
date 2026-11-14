package com.igemons.domain.petlog;

import java.time.LocalDateTime;
import java.util.Objects;

import com.igemons.domain.Guard;
import com.igemons.domain.pets.PetId;

public class PetLog {
 
    private final PetLogId id;               // 신규 생성 시 null
    private final PetId petId;
    private PetLogDetail detail;
    private LocalDateTime loggedAt;          // 실제 발생 시각
    private String memo;
    private final LocalDateTime createdAt;   // 입력 시각
 
    private PetLog(PetLogId id, PetId petId, PetLogDetail detail, LocalDateTime loggedAt,
                   String memo, LocalDateTime createdAt) {
        this.id = id;
        this.petId = Objects.requireNonNull(petId);
        this.detail = Objects.requireNonNull(detail);
        this.loggedAt = Objects.requireNonNull(loggedAt);
        this.memo = Guard.optionalText(memo, "memo", 1000);
        this.createdAt = Objects.requireNonNull(createdAt);
    }
 
    // ---------- 팩토리 ----------
 
    public static PetLog record(PetId petId, PetLogDetail detail, LocalDateTime loggedAt,
                                String memo, LocalDateTime now) {
        Guard.require(!loggedAt.isAfter(now), "미래 시각으로는 기록할 수 없습니다.");
        return new PetLog(null, petId, detail, loggedAt, memo, now);
    }
 
    public static PetLog restore(PetLogId id, PetId petId, PetLogDetail detail,
                                 LocalDateTime loggedAt, String memo, LocalDateTime createdAt) {
        Objects.requireNonNull(id, "restore requires id");
        return new PetLog(id, petId, detail, loggedAt, memo, createdAt);
    }
 
    // ---------- 행위 ----------
 
    /** 기록 수정. 종류(type)는 바꿀 수 없다. */
    public void amend(PetLogDetail newDetail, LocalDateTime newLoggedAt, String newMemo,
                      LocalDateTime now) {
        Guard.require(newDetail.type() == this.detail.type(), "기록 종류는 변경할 수 없습니다.");
        Guard.require(!newLoggedAt.isAfter(now), "미래 시각으로는 기록할 수 없습니다.");
        this.detail = newDetail;
        this.loggedAt = newLoggedAt;
        this.memo = Guard.optionalText(newMemo, "memo", 1000);
    }
 
    public PetLogType getType() {
        return detail.type();
    }
 
    public boolean isNew() {
        return id == null;
    }
 
    // ---------- getter ----------
    public PetLogId getId() { return id; }
    public PetId getPetId() { return petId; }
    public PetLogDetail getDetail() { return detail; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public String getMemo() { return memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
 

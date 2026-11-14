package com.igemons.application.petlog;

import com.igemons.domain.petlog.PetLogDetail;

import java.time.LocalDateTime;

/**
 * 사육일지 기록/수정 입력값
 * @param loggedAt null이면 생성 시 현재 시각, 수정 시 기존 시각 유지
 */
public record PetLogCommand(
    PetLogDetail detail, 
    LocalDateTime loggedAt, 
    String memo) {}

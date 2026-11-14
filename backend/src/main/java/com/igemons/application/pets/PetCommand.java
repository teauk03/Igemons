package com.igemons.application.pets;

import com.igemons.domain.pets.PetGender;

import java.time.LocalDate;

/**
 * 펫 등록/수정 입력값
 */
public record PetCommand(
    String name,
    String species,
    String morph,
    PetGender gender,
    LocalDate birthDate,
    LocalDate adoptedDate,
    String profileImageUrl,
    String memo
) {}

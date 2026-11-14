package com.igemons.adapter.in.web.pets;

import com.igemons.application.pets.PetCommand;
import com.igemons.domain.pets.PetGender;

import java.time.LocalDate;

public record PetRequest(
    String name,
    String species,
    String morph,
    PetGender gender,
    LocalDate birthDate,
    LocalDate adoptedDate,
    String profileImageUrl,
    String memo
) {
    public PetCommand toCommand() {
        return new PetCommand(name, species, morph, gender, birthDate, adoptedDate, profileImageUrl, memo);
    }
}

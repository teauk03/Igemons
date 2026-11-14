package com.igemons.adapter.in.web.pets;

import com.igemons.domain.pets.Pet;
import com.igemons.domain.pets.PetGender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PetResponse(
    Long id,
    String name,
    String species,
    String morph,
    PetGender gender,
    LocalDate birthDate,
    LocalDate adoptedDate,
    String profileImageUrl,
    String memo,
    LocalDateTime createdAt
) {
    public static PetResponse from(Pet pet) {
        return new PetResponse(pet.getId().value(), pet.getName(), pet.getSpecies(), pet.getMorph(),
                pet.getGender(), pet.getBirthDate(), pet.getAdoptedDate(), pet.getProfileImageUrl(),
                pet.getMemo(), pet.getCreatedAt());
    }
}

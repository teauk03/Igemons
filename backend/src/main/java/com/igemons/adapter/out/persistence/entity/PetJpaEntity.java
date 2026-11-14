package com.igemons.adapter.out.persistence.entity;

import com.igemons.domain.pets.Pet;
import com.igemons.domain.pets.PetGender;
import com.igemons.domain.pets.PetId;
import com.igemons.domain.users.UserId;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets", indexes = @Index(name = "idx_pets_owner", columnList = "owner_id"))
public class PetJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String species;

    @Column(length = 100)
    private String morph;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PetGender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "adopted_date")
    private LocalDate adoptedDate;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(length = 1000)
    private String memo;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected PetJpaEntity() {}

    public static PetJpaEntity from(Pet pet) {
        PetJpaEntity e = new PetJpaEntity();
        if (pet.getId() != null) e.id = pet.getId().value();
        e.ownerId = pet.getOwnerId().value();
        e.name = pet.getName();
        e.species = pet.getSpecies();
        e.morph = pet.getMorph();
        e.gender = pet.getGender();
        e.birthDate = pet.getBirthDate();
        e.adoptedDate = pet.getAdoptedDate();
        e.profileImageUrl = pet.getProfileImageUrl();
        e.memo = pet.getMemo();
        e.deletedAt = pet.getDeletedAt();
        e.createdAt = pet.getCreatedAt();
        return e;
    }

    public Pet toDomain() {
        return Pet.restore(PetId.of(id), UserId.of(ownerId), name, species, morph, gender,
                birthDate, adoptedDate, profileImageUrl, memo, deletedAt, createdAt);
    }
}

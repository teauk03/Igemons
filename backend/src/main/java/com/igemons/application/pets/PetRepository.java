package com.igemons.application.pets;

import com.igemons.domain.pets.Pet;
import com.igemons.domain.pets.PetId;
import com.igemons.domain.users.UserId;

import java.util.List;
import java.util.Optional;

public interface PetRepository {
    Optional<Pet> findById(PetId id);

    /** 삭제되지 않은 소유자의 펫 목록 (최근 등록순) */
    List<Pet> findActiveByOwner(UserId ownerId);

    Pet save(Pet pet);
}

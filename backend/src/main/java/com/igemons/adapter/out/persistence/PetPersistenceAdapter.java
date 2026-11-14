package com.igemons.adapter.out.persistence;

import lombok.RequiredArgsConstructor;

import com.igemons.adapter.out.persistence.entity.PetJpaEntity;
import com.igemons.application.pets.PetRepository;
import com.igemons.domain.pets.Pet;
import com.igemons.domain.pets.PetId;
import com.igemons.domain.users.UserId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PetPersistenceAdapter implements PetRepository {

    private final SpringDataPetRepository repository;

    @Override
    public Optional<Pet> findById(PetId id) {
        return repository.findById(id.value()).map(PetJpaEntity::toDomain);
    }

    @Override
    public List<Pet> findActiveByOwner(UserId ownerId) {
        return repository.findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDescIdDesc(ownerId.value())
            .stream().map(PetJpaEntity::toDomain).toList();
    }

    @Override
    public Pet save(Pet pet) {
        return repository.save(PetJpaEntity.from(pet)).toDomain();
    }
}

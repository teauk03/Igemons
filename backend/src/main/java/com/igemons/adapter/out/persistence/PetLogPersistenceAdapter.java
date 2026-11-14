package com.igemons.adapter.out.persistence;

import lombok.RequiredArgsConstructor;

import com.igemons.adapter.out.persistence.entity.PetLogJpaEntity;
import com.igemons.application.petlog.PetLogRepository;
import com.igemons.domain.petlog.PetLog;
import com.igemons.domain.petlog.PetLogId;
import com.igemons.domain.petlog.PetLogType;
import com.igemons.domain.pets.PetId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PetLogPersistenceAdapter implements PetLogRepository {

    private final SpringDataPetLogRepository repository;

    @Override
    public Optional<PetLog> findById(PetLogId id) {
        return repository.findById(id.value()).map(PetLogJpaEntity::toDomain);
    }

    @Override
    public List<PetLog> findByPet(PetId petId, PetLogType type) {
        List<PetLogJpaEntity> rows = (type == null)
            ? repository.findByPetIdOrderByLoggedAtDescIdDesc(petId.value())
            : repository.findByPetIdAndTypeOrderByLoggedAtDescIdDesc(petId.value(), type);
        return rows.stream().map(PetLogJpaEntity::toDomain).toList();
    }

    @Override
    public PetLog save(PetLog log) {
        return repository.save(PetLogJpaEntity.from(log)).toDomain();
    }

    @Override
    public void delete(PetLogId id) {
        repository.deleteById(id.value());
    }
}

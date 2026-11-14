package com.igemons.application.petlog;

import com.igemons.domain.petlog.PetLog;
import com.igemons.domain.petlog.PetLogId;
import com.igemons.domain.petlog.PetLogType;
import com.igemons.domain.pets.PetId;

import java.util.List;
import java.util.Optional;

public interface PetLogRepository {
    Optional<PetLog> findById(PetLogId id);

    /**
     * 펫의 기록 목록 (발생 시각 최신순)
     * @param type null이면 전체 종류
     */
    List<PetLog> findByPet(PetId petId, PetLogType type);

    PetLog save(PetLog log);

    void delete(PetLogId id);
}

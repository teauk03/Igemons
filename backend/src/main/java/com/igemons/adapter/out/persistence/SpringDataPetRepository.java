package com.igemons.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.igemons.adapter.out.persistence.entity.PetJpaEntity;

import java.util.List;

interface SpringDataPetRepository extends JpaRepository<PetJpaEntity, Long> {
    /**
     * 소유자 ID로 활성 상태(삭제되지 않은) 반려 개체 목록 조회
     *
     * @param ownerId 소유자 ID
     * @return 활성 상태인 {@link PetJpaEntity} 목록
     */
    List<PetJpaEntity> findByOwnerIdAndDeletedAtIsNullOrderByCreatedAtDescIdDesc(Long ownerId);
}

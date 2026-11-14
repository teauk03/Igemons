package com.igemons.adapter.out.persistence;

import com.igemons.adapter.out.persistence.entity.PetLogJpaEntity;
import com.igemons.domain.petlog.PetLogType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SpringDataPetLogRepository extends JpaRepository<PetLogJpaEntity, Long> {
    
    /**
     * 특정 반려 개체의 전체 케어 기록을 최신순으로 조회
     *
     * @param petId 반려 개체 ID
     * @return 기록 발생 시각({@code loggedAt}) 내림차순, 동일 시각인 경우 ID 내림차순으로 정렬된 엔티티 목록
     */
    List<PetLogJpaEntity> findByPetIdOrderByLoggedAtDescIdDesc(Long petId);

    /**
     * 특정 반려 개체의 케어 기록 중 지정된 유형({@link PetLogType})만 필터링하여 최신순으로 조회
     *
     * @param petId 반려 개체 ID
     * @param type  필터링할 기록 유형 (급여, 탈피, 체중, 온습도 등)
     * @return 조건에 일치하며 발생 시각 및 ID 내림차순으로 정렬된 엔티티 목록
     */
    List<PetLogJpaEntity> findByPetIdAndTypeOrderByLoggedAtDescIdDesc(Long petId, PetLogType type);
}

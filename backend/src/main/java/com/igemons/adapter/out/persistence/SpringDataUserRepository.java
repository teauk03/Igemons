package com.igemons.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.igemons.adapter.out.persistence.entity.UserJpaEntity;
import java.util.Optional;

interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {
    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일 주소
     * @return 조회된 {@link UserJpaEntity} 엔티티 (없으면 {@code Optional.empty()})
     */
    Optional<UserJpaEntity> findByEmail(String email);
}

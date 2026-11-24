package com.igemons.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.igemons.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {
    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일 주소
     * @return 조회된 {@link UserJpaEntity} 엔티티 (없으면 {@code Optional.empty()})
     */
    Optional<UserJpaEntity> findByEmail(String email);

    /** 닉네임 조회용 프로젝션 (비밀번호 해시 등은 읽지 않음) */
    interface NicknameRow {
        Long getId();
        String getNickname();
    }

    @Query("select u.id as id, u.nickname as nickname from UserJpaEntity u where u.id in :ids")
    List<NicknameRow> findNicknamesByIdIn(@Param("ids") Collection<Long> ids);
}

package com.igemons.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByProviderId(String providerId);
}

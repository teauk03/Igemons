package com.igemons.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import com.igemons.adapter.out.persistence.entity.UserJpaEntity;
import com.igemons.application.auth.UserRepository;
import com.igemons.domain.users.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final SpringDataUserRepository repository;

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public User save(User user) {
        return repository.save(UserJpaEntity.from(user)).toDomain();
    }
}

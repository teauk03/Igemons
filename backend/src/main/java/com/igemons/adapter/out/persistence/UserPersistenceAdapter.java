package com.igemons.adapter.out.persistence;

import com.igemons.application.auth.UserRepository;
import com.igemons.domain.users.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepository {

    private final SpringDataUserRepository repository;

    public UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return repository.findByProviderId(providerId)
            .map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id)
            .map(UserJpaEntity::toDomain);
    }

    @Override
    public User save(User user) {
        return repository.save(UserJpaEntity.from(user)).toDomain();
    }
}

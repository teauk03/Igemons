package com.igemons.application.auth;

import com.igemons.domain.users.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByProviderId(String providerId);
    Optional<User> findById(Long id);
    User save(User user);
}

package com.igemons.adapter.out.persistence;

import lombok.RequiredArgsConstructor;

import com.igemons.adapter.out.persistence.entity.UserJpaEntity;
import com.igemons.application.auth.DuplicateUserException;
import com.igemons.application.auth.UserRepository;
import com.igemons.domain.users.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
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

    /**
     * 신규 사용자 저장 시 email UNIQUE 제약 위반은 DuplicateUserException으로 변환
     * (사전 중복 검사를 동시에 통과한 가입 요청 대비)
     */
    @Override
    public User save(User user) {
        try {
            return repository.save(UserJpaEntity.from(user)).toDomain();
        } catch (DataIntegrityViolationException e) {
            if (user.isNew() && isUniqueViolation(e)) {
                throw new DuplicateUserException();
            }
            throw e;
        }
    }

    /** SQLState 23505 = unique_violation (SQL 표준 코드, PostgreSQL·H2 공통) */
    private static final String UNIQUE_VIOLATION = "23505";

    private static boolean isUniqueViolation(DataIntegrityViolationException e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && UNIQUE_VIOLATION.equals(sql.getSQLState())) {
                return true;
            }
        }
        return false;
    }
}

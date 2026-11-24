package com.igemons.application.auth;

import com.igemons.domain.users.User;
import com.igemons.domain.users.UserId;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User save(User user);

    /** 여러 사용자의 닉네임을 한 번에 조회 (없는 ID는 결과에서 빠짐) */
    Map<UserId, String> findNicknames(Collection<UserId> ids);
}

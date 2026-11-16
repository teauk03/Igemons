package com.igemons.application.auth;

import lombok.RequiredArgsConstructor;
import com.igemons.adapter.in.web.auth.AuthResponse;
import com.igemons.domain.Guard;
import com.igemons.domain.users.User;
import com.igemons.infrastructure.jwt.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    /** BCrypt는 72바이트까지만 처리하며, 초과 시 encode가 예외를 던짐 */
    private static final int MAX_PASSWORD_BYTES = 72;

    /** 입력값 누락·길이 초과는 DomainException(400), 중복 가입은 DuplicateUserException(409) */
    @Transactional
    public AuthResponse register(String email, String nickname, String rawPassword) {
        email = Guard.requireText(email, "email", 255);
        Guard.requireText(nickname, "nickname", 50);
        Guard.require(rawPassword != null && !rawPassword.isBlank(), "password은(는) 필수입니다.");
        Guard.require(rawPassword.getBytes(StandardCharsets.UTF_8).length <= MAX_PASSWORD_BYTES,
            "password은(는) " + MAX_PASSWORD_BYTES + "바이트를 넘을 수 없습니다.");

        userRepository.findByEmail(email).ifPresent(u -> {
            throw new DuplicateUserException();
        });

        String hash = passwordEncoder.encode(rawPassword);
        LocalDateTime now = LocalDateTime.now();
        User user = User.registerLocal(email, nickname, hash, now);
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId().value());

        return new AuthResponse(
            token,
            new AuthResponse.UserInfo(saved.getId().value(), saved.getNickname(), saved.getEmail())
        );
    }

    @Transactional
    public AuthResponse login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        user.recordLogin(LocalDateTime.now());
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId().value());

        return new AuthResponse(
            token,
            new AuthResponse.UserInfo(saved.getId().value(), saved.getNickname(), saved.getEmail())
        );
    }
}

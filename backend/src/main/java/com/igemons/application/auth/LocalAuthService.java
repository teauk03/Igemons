package com.igemons.application.auth;

import lombok.RequiredArgsConstructor;
import com.igemons.adapter.in.web.auth.AuthResponse;
import com.igemons.domain.users.User;
import com.igemons.infrastructure.jwt.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(String email, String nickname, String rawPassword) {
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
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

        if (user.getPasswordHash() == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
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

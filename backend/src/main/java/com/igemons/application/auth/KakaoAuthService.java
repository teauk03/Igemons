package com.igemons.application.auth;

import com.igemons.adapter.in.web.auth.AuthResponse;
import com.igemons.adapter.out.kakao.KakaoClient;
import com.igemons.adapter.out.kakao.KakaoUserResponse;
import com.igemons.domain.users.User;
import com.igemons.infrastructure.jwt.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class KakaoAuthService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public KakaoAuthService(KakaoClient kakaoClient, UserRepository userRepository, JwtService jwtService) {
        this.kakaoClient = kakaoClient;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse loginWithKakao(String code, String redirectUri) {
        String kakaoAccessToken = kakaoClient.getAccessToken(code, redirectUri);
        KakaoUserResponse kakaoUser = kakaoClient.getUserInfo(kakaoAccessToken);

        String kakaoId = String.valueOf(kakaoUser.id());
        String email = kakaoUser.kakaoAccount() != null ? kakaoUser.kakaoAccount().email() : null;
        String nickname = resolveNickname(kakaoUser);
        String profileImageUrl = resolveProfileImageUrl(kakaoUser);

        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findByProviderId(kakaoId)
            .map(existing -> {
                existing.syncKakaoProfile(email, nickname, profileImageUrl);
                existing.recordLogin(now);
                return existing;
            })
            .orElseGet(() -> User.registerFromKakao(kakaoId, email, nickname, profileImageUrl, now));

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId().value());

        return new AuthResponse(
            token,
            new AuthResponse.UserInfo(saved.getId().value(), saved.getNickname(), saved.getEmail())
        );
    }

    private String resolveNickname(KakaoUserResponse kakaoUser) {
        if (kakaoUser.kakaoAccount() != null
            && kakaoUser.kakaoAccount().profile() != null
            && kakaoUser.kakaoAccount().profile().nickname() != null) {
            return kakaoUser.kakaoAccount().profile().nickname();
        }
        return "이게모노 집사";
    }

    private String resolveProfileImageUrl(KakaoUserResponse kakaoUser) {
        if (kakaoUser.kakaoAccount() != null && kakaoUser.kakaoAccount().profile() != null) {
            return kakaoUser.kakaoAccount().profile().profileImageUrl();
        }
        return null;
    }
}

package com.igemons.adapter.in.web.auth;

import com.igemons.application.auth.KakaoAuthService;
import com.igemons.application.auth.UserRepository;
import com.igemons.domain.users.User;
import com.igemons.infrastructure.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(KakaoAuthService kakaoAuthService, UserRepository userRepository, JwtService jwtService) {
        this.kakaoAuthService = kakaoAuthService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/kakao")
    public ResponseEntity<AuthResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        AuthResponse response = kakaoAuthService.loginWithKakao(request.code(), request.redirectUri());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserInfo> me(
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        Long userId;
        try {
            userId = jwtService.extractUserId(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return ResponseEntity.ok(
            new AuthResponse.UserInfo(user.getId().value(), user.getNickname(), user.getEmail())
        );
    }
}

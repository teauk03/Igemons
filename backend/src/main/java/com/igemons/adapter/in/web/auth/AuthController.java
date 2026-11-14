package com.igemons.adapter.in.web.auth;

import com.igemons.application.auth.LocalAuthService;
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

    private final LocalAuthService localAuthService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(LocalAuthService localAuthService, UserRepository userRepository, JwtService jwtService) {
        this.localAuthService = localAuthService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = localAuthService.register(request.email(), request.nickname(), request.password());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = localAuthService.login(request.email(), request.password());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
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

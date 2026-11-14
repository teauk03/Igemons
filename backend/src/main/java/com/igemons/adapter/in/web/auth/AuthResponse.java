package com.igemons.adapter.in.web.auth;

public record AuthResponse(String token, UserInfo user) {

    public record UserInfo(Long id, String nickname, String email) {}
}

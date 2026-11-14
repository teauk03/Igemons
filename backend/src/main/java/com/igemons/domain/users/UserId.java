package com.igemons.domain.users;

public record UserId(Long value) {

    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UserId는 양수여야 합니다.");
        }
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }    
}

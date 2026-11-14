package com.igemons.domain.pets;

import java.util.Objects;

public record PetId(Long value) {
    public PetId {
        Objects.requireNonNull(value, "PetId는 null일 수 없습니다.");
    }
 
    public static PetId of(long value) {
        return new PetId(value);
    }
}
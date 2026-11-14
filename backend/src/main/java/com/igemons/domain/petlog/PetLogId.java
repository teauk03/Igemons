package com.igemons.domain.petlog;

import java.util.Objects;

public record PetLogId(Long value) {

    public PetLogId {
        Objects.requireNonNull(value, "PetLogId는 null일 수 없습니다.");
    }

    public static PetLogId of(long value) {
        return new PetLogId(value);
    }
    
}
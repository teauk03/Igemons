package com.igemons.domain.posts;

import java.util.Objects;

public record PostId(Long value) {
    public PostId {
        Objects.requireNonNull(value, "PostId는 null일 수 없습니다.");
    }

    public static PostId of(long value) {
        return new PostId(value);
    }
}

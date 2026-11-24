package com.igemons.adapter.in.web.posts;

import com.igemons.application.posts.PostView;
import com.igemons.domain.posts.Post;
import com.igemons.domain.posts.PostCategory;

import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    PostCategory category,
    String title,
    String content,
    Long authorId,
    String authorNickname,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PostResponse from(PostView view) {
        Post p = view.post();
        return new PostResponse(p.getId().value(), p.getCategory(), p.getTitle(), p.getContent(),
                p.getAuthorId().value(), view.authorNickname(), p.getCreatedAt(), p.getUpdatedAt());
    }
}

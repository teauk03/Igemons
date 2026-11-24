package com.igemons.application.posts;

import com.igemons.domain.posts.PostCategory;

/**
 * 게시글 작성/수정 입력값
 */
public record PostCommand(
    PostCategory category,
    String title,
    String content
) {}

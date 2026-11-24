package com.igemons.application.posts;

import com.igemons.domain.posts.Post;

/**
 * 화면 표시용 게시글 + 작성자 닉네임
 * (탈퇴 등으로 작성자를 찾을 수 없으면 nickname은 null)
 */
public record PostView(Post post, String authorNickname) {}

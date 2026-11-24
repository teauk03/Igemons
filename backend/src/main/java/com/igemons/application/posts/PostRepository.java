package com.igemons.application.posts;

import com.igemons.domain.posts.Post;
import com.igemons.domain.posts.PostCategory;
import com.igemons.domain.posts.PostId;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Optional<Post> findById(PostId id);

    /**
     * 삭제되지 않은 게시글 목록 (최신순)
     * @param category null이면 전체
     * @param page 0부터 시작하는 페이지 번호
     */
    List<Post> findActive(PostCategory category, int page, int size);

    Post save(Post post);
}

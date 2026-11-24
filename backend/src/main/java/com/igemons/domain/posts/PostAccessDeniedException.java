package com.igemons.domain.posts;

import com.igemons.domain.DomainException;

/**
 * 작성자가 아닌 사용자가 게시글을 수정·삭제하려는 경우 발생하는 예외
 */
public class PostAccessDeniedException extends DomainException {
    public PostAccessDeniedException() {
        super("게시글에 대한 권한이 없습니다.");
    }
}

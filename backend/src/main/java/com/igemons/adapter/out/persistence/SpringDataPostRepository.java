package com.igemons.adapter.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.igemons.adapter.out.persistence.entity.PostJpaEntity;
import com.igemons.domain.posts.PostCategory;

import java.util.List;

interface SpringDataPostRepository extends JpaRepository<PostJpaEntity, Long> {
    /** 삭제되지 않은 전체 게시글 (최신순) */
    List<PostJpaEntity> findByDeletedAtIsNullOrderByCreatedAtDescIdDesc(Pageable pageable);

    /** 삭제되지 않은 분류별 게시글 (최신순) */
    List<PostJpaEntity> findByCategoryAndDeletedAtIsNullOrderByCreatedAtDescIdDesc(PostCategory category, Pageable pageable);
}

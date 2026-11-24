package com.igemons.adapter.out.persistence.entity;

import com.igemons.domain.posts.Post;
import com.igemons.domain.posts.PostCategory;
import com.igemons.domain.posts.PostId;
import com.igemons.domain.users.UserId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts", indexes = {
    @Index(name = "idx_posts_created", columnList = "created_at"),
    @Index(name = "idx_posts_category_created", columnList = "category, created_at")
})
public class PostJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostCategory category;

    @Column(nullable = false, length = Post.TITLE_MAX)
    private String title;

    @Column(nullable = false, length = Post.CONTENT_MAX)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected PostJpaEntity() {}

    public static PostJpaEntity from(Post post) {
        PostJpaEntity e = new PostJpaEntity();
        if (post.getId() != null) e.id = post.getId().value();
        e.authorId = post.getAuthorId().value();
        e.category = post.getCategory();
        e.title = post.getTitle();
        e.content = post.getContent();
        e.createdAt = post.getCreatedAt();
        e.updatedAt = post.getUpdatedAt();
        e.deletedAt = post.getDeletedAt();
        return e;
    }

    public Post toDomain() {
        return Post.restore(PostId.of(id), UserId.of(authorId), category, title, content,
                createdAt, updatedAt, deletedAt);
    }
}

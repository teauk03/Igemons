package com.igemons.domain.posts;

import java.time.LocalDateTime;
import java.util.Objects;

import com.igemons.domain.DomainException;
import com.igemons.domain.Guard;
import com.igemons.domain.users.UserId;

public class Post {
    public static final int TITLE_MAX = 100;
    public static final int CONTENT_MAX = 5000;

    private final PostId id;                 // 신규 생성 시 null
    private final UserId authorId;
    private PostCategory category;
    private String title;
    private String content;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Post(PostId id, UserId authorId, PostCategory category, String title, String content,
                 LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.authorId = Objects.requireNonNull(authorId);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.deletedAt = deletedAt;
        applyContent(category, title, content);
    }

    // ---------- 팩토리 ----------

    public static Post write(UserId authorId, PostCategory category, String title, String content,
                             LocalDateTime now) {
        return new Post(null, authorId, category, title, content, now, now, null);
    }

    public static Post restore(PostId id, UserId authorId, PostCategory category, String title,
                               String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                               LocalDateTime deletedAt) {
        Objects.requireNonNull(id, "restore requires id");
        return new Post(id, authorId, category, title, content, createdAt, updatedAt, deletedAt);
    }

    // ---------- 행위 ----------

    public void edit(UserId requester, PostCategory category, String title, String content,
                     LocalDateTime now) {
        verifyAuthor(requester);
        applyContent(category, title, content);
        this.updatedAt = now;
    }

    public void delete(UserId requester, LocalDateTime now) {
        verifyAuthor(requester);
        this.deletedAt = now;
    }

    /**
     * 요청자가 작성자인지 검증
     * @throws PostAccessDeniedException 작성자가 아니면 발생
     * @throws DomainException 삭제된 게시글이면 발생
     */
    public void verifyAuthor(UserId requester) {
        if (!authorId.equals(requester)) {
            throw new PostAccessDeniedException();
        }
        if (isDeleted()) {
            throw new DomainException("삭제된 게시글입니다.");
        }
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isNew() {
        return id == null;
    }

    private void applyContent(PostCategory category, String title, String content) {
        this.category = (category == null) ? PostCategory.FREE : category;
        this.title = Guard.requireText(title, "title", TITLE_MAX);
        this.content = Guard.requireText(content, "content", CONTENT_MAX);
    }

    // getter
    public PostId getId() { return id; }
    public UserId getAuthorId() { return authorId; }
    public PostCategory getCategory() { return category; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}

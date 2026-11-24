package com.igemons.application;

import com.igemons.application.auth.UserRepository;
import com.igemons.application.posts.PostCommand;
import com.igemons.application.posts.PostRepository;
import com.igemons.application.posts.PostService;
import com.igemons.application.posts.PostView;
import com.igemons.domain.DomainException;
import com.igemons.domain.posts.*;
import com.igemons.domain.users.User;
import com.igemons.domain.users.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;

class PostServiceTest {

    static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 18, 12, 0);
    static final UserId AUTHOR = UserId.of(1L);
    static final UserId STRANGER = UserId.of(2L);

    MutableClock clock;
    PostService postService;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(NOW);
        postService = new PostService(new InMemoryPosts(), new FakeUsers(Map.of(AUTHOR, "모찌집사")), clock);
    }

    @Test
    void 게시글_작성_조회_수정_삭제() {
        PostView written = postService.write(AUTHOR, new PostCommand(PostCategory.QNA, " 질문 ", "탈피 주기가 궁금해요"));
        Post post = written.post();
        assertThat(post.getId()).isNotNull();
        assertThat(post.getTitle()).isEqualTo("질문");
        assertThat(written.authorNickname()).isEqualTo("모찌집사");

        assertThat(postService.get(post.getId()).post().getContent()).isEqualTo("탈피 주기가 궁금해요");

        clock.advance(Duration.ofMinutes(5));
        Post edited = postService.edit(AUTHOR, post.getId(), new PostCommand(null, "수정", "본문")).post();
        assertThat(edited.getCategory()).isEqualTo(PostCategory.FREE);
        assertThat(edited.getUpdatedAt()).isEqualTo(NOW.plusMinutes(5));
        assertThat(edited.getCreatedAt()).isEqualTo(NOW);

        postService.delete(AUTHOR, post.getId());
        assertThat(postService.list(null, 0, 20)).isEmpty();
        assertThatThrownBy(() -> postService.get(post.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void 작성자가_아니면_수정_삭제할_수_없다() {
        PostId id = postService.write(AUTHOR, new PostCommand(null, "제목", "본문")).post().getId();
        PostCommand cmd = new PostCommand(null, "남의 글", "수정");
        assertThatThrownBy(() -> postService.edit(STRANGER, id, cmd)).isInstanceOf(PostAccessDeniedException.class);
        assertThatThrownBy(() -> postService.delete(STRANGER, id)).isInstanceOf(PostAccessDeniedException.class);
    }

    @Test
    void 제목_본문은_필수이고_길이_제한이_있다() {
        assertThatThrownBy(() -> postService.write(AUTHOR, new PostCommand(null, " ", "본문")))
                .isInstanceOf(DomainException.class).hasMessageContaining("title");
        assertThatThrownBy(() -> postService.write(AUTHOR, new PostCommand(null, "제목", null)))
                .isInstanceOf(DomainException.class).hasMessageContaining("content");
        String tooLong = "가".repeat(Post.TITLE_MAX + 1);
        assertThatThrownBy(() -> postService.write(AUTHOR, new PostCommand(null, tooLong, "본문")))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void 목록은_최신순이고_분류_필터와_페이지를_지원한다() {
        for (int i = 1; i <= 3; i++) {
            clock.advance(Duration.ofMinutes(1));
            PostCategory c = (i == 2) ? PostCategory.HUSBANDRY : PostCategory.FREE;
            postService.write(AUTHOR, new PostCommand(c, "글" + i, "본문"));
        }
        assertThat(postService.list(null, 0, 20)).extracting(v -> v.post().getTitle())
                .containsExactly("글3", "글2", "글1");
        assertThat(postService.list(PostCategory.HUSBANDRY, 0, 20)).extracting(v -> v.post().getTitle())
                .containsExactly("글2");
        assertThat(postService.list(null, 1, 2)).extracting(v -> v.post().getTitle())
                .containsExactly("글1");
        // 범위를 벗어난 값은 보정
        assertThat(postService.list(null, -1, 0)).hasSize(1);
    }

    // ---------- fakes ----------

    static class MutableClock extends Clock {
        private Instant now;
        MutableClock(LocalDateTime t) { this.now = t.atZone(ZoneId.systemDefault()).toInstant(); }
        void advance(Duration d) { now = now.plus(d); }
        @Override public ZoneId getZone() { return ZoneId.systemDefault(); }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }

    static class InMemoryPosts implements PostRepository {
        private final Map<Long, Post> store = new HashMap<>();
        private final AtomicLong seq = new AtomicLong();

        @Override public Optional<Post> findById(PostId id) { return Optional.ofNullable(store.get(id.value())); }

        @Override public List<Post> findActive(PostCategory category, int page, int size) {
            return store.values().stream()
                    .filter(p -> !p.isDeleted())
                    .filter(p -> category == null || p.getCategory() == category)
                    .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                    .skip((long) page * size).limit(size).toList();
        }

        @Override public Post save(Post p) {
            PostId id = p.isNew() ? PostId.of(seq.incrementAndGet()) : p.getId();
            Post saved = Post.restore(id, p.getAuthorId(), p.getCategory(), p.getTitle(), p.getContent(),
                    p.getCreatedAt(), p.getUpdatedAt(), p.getDeletedAt());
            store.put(id.value(), saved);
            return saved;
        }
    }

    record FakeUsers(Map<UserId, String> nicknames) implements UserRepository {
        @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
        @Override public Optional<User> findById(Long id) { return Optional.empty(); }
        @Override public User save(User user) { return user; }
        @Override public Map<UserId, String> findNicknames(Collection<UserId> ids) {
            Map<UserId, String> out = new HashMap<>();
            ids.forEach(id -> { if (nicknames.containsKey(id)) out.put(id, nicknames.get(id)); });
            return out;
        }
    }
}

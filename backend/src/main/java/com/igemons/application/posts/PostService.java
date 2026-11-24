package com.igemons.application.posts;

import lombok.RequiredArgsConstructor;
import com.igemons.application.NotFoundException;
import com.igemons.application.auth.UserRepository;
import com.igemons.domain.posts.Post;
import com.igemons.domain.posts.PostCategory;
import com.igemons.domain.posts.PostId;
import com.igemons.domain.users.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 50;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    /**
     * 게시글 작성
     *
     * @param author 작성자 ID
     * @param cmd    분류·제목·본문
     */
    @Transactional
    public PostView write(UserId author, PostCommand cmd) {
        Post post = Post.write(author, cmd.category(), cmd.title(), cmd.content(), LocalDateTime.now(clock));
        return withAuthor(postRepository.save(post));
    }

    /**
     * 게시글 목록 (로그인 없이 조회 가능, 최신순)
     *
     * @param category null이면 전체
     * @param page     0부터 시작 (음수는 0으로 보정)
     * @param size     1~{@value #MAX_PAGE_SIZE} 범위로 보정
     */
    @Transactional(readOnly = true)
    public List<PostView> list(PostCategory category, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);
        List<Post> posts = postRepository.findActive(category, safePage, safeSize);

        // 작성자 닉네임은 한 번에 조회 (게시글마다 조회하지 않도록)
        Set<UserId> authorIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toSet());
        Map<UserId, String> nicknames = userRepository.findNicknames(authorIds);
        return posts.stream().map(p -> new PostView(p, nicknames.get(p.getAuthorId()))).toList();
    }

    /** 게시글 단건 조회 (로그인 없이 조회 가능) */
    @Transactional(readOnly = true)
    public PostView get(PostId postId) {
        return withAuthor(loadActive(postId));
    }

    /**
     * 게시글 수정 (작성자만 가능, 도메인에서 검증)
     */
    @Transactional
    public PostView edit(UserId requester, PostId postId, PostCommand cmd) {
        Post post = loadActive(postId);
        post.edit(requester, cmd.category(), cmd.title(), cmd.content(), LocalDateTime.now(clock));
        return withAuthor(postRepository.save(post));
    }

    /**
     * 게시글 논리 삭제(Soft Delete) (작성자만 가능, 도메인에서 검증)
     */
    @Transactional
    public void delete(UserId requester, PostId postId) {
        Post post = loadActive(postId);
        post.delete(requester, LocalDateTime.now(clock));
        postRepository.save(post);
    }

    private PostView withAuthor(Post post) {
        String nickname = userRepository.findNicknames(Set.of(post.getAuthorId())).get(post.getAuthorId());
        return new PostView(post, nickname);
    }

    /**
     * 삭제되지 않은 게시글 단건 조회
     *
     * @throws NotFoundException 없거나 이미 삭제된 경우
     */
    private Post loadActive(PostId postId) {
        return postRepository.findById(postId)
            .filter(p -> !p.isDeleted())
            .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
    }
}

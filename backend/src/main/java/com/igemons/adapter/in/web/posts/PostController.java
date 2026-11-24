package com.igemons.adapter.in.web.posts;

import lombok.RequiredArgsConstructor;
import com.igemons.adapter.in.web.auth.CurrentUser;
import com.igemons.application.posts.PostService;
import com.igemons.domain.posts.PostCategory;
import com.igemons.domain.posts.PostId;
import com.igemons.domain.users.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 커뮤니티 게시글
 * 조회(목록·단건)는 로그인 없이 가능, 작성·수정·삭제는 로그인 필요 (수정·삭제는 작성자만)
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> write(@CurrentUser UserId me, @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PostResponse.from(postService.write(me, request.toCommand())));
    }

    /** ?category=FREE|HUSBANDRY|QNA (생략 시 전체), ?page=0&size=20 */
    @GetMapping
    public List<PostResponse> list(@RequestParam(required = false) PostCategory category,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "" + PostService.DEFAULT_PAGE_SIZE) int size) {
        return postService.list(category, page, size).stream().map(PostResponse::from).toList();
    }

    @GetMapping("/{postId}")
    public PostResponse get(@PathVariable long postId) {
        return PostResponse.from(postService.get(PostId.of(postId)));
    }

    /** 분류·제목·본문 전체 교체 */
    @PutMapping("/{postId}")
    public PostResponse edit(@CurrentUser UserId me, @PathVariable long postId,
                             @RequestBody PostRequest request) {
        return PostResponse.from(postService.edit(me, PostId.of(postId), request.toCommand()));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@CurrentUser UserId me, @PathVariable long postId) {
        postService.delete(me, PostId.of(postId));
        return ResponseEntity.noContent().build();
    }
}

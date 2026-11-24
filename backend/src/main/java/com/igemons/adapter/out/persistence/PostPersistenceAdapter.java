package com.igemons.adapter.out.persistence;

import lombok.RequiredArgsConstructor;

import com.igemons.adapter.out.persistence.entity.PostJpaEntity;
import com.igemons.application.posts.PostRepository;
import com.igemons.domain.posts.Post;
import com.igemons.domain.posts.PostCategory;
import com.igemons.domain.posts.PostId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostPersistenceAdapter implements PostRepository {

    private final SpringDataPostRepository repository;

    @Override
    public Optional<Post> findById(PostId id) {
        return repository.findById(id.value()).map(PostJpaEntity::toDomain);
    }

    @Override
    public List<Post> findActive(PostCategory category, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<PostJpaEntity> rows = (category == null)
            ? repository.findByDeletedAtIsNullOrderByCreatedAtDescIdDesc(pageable)
            : repository.findByCategoryAndDeletedAtIsNullOrderByCreatedAtDescIdDesc(category, pageable);
        return rows.stream().map(PostJpaEntity::toDomain).toList();
    }

    @Override
    public Post save(Post post) {
        return repository.save(PostJpaEntity.from(post)).toDomain();
    }
}

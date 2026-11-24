package com.igemons.adapter.in.web.posts;

import com.igemons.application.posts.PostCommand;
import com.igemons.domain.posts.PostCategory;

/** category를 보내지 않으면 FREE(자유) */
public record PostRequest(
    PostCategory category,
    String title,
    String content
) {
    public PostCommand toCommand() {
        return new PostCommand(category, title, content);
    }
}

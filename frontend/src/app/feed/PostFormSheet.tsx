"use client";

import { useState } from "react";
import Popover, { Anchor } from "../components/Popover";
import { CATEGORIES, CATEGORY_LABEL, CONTENT_MAX, Post, PostCategory, TITLE_MAX } from "./types";

export interface PostInput {
  category: PostCategory;
  title: string;
  content: string;
}

const fieldClass = "w-full min-w-0 border border-gray-200 rounded-lg px-3 py-2 text-sm";

/**
 * 글쓰기/수정 창 (post가 있으면 수정)
 * 부모에서 key로 렌더링해 대상이 바뀌면 입력값이 새로 초기화되도록 설정
 */
export default function PostFormSheet({
  post,
  defaultCategory,
  anchor,
  busy,
  onSubmit,
  onClose,
}: {
  post?: Post;
  defaultCategory: PostCategory;
  anchor: Anchor;
  busy: boolean;
  onSubmit: (input: PostInput) => void;
  onClose: () => void;
}) {
  const [category, setCategory] = useState<PostCategory>(post?.category ?? defaultCategory);
  const [title, setTitle] = useState(post?.title ?? "");
  const [content, setContent] = useState(post?.content ?? "");
  const canSubmit = title.trim() !== "" && content.trim() !== "" && !busy;

  return (
    <Popover anchor={anchor} title={post ? "글 수정" : "글쓰기"} onClose={onClose}>
      <form
        className="flex flex-col gap-3"
        onSubmit={(e) => {
          e.preventDefault();
          if (canSubmit) onSubmit({ category, title: title.trim(), content: content.trim() });
        }}
      >
        <div className="flex gap-2">
          {CATEGORIES.map((c) => (
            <button
              key={c}
              type="button"
              onClick={() => setCategory(c)}
              className={`px-3 py-1 rounded-full text-xs ${
                category === c ? "bg-emerald-600 text-white font-semibold" : "bg-gray-100 text-gray-500"
              }`}
            >
              {CATEGORY_LABEL[c]}
            </button>
          ))}
        </div>
        <input
          className={fieldClass}
          placeholder="제목"
          value={title}
          maxLength={TITLE_MAX}
          onChange={(e) => setTitle(e.target.value)}
          autoFocus
        />
        <div className="flex flex-col gap-1">
          <textarea
            className={`${fieldClass} min-h-40 resize-y`}
            placeholder="내용을 입력하세요"
            value={content}
            maxLength={CONTENT_MAX}
            onChange={(e) => setContent(e.target.value)}
          />
          <span className="self-end text-[10px] text-gray-400">
            {content.length}/{CONTENT_MAX}
          </span>
        </div>
        <button
          type="submit"
          disabled={!canSubmit}
          className="py-2.5 rounded-xl bg-emerald-600 text-white text-sm font-semibold disabled:bg-gray-300"
        >
          {busy ? "저장 중..." : post ? "수정하기" : "등록하기"}
        </button>
      </form>
    </Popover>
  );
}

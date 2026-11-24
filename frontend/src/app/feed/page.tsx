"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { PenLine } from "lucide-react";
import { api } from "../lib/api";
import { useAuth } from "../context/AuthContext";
import { Anchor, anchorOf } from "../components/Popover";
import PostFormSheet, { PostInput } from "./PostFormSheet";
import { CATEGORIES, CATEGORY_LABEL, Post, PostCategory, formatDateTime } from "./types";

const PAGE_SIZE = 20;

const postsUrl = (category: PostCategory | null, page: number) =>
  `/api/posts?page=${page}&size=${PAGE_SIZE}${category ? `&category=${category}` : ""}`;

type FormState = { anchor: Anchor; post?: Post } | null;

export default function FeedPage() {
  const { user } = useAuth();
  const router = useRouter();

  const [category, setCategory] = useState<PostCategory | null>(null); // null = 전체
  const [version, setVersion] = useState(0); // 작성·수정·삭제 후 목록 새로고침용
  const listKey = `${category}-${version}`;

  // 어떤 조건(listKey)으로 불러온 결과인지 함께 저장 → 조건이 바뀌면 자동으로 "불러오는 중"
  const [list, setList] = useState<{ key: string; items: Post[]; page: number; hasMore: boolean } | null>(null);
  const [error, setError] = useState<{ key: string; message: string } | null>(null);
  const [loadingMore, setLoadingMore] = useState(false);

  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [form, setForm] = useState<FormState>(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    let cancelled = false;
    api
      .get<Post[]>(postsUrl(category, 0))
      .then((items) => {
        if (!cancelled) setList({ key: listKey, items, page: 0, hasMore: items.length === PAGE_SIZE });
      })
      .catch((e: Error) => {
        if (!cancelled) setError({ key: listKey, message: e.message });
      });
    return () => {
      cancelled = true;
    };
  }, [category, listKey]);

  const current = list?.key === listKey ? list : null;
  const currentError = error?.key === listKey ? error.message : null;
  const isLoading = !current && !currentError;

  const loadMore = async () => {
    if (!current) return;
    setLoadingMore(true);
    try {
      const next = await api.get<Post[]>(postsUrl(category, current.page + 1));
      setList({
        key: current.key,
        items: [...current.items, ...next.filter((p) => !current.items.some((c) => c.id === p.id))],
        page: current.page + 1,
        hasMore: next.length === PAGE_SIZE,
      });
    } catch (e) {
      alert((e as Error).message);
    } finally {
      setLoadingMore(false);
    }
  };

  const openWrite = (e: React.MouseEvent<HTMLElement>) => {
    if (!user) {
      if (confirm("글을 쓰려면 로그인이 필요합니다. 로그인할까요?")) router.push("/login");
      return;
    }
    setForm({ anchor: anchorOf(e) });
  };

  const submit = async (input: PostInput) => {
    setBusy(true);
    try {
      if (form?.post) {
        await api.put<Post>(`/api/posts/${form.post.id}`, input);
      } else {
        await api.post<Post>("/api/posts", input);
      }
      setForm(null);
      setVersion((v) => v + 1);
    } catch (e) {
      alert((e as Error).message);
    } finally {
      setBusy(false);
    }
  };

  const remove = async (post: Post) => {
    if (!confirm("이 글을 삭제할까요?")) return;
    try {
      await api.delete(`/api/posts/${post.id}`);
      setExpandedId(null);
      setVersion((v) => v + 1);
    } catch (e) {
      alert((e as Error).message);
    }
  };

  const tabClass = (active: boolean) =>
    `px-3 py-1 rounded-full ${active ? "bg-emerald-50 text-emerald-700 font-semibold" : "bg-gray-100 text-gray-500"}`;

  return (
    <div className="p-4 flex flex-col gap-4">
      <header className="flex justify-between items-center">
        <h1 className="text-lg font-bold text-gray-900">커뮤니티 피드</h1>
        <button
          onClick={openWrite}
          className="flex items-center gap-1 text-xs bg-emerald-600 text-white font-semibold px-3 py-1.5 rounded-full"
        >
          <PenLine size={13} /> 글쓰기
        </button>
      </header>

      <div className="flex gap-2 text-xs">
        <button className={tabClass(category === null)} onClick={() => setCategory(null)}>
          전체
        </button>
        {CATEGORIES.map((c) => (
          <button key={c} className={tabClass(category === c)} onClick={() => setCategory(c)}>
            {CATEGORY_LABEL[c]}
          </button>
        ))}
      </div>

      {isLoading && <div className="py-16 text-center text-sm text-gray-400">불러오는 중...</div>}
      {currentError && (
        <div className="py-16 text-center text-sm text-red-400">
          데이터를 불러오지 못했습니다.
          <button className="block mx-auto mt-2 text-xs text-gray-500 underline" onClick={() => setVersion((v) => v + 1)}>
            다시 시도
          </button>
        </div>
      )}

      {current && current.items.length === 0 && (
        <div className="py-16 text-center text-sm text-gray-400">아직 게시글이 없어요. 첫 글을 남겨 보세요!</div>
      )}

      {current && current.items.length > 0 && (
        <div className="flex flex-col gap-3">
          {current.items.map((post) => {
            const expanded = expandedId === post.id;
            const mine = user?.id === post.authorId;
            const edited = post.updatedAt.slice(0, 16) !== post.createdAt.slice(0, 16);
            return (
              <article
                key={post.id}
                onClick={() => setExpandedId(expanded ? null : post.id)}
                className="p-4 bg-gray-50 border border-gray-100 rounded-2xl flex flex-col gap-2 cursor-pointer"
              >
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 rounded-full bg-gray-300" />
                  <div className="flex-1 min-w-0">
                    <p className="text-xs font-semibold text-gray-800">{post.authorNickname ?? "알 수 없음"}</p>
                    <p className="text-[10px] text-gray-400">
                      {formatDateTime(post.createdAt)}
                      {edited && " · 수정됨"}
                    </p>
                  </div>
                  <span className="text-[10px] px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700">
                    {CATEGORY_LABEL[post.category]}
                  </span>
                </div>
                <h2 className="text-sm font-semibold text-gray-900 break-words">{post.title}</h2>
                <p className={`text-xs text-gray-700 whitespace-pre-wrap break-words ${expanded ? "" : "line-clamp-3"}`}>
                  {post.content}
                </p>
                {expanded && mine && (
                  <div className="flex justify-end gap-2 pt-1" onClick={(e) => e.stopPropagation()}>
                    <button
                      className="text-xs px-3 py-1 rounded-full bg-white border border-gray-200 text-gray-600"
                      onClick={(e) => setForm({ anchor: anchorOf(e), post })}
                    >
                      수정
                    </button>
                    <button
                      className="text-xs px-3 py-1 rounded-full bg-white border border-red-200 text-red-500"
                      onClick={() => remove(post)}
                    >
                      삭제
                    </button>
                  </div>
                )}
              </article>
            );
          })}
          {current.hasMore && (
            <button
              onClick={loadMore}
              disabled={loadingMore}
              className="py-2 text-xs text-gray-500 bg-gray-100 rounded-xl disabled:opacity-60"
            >
              {loadingMore ? "불러오는 중..." : "더 보기"}
            </button>
          )}
        </div>
      )}

      {form && (
        <PostFormSheet
          key={form.post?.id ?? "new"}
          post={form.post}
          defaultCategory={category ?? "FREE"}
          anchor={form.anchor}
          busy={busy}
          onSubmit={submit}
          onClose={() => setForm(null)}
        />
      )}
    </div>
  );
}

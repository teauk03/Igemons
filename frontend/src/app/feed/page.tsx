"use client";

import { useEffect, useState } from "react";
import { MessageSquare, Heart } from "lucide-react";
import { api } from "../lib/api";

interface Post {
  id: number;
  author: string;
  timeAgo: string;
  content: string;
  likeCount: number;
  commentCount: number;
}

export default function FeedPage() {
  const [posts, setPosts] = useState<Post[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api
      .get<Post[]>("/api/feed/posts")
      .then(setPosts)
      .catch(() => setError(true))
      .finally(() => setIsLoading(false));
  }, []);

  return (
    <div className="p-4 flex flex-col gap-4">
      <header className="flex justify-between items-center">
        <h1 className="text-lg font-bold text-gray-900">커뮤니티 피드</h1>
        <button className="text-xs bg-emerald-600 text-white font-semibold px-3 py-1.5 rounded-full">
          글쓰기
        </button>
      </header>

      <div className="flex gap-2 text-xs">
        <button className="px-3 py-1 rounded-full bg-emerald-50 text-emerald-700 font-semibold">
          전체
        </button>
        <button className="px-3 py-1 rounded-full bg-gray-100 text-gray-500">사육방식</button>
        <button className="px-3 py-1 rounded-full bg-gray-100 text-gray-500">QnA</button>
      </div>

      {isLoading && (
        <div className="py-16 text-center text-sm text-gray-400">불러오는 중...</div>
      )}
      {error && (
        <div className="py-16 text-center text-sm text-red-400">
          데이터를 불러오지 못했습니다.
        </div>
      )}

      {!isLoading && !error && (
        <div className="flex flex-col gap-3">
          {posts.map((post) => (
            <div
              key={post.id}
              className="p-4 bg-gray-50 border border-gray-100 rounded-2xl flex flex-col gap-2"
            >
              <div className="flex items-center gap-2">
                <div className="w-7 h-7 rounded-full bg-gray-300" />
                <div>
                  <p className="text-xs font-semibold text-gray-800">{post.author}</p>
                  <p className="text-[10px] text-gray-400">{post.timeAgo}</p>
                </div>
              </div>
              <p className="text-xs text-gray-700">{post.content}</p>
              <div className="flex items-center gap-4 text-xs text-gray-400 pt-1">
                <span className="flex items-center gap-1">
                  <Heart size={14} /> {post.likeCount}
                </span>
                <span className="flex items-center gap-1">
                  <MessageSquare size={14} /> {post.commentCount}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

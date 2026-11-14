"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import Header from "./components/Header";
import { api } from "./lib/api";

interface PopularAnimal {
  id: number;
  name: string;
}

interface CommunityPreview {
  popularPost: { id: number; title: string };
  latestPost: { id: number; title: string };
}

export default function HomePage() {
  const [popularAnimals, setPopularAnimals] = useState<PopularAnimal[]>([]);
  const [communityPreview, setCommunityPreview] = useState<CommunityPreview | null>(null);

  useEffect(() => {
    api
      .get<PopularAnimal[]>("/api/home/popular-animals")
      .then(setPopularAnimals)
      .catch(() => {});
    api
      .get<CommunityPreview>("/api/home/community-preview")
      .then(setCommunityPreview)
      .catch(() => {});
  }, []);

  return (
    <div className="flex flex-col gap-5 p-4">
      <Header />

      {/* 원클릭 서류 작성 CTA 카드 */}
      <section className="bg-emerald-100/70 border border-emerald-200 rounded-2xl p-5 flex flex-col justify-between h-36">
        <div>
          <h2 className="text-lg font-bold text-emerald-950">원클릭 서류 작성</h2>
          <p className="text-xs text-emerald-800 mt-1">간편하게 작성하는 생물 양도양수 서류</p>
        </div>
        <Link
          href="/contract"
          className="self-end text-xs font-semibold text-emerald-900 bg-white/80 px-3 py-1.5 rounded-full shadow-sm hover:bg-white"
        >
          작성하러 가기 →
        </Link>
      </section>

      {/* 인기 생물 그리드 */}
      {popularAnimals.length > 0 && (
        <section>
          <h3 className="text-sm font-semibold text-gray-800 mb-3">요즘 인기 많은 생물</h3>
          <div className="grid grid-cols-4 gap-2 text-center text-xs text-gray-600">
            {popularAnimals.map((animal) => (
              <div
                key={animal.id}
                className="flex flex-col items-center justify-center p-2.5 bg-gray-50 rounded-xl hover:bg-gray-100"
              >
                <span className="w-8 h-8 rounded-full bg-emerald-50 flex items-center justify-center text-sm mb-1">
                  🦎
                </span>
                <span>{animal.name}</span>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* 최신/인기 게시물 미리보기 */}
      <section className="flex flex-col gap-2">
        <h3 className="text-sm font-semibold text-gray-800">커뮤니티 소식</h3>
        <div className="grid grid-cols-2 gap-3">
          <div className="h-28 bg-emerald-50/50 border border-emerald-100 rounded-xl p-3 flex flex-col justify-end text-xs text-gray-600">
            {communityPreview?.popularPost?.title ?? ""}
          </div>
          <div className="h-28 bg-emerald-50/50 border border-emerald-100 rounded-xl p-3 flex flex-col justify-end text-xs text-gray-600">
            {communityPreview?.latestPost?.title ?? ""}
          </div>
        </div>
      </section>
    </div>
  );
}

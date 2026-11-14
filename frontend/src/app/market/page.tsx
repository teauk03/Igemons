"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { api } from "../lib/api";

interface Listing {
  id: number;
  title: string;
  price: number;
  tag: string;
  shop: string;
}

export default function MarketPage() {
  const [listings, setListings] = useState<Listing[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    api
      .get<Listing[]>("/api/market/listings")
      .then(setListings)
      .catch(() => setError(true))
      .finally(() => setIsLoading(false));
  }, []);

  return (
    <div className="p-4 flex flex-col gap-4">
      <header className="flex justify-between items-center">
        <h1 className="text-lg font-bold text-gray-900">개체 분양</h1>
        {!isLoading && !error && (
          <span className="text-xs text-gray-500">총 {listings.length}마리</span>
        )}
      </header>

      {/* 탭 필터 */}
      <div className="flex gap-2 text-xs font-medium">
        <button className="px-3 py-1.5 rounded-full bg-emerald-600 text-white">전체</button>
        <button className="px-3 py-1.5 rounded-full bg-gray-100 text-gray-600 hover:bg-gray-200">
          제휴할인
        </button>
        <button className="px-3 py-1.5 rounded-full bg-gray-100 text-gray-600 hover:bg-gray-200">
          호텔링
        </button>
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
          {listings.map((item) => (
            <Link
              key={item.id}
              href={`/market/${item.id}`}
              className="flex items-center gap-3 p-3 bg-gray-50 border border-gray-100 rounded-2xl hover:bg-gray-100/70 transition"
            >
              <div className="w-16 h-16 rounded-xl bg-emerald-100 flex items-center justify-center text-2xl">
                🦎
              </div>
              <div className="flex-1">
                <span className="text-[10px] font-semibold text-emerald-600 bg-emerald-50 px-1.5 py-0.5 rounded">
                  {item.tag}
                </span>
                <h3 className="text-sm font-semibold text-gray-800 mt-1">{item.title}</h3>
                <p className="text-xs text-gray-400">{item.shop}</p>
                <p className="text-sm font-bold text-gray-900 mt-0.5">
                  {item.price.toLocaleString()}원
                </p>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}

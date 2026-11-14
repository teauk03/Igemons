"use client";

import { useEffect, useState } from "react";
import { Plus } from "lucide-react";
import { useRouter } from "next/navigation";
import { useAuth } from "../context/AuthContext";
import { api } from "../lib/api";

interface FeedingRecord {
  food: string;
  amount: string;
}

interface Pet {
  id: number;
  name: string;
  lastFedAt: string;
  feedingRecords: FeedingRecord[];
  lastMoltDate: string;
  nextMoltDate: string;
  moltCycleDays: number;
}

export default function HandbookPage() {
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();
  const [pets, setPets] = useState<Pet[]>([]);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (authLoading) return;
    if (!user) {
      setIsLoading(false);
      return;
    }
    api
      .get<Pet[]>("/api/handbook/pets")
      .then((data) => {
        setPets(data);
        if (data.length > 0) setSelectedId(data[0].id);
      })
      .catch(() => setError(true))
      .finally(() => setIsLoading(false));
  }, [user, authLoading]);

  if (authLoading || isLoading) {
    return (
      <div className="flex items-center justify-center h-40 text-sm text-gray-400">
        불러오는 중...
      </div>
    );
  }

  /* 로그인 전 */
  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-5 p-6 min-h-[70vh]">
        <div className="text-5xl">📋</div>
        <div className="text-center">
          <h2 className="text-base font-bold text-gray-800">건강수첩</h2>
          <p className="text-xs text-gray-400 mt-2 leading-relaxed">
            로그인하면 내 생물의 급여·탈피 기록을<br />관리할 수 있어요.
          </p>
        </div>
        <button
          onClick={() => router.push("/login")}
          className="px-8 py-3 bg-emerald-600 text-white text-sm font-semibold rounded-xl hover:bg-emerald-700 transition-colors"
        >
          로그인하기
        </button>
      </div>
    );
  }

  if (error) {
    return (
      <div className="py-16 text-center text-sm text-red-400">
        데이터를 불러오지 못했습니다.
      </div>
    );
  }

  const selectedPet = pets.find((p) => p.id === selectedId) ?? null;

  return (
    <div className="p-4 flex flex-col gap-5">
      <header className="flex justify-between items-center">
        <h1 className="text-lg font-bold text-gray-900">건강수첩</h1>
      </header>

      {/* 개체 선택 탭 */}
      <div className="flex items-center gap-3 overflow-x-auto pb-1">
        {pets.map((pet) => (
          <button
            key={pet.id}
            onClick={() => setSelectedId(pet.id)}
            className={`flex flex-col items-center gap-1 min-w-[56px] ${
              pet.id === selectedId ? "opacity-100 font-bold" : "opacity-60"
            }`}
          >
            <div
              className={`w-14 h-14 rounded-full border-2 flex items-center justify-center text-xl ${
                pet.id === selectedId
                  ? "border-emerald-500 bg-emerald-50"
                  : "border-gray-200 bg-gray-100"
              }`}
            >
              🦎
            </div>
            <span className="text-xs text-gray-700">{pet.name}</span>
          </button>
        ))}
        <button className="flex flex-col items-center gap-1 min-w-[56px]">
          <div className="w-14 h-14 rounded-full border-2 border-dashed border-gray-300 flex items-center justify-center text-gray-400">
            <Plus size={20} />
          </div>
          <span className="text-xs text-gray-400">추가</span>
        </button>
      </div>

      {pets.length === 0 && (
        <div className="py-16 text-center text-sm text-gray-400">
          <p>등록된 생물이 없습니다.</p>
          <p className="text-xs mt-1">+ 추가 버튼으로 생물을 등록해보세요.</p>
        </div>
      )}

      {selectedPet && (
        <>
          {/* 급여 카드 */}
          <div className="bg-emerald-50/70 border border-emerald-100 rounded-2xl p-4 flex flex-col gap-3">
            <div className="flex justify-between items-center">
              <span className="text-xs font-semibold text-emerald-900">급여 기록</span>
              <span className="text-xs text-gray-500">마지막: {selectedPet.lastFedAt}</span>
            </div>
            <div className="flex flex-col gap-1.5">
              {selectedPet.feedingRecords.map((record, i) => (
                <div
                  key={i}
                  className="bg-white p-2.5 rounded-xl text-xs flex justify-between"
                >
                  <span className="text-gray-700">{record.food}</span>
                  <span className="font-semibold text-emerald-700">{record.amount}</span>
                </div>
              ))}
            </div>
          </div>

          {/* 탈피 기록 카드 */}
          <div className="bg-emerald-50/70 border border-emerald-100 rounded-2xl p-4 flex flex-col gap-2">
            <span className="text-xs font-semibold text-emerald-900">탈피 주기</span>
            <div className="bg-white p-3 rounded-xl flex justify-between text-xs">
              <div>
                <p className="text-gray-400">마지막 탈피</p>
                <p className="font-semibold text-gray-800 mt-0.5">{selectedPet.lastMoltDate}</p>
              </div>
              <div className="text-right">
                <p className="text-gray-400">다음 예상 (주기 {selectedPet.moltCycleDays}일)</p>
                <p className="font-semibold text-emerald-600 mt-0.5">{selectedPet.nextMoltDate}</p>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

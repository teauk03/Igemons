"use client";

import { useEffect, useRef, useState } from "react";
import { Search, X, ChevronRight } from "lucide-react";
import Link from "next/link";

interface SearchModalProps {
  isOpen: boolean;
  onClose: () => void;
}

// 분양 중인 동물 이름 목록 (추후 백엔드 API에서 이름 데이터만 받아오도록 교체)
const ADOPTION_ANIMALS = [
  { id: 1, name: "크레스티드 게코" },
  { id: 2, name: "크레스티드 게코 카푸치노" },
  { id: 3, name: "크레스티드 게코 릴리 화이트" },
  { id: 4, name: "레오파드 게코" },
  { id: 5, name: "레오파드 게코 블러드 텐저린" },
  { id: 6, name: "가고일 게코" },
  { id: 7, name: "차화 게코" },
  { id: 8, name: "헤르만 육지거북" },
  { id: 9, name: "호스필드 육지거북" },
  { id: 10, name: "화이트트리프록" },
  { id: 11, name: "다트프록" },
  { id: 12, name: "볼파이톤" },
  { id: 13, name: "콘스네이크" },
  { id: 14, name: "비어디드래곤" },
];

export default function SearchModal({ isOpen, onClose }: SearchModalProps) {
  if (!isOpen) return null;

  return <SearchModalContent onClose={onClose} />;
}

function SearchModalContent({ onClose }: Pick<SearchModalProps, "onClose">) {
  const [searchTerm, setSearchTerm] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);

  // 모달 마운트 시 인풋 포커스 및 배경 스크롤 방지
  useEffect(() => {
    inputRef.current?.focus();
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
    };
    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.body.style.overflow = previousOverflow;
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [onClose]);

  // 입력된 텍스트가 포함된 동물 이름 필터링 (대소문자/공백 무시)
  const filteredAnimals = searchTerm.trim() === ""
    ? []
    : ADOPTION_ANIMALS.filter((animal) =>
        animal.name.replace(/\s+/g, "").toLowerCase().includes(searchTerm.replace(/\s+/g, "").toLowerCase())
      );

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 p-4 animate-in fade-in duration-150"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-label="동물 검색"
        onClick={(event) => event.stopPropagation()}
        className="w-full max-w-[430px] max-h-[min(70vh,600px)] rounded-2xl bg-white flex flex-col p-4 shadow-xl"
      >
        {/* 상단 텍스트 검색 입력창 */}
        <div className="flex items-center gap-2">
          <div className="flex-1 flex items-center gap-2 bg-gray-100 rounded-xl px-3 py-2.5">
            <Search size={18} className="text-gray-400 shrink-0" />
            <input
              ref={inputRef}
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="분양할 동물 이름을 입력하세요"
              className="w-full bg-transparent text-sm text-gray-800 placeholder-gray-400 focus:outline-none"
            />
          </div>
          <button
            type="button"
            onClick={onClose}
            className="p-1.5 text-gray-500 hover:text-gray-800 hover:bg-gray-100 rounded-lg transition-colors"
            aria-label="닫기"
          >
            <X size={18} />
          </button>
        </div>

        {/* 텍스트 매칭 동물 이름 목록 */}
        {searchTerm.trim() !== "" && (
          <div className="max-h-[min(50vh,420px)] overflow-y-auto pt-2">
            {filteredAnimals.length > 0 ? (
            <ul className="divide-y divide-gray-100">
              {filteredAnimals.map((animal) => (
                <li key={animal.id}>
                  <Link
                    href={`/market?query=${encodeURIComponent(animal.name)}`}
                    onClick={onClose}
                    className="flex items-center justify-between py-3.5 px-2 hover:bg-emerald-50/50 rounded-xl transition-colors group"
                  >
                    <span className="text-sm font-medium text-gray-800 group-hover:text-emerald-700">
                      {animal.name}
                    </span>
                    <ChevronRight size={16} className="text-gray-300 group-hover:text-emerald-600" />
                  </Link>
                </li>
              ))}
            </ul>
          ) : (
            <div className="py-20 text-center text-xs text-gray-400">
              <p className="font-semibold text-gray-600 text-sm mb-1">&lsquo;{searchTerm}&rsquo;</p>
              해당하는 동물 이름이 없습니다.
            </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

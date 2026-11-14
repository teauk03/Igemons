"use client";

import { useEffect, useRef, useState } from "react";
import { Search, X, ChevronRight } from "lucide-react";
import Link from "next/link";
import { api } from "../lib/api";

interface SearchModalProps {
  isOpen: boolean;
  onClose: () => void;
}

interface AnimalSuggestion {
  id: number;
  name: string;
}

export default function SearchModal({ isOpen, onClose }: SearchModalProps) {
  if (!isOpen) return null;
  return <SearchModalContent onClose={onClose} />;
}

function SearchModalContent({ onClose }: Pick<SearchModalProps, "onClose">) {
  const [searchTerm, setSearchTerm] = useState("");
  const [suggestions, setSuggestions] = useState<AnimalSuggestion[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

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

  /* 300ms 디바운스로 API 검색 */
  useEffect(() => {
    const trimmed = searchTerm.trim();
    if (!trimmed) {
      setSuggestions([]);
      return;
    }
    setIsLoading(true);
    const timer = setTimeout(() => {
      api
        .get<AnimalSuggestion[]>(`/api/market/search?q=${encodeURIComponent(trimmed)}`)
        .then(setSuggestions)
        .catch(() => setSuggestions([]))
        .finally(() => setIsLoading(false));
    }, 300);
    return () => clearTimeout(timer);
  }, [searchTerm]);

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
        {/* 검색 입력창 */}
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

        {/* 검색 결과 */}
        {searchTerm.trim() !== "" && (
          <div className="max-h-[min(50vh,420px)] overflow-y-auto pt-2">
            {isLoading ? (
              <div className="py-10 text-center text-xs text-gray-400">검색 중...</div>
            ) : suggestions.length > 0 ? (
              <ul className="divide-y divide-gray-100">
                {suggestions.map((animal) => (
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
                <p className="font-semibold text-gray-600 text-sm mb-1">
                  &lsquo;{searchTerm}&rsquo;
                </p>
                해당하는 동물 이름이 없습니다.
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

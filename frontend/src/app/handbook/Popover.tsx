"use client";

import { X } from "lucide-react";

/** 팝오버를 띄울 기준 요소의 화면상 위치 (getBoundingClientRect 값) */
export interface Anchor {
  top: number;
  bottom: number;
}

export function anchorOf(e: React.MouseEvent<HTMLElement>): Anchor {
  const { top, bottom } = e.currentTarget.getBoundingClientRect();
  return { top, bottom };
}

const GAP = 8; // 기준 요소와의 간격
const MARGIN = 16; // 화면 가장자리 여백

/**
 * 클릭한 요소 바로 아래(공간이 부족하면 위)에 뜨는 창.
 * 가로는 앱 영역(최대 430px) 중앙, 크기는 화면을 넘지 않게 설정
 */
export default function Popover({
  anchor,
  title,
  onClose,
  children,
}: {
  anchor: Anchor;
  title: string;
  onClose: () => void;
  children: React.ReactNode;
}) {
  // 렌더 후 실제 높이를 재서 위치를 정한다 (측정 전에는 숨김)
  const place = (el: HTMLDivElement | null) => {
    if (!el) return;
    const h = el.offsetHeight;
    const vh = window.innerHeight;
    let top = anchor.bottom + GAP;
    if (top + h > vh - MARGIN) top = anchor.top - GAP - h; // 아래 공간 부족 → 위로
    top = Math.max(MARGIN, Math.min(top, vh - MARGIN - h)); // 화면 안으로 보정
    el.style.top = `${top}px`;
    el.style.visibility = "visible";
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/30" onClick={onClose}>
      <div
        ref={place}
        style={{ visibility: "hidden" }}
        className="absolute left-1/2 -translate-x-1/2 w-[calc(100%-2rem)] max-w-[398px] max-h-[calc(100dvh-2rem)] overflow-y-auto bg-white rounded-2xl shadow-xl p-4 flex flex-col gap-3"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex justify-between items-center">
          <h2 className="text-sm font-bold text-gray-900">{title}</h2>
          <button onClick={onClose} className="text-gray-400" aria-label="닫기">
            <X size={18} />
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

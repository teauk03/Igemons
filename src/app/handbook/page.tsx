import { Plus } from "lucide-react";

export default function HandbookPage() {
  return (
    <div className="p-4 flex flex-col gap-5">
      <header className="flex justify-between items-center">
        <h1 className="text-lg font-bold text-gray-900">건강수첩</h1>
      </header>

      {/* 개체 선택 탭 */}
      <div className="flex items-center gap-3 overflow-x-auto pb-1">
        {["망고", "감자", "자두"].map((pet, idx) => (
          <button
            key={pet}
            className={`flex flex-col items-center gap-1 min-w-[56px] ${
              idx === 0 ? "opacity-100 font-bold" : "opacity-60"
            }`}
          >
            <div className={`w-14 h-14 rounded-full border-2 flex items-center justify-center text-xl ${
              idx === 0 ? "border-emerald-500 bg-emerald-50" : "border-gray-200 bg-gray-100"
            }`}>
              🦎
            </div>
            <span className="text-xs text-gray-700">{pet}</span>
          </button>
        ))}
        <button className="flex flex-col items-center gap-1 min-w-[56px]">
          <div className="w-14 h-14 rounded-full border-2 border-dashed border-gray-300 flex items-center justify-center text-gray-400">
            <Plus size={20} />
          </div>
          <span className="text-xs text-gray-400">추가</span>
        </button>
      </div>

      {/* 급여 카드 */}
      <div className="bg-emerald-50/70 border border-emerald-100 rounded-2xl p-4 flex flex-col gap-3">
        <div className="flex justify-between items-center">
          <span className="text-xs font-semibold text-emerald-900">급여 기록</span>
          <span className="text-xs text-gray-500">마지막: 2026.10.09</span>
        </div>
        <div className="flex flex-col gap-1.5">
          <div className="bg-white p-2.5 rounded-xl text-xs flex justify-between">
            <span className="text-gray-700">밀웜</span>
            <span className="font-semibold text-emerald-700">3마리</span>
          </div>
          <div className="bg-white p-2.5 rounded-xl text-xs flex justify-between">
            <span className="text-gray-700">귀뚜라미</span>
            <span className="font-semibold text-emerald-700">2마리</span>
          </div>
        </div>
      </div>

      {/* 탈피 기록 카드 */}
      <div className="bg-emerald-50/70 border border-emerald-100 rounded-2xl p-4 flex flex-col gap-2">
        <span className="text-xs font-semibold text-emerald-900">탈피 주기</span>
        <div className="bg-white p-3 rounded-xl flex justify-between text-xs">
          <div>
            <p className="text-gray-400">마지막 탈피</p>
            <p className="font-semibold text-gray-800 mt-0.5">2026.09.10</p>
          </div>
          <div className="text-right">
            <p className="text-gray-400">다음 예상 (주기 30일)</p>
            <p className="font-semibold text-emerald-600 mt-0.5">2026.10.10</p>
          </div>
        </div>
      </div>
    </div>
  );
}
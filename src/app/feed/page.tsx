import { MessageSquare, Heart } from "lucide-react";

export default function FeedPage() {
  return (
    <div className="p-4 flex flex-col gap-4">
      <header className="flex justify-between items-center">
        <h1 className="text-lg font-bold text-gray-900">커뮤니티 피드</h1>
        <button className="text-xs bg-emerald-600 text-white font-semibold px-3 py-1.5 rounded-full">
          글쓰기
        </button>
      </header>

      <div className="flex gap-2 text-xs">
        <button className="px-3 py-1 rounded-full bg-emerald-50 text-emerald-700 font-semibold">전체</button>
        <button className="px-3 py-1 rounded-full bg-gray-100 text-gray-500">사육방식</button>
        <button className="px-3 py-1 rounded-full bg-gray-100 text-gray-500">QnA</button>
      </div>

      {/* 목업 포스트 */}
      <div className="flex flex-col gap-3">
        {[1, 2].map((i) => (
          <div key={i} className="p-4 bg-gray-50 border border-gray-100 rounded-2xl flex flex-col gap-2">
            <div className="flex items-center gap-2">
              <div className="w-7 h-7 rounded-full bg-gray-300" />
              <div>
                <p className="text-xs font-semibold text-gray-800">집사{i}</p>
                <p className="text-[10px] text-gray-400">10분 전</p>
              </div>
            </div>
            <p className="text-xs text-gray-700">
              우리 크레가 귀뚜라미를 안 먹는데 혹시 슈푸만 줘도 괜찮을까요? 경험 있으신 분 조언 부탁드립니다!
            </p>
            <div className="flex items-center gap-4 text-xs text-gray-400 pt-1">
              <span className="flex items-center gap-1"><Heart size={14} /> 12</span>
              <span className="flex items-center gap-1"><MessageSquare size={14} /> 5</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
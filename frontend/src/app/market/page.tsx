import Link from "next/link";

const MOCK_LISTINGS = [
  { id: 1, title: "크레스티드 게코 카푸치노", price: 250000, tag: "프리미엄", shop: "크레전문샵" },
  { id: 2, title: "레오파드 게코 텐저린", price: 35000, tag: "일반", shop: "렙타일하우스" },
  { id: 3, title: "헤르만 육지거북 베이비", price: 120000, tag: "인기", shop: "거북이마을" },
];

export default function MarketPage() {
  return (
    <div className="p-4 flex flex-col gap-4">
      <header className="flex justify-between items-center">
        <h1 className="text-lg font-bold text-gray-900">개체 분양</h1>
        <span className="text-xs text-gray-500">총 {MOCK_LISTINGS.length}마리</span>
      </header>

      {/* 탭 필터 */}
      <div className="flex gap-2 text-xs font-medium">
        <button className="px-3 py-1.5 rounded-full bg-emerald-600 text-white">전체</button>
        <button className="px-3 py-1.5 rounded-full bg-gray-100 text-gray-600 hover:bg-gray-200">제휴할인</button>
        <button className="px-3 py-1.5 rounded-full bg-gray-100 text-gray-600 hover:bg-gray-200">호텔링</button>
      </div>

      {/* 분양 리스트 */}
      <div className="flex flex-col gap-3">
        {MOCK_LISTINGS.map((item) => (
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
    </div>
  );
}
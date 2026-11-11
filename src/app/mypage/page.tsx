import { ShoppingBag, Bookmark, Clock, MessageSquareText, Settings, HelpCircle, Headphones } from "lucide-react";

export default function MyPage() {
  return (
    <div className="p-4 flex flex-col gap-5">
      {/* 유저 프로필 */}
      <div className="flex items-center gap-3 py-2">
        <div className="w-14 h-14 rounded-full bg-gray-200 flex items-center justify-center text-xl">
          👤
        </div>
        <div>
          <h2 className="text-sm font-bold text-gray-900">집사님</h2>
          <p className="text-xs text-gray-400">user@igemons.com</p>
        </div>
      </div>

      {/* 바로가기 아이콘 4개 */}
      <div className="grid grid-cols-4 gap-2 bg-gray-50 p-3 rounded-2xl border border-gray-100 text-center">
        {[
          { icon: ShoppingBag, label: "분양내역" },
          { icon: Bookmark, label: "찜목록" },
          { icon: Clock, label: "최근본생물" },
          { icon: MessageSquareText, label: "내활동" },
        ].map(({ icon: Icon, label }) => (
          <button key={label} className="flex flex-col items-center gap-1">
            <Icon size={18} className="text-gray-600" />
            <span className="text-[11px] text-gray-600">{label}</span>
          </button>
        ))}
      </div>

      {/* 설정 및 고객지원 메뉴 */}
      <div className="flex flex-col gap-1 text-xs text-gray-700">
        <div className="p-3 hover:bg-gray-50 rounded-xl cursor-pointer flex items-center gap-2">
          <Settings size={16} className="text-gray-400" /> 공지사항 및 이벤트
        </div>
        <div className="p-3 hover:bg-gray-50 rounded-xl cursor-pointer flex items-center gap-2">
          <HelpCircle size={16} className="text-gray-400" /> 자주 묻는 질문 (FAQ)
        </div>
        <div className="p-3 hover:bg-gray-50 rounded-xl cursor-pointer flex items-center gap-2">
          <Headphones size={16} className="text-gray-400" /> 고객센터
        </div>
      </div>
    </div>
  );
}
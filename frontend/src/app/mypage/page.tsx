"use client";

import { useRouter } from "next/navigation";
import Link from "next/link";
import {
  ShoppingBag,
  Bookmark,
  Clock,
  MessageSquareText,
  Settings,
  HelpCircle,
  Headphones,
  LogIn,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";

export default function MyPage() {
  const { user, isLoading, logout } = useAuth();
  const router = useRouter();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-40 text-sm text-gray-400">
        불러오는 중...
      </div>
    );
  }

  /* 로그인 전 */
  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-6 p-6 min-h-[70vh]">
        <div className="w-20 h-20 rounded-full bg-gray-100 flex items-center justify-center text-4xl">
          👤
        </div>
        <div className="text-center">
          <h2 className="text-base font-bold text-gray-800">로그인이 필요해요</h2>
          <p className="text-xs text-gray-400 mt-2 leading-relaxed">
            로그인하면 분양내역, 찜목록,<br />건강수첩 등을 이용할 수 있어요.
          </p>
        </div>
        <button
          onClick={() => router.push("/login")}
          className="flex items-center gap-2 px-8 py-3 bg-emerald-600 text-white text-sm font-semibold rounded-xl hover:bg-emerald-700 transition-colors"
        >
          <LogIn size={16} />
          로그인하기
        </button>
        <p className="text-xs text-gray-400">
          계정이 없으신가요?{" "}
          <Link href="/signup" className="text-emerald-600 font-medium">
            회원가입
          </Link>
        </p>
      </div>
    );
  }

  /* 로그인 후 */
  return (
    <div className="p-4 flex flex-col gap-5">
      {/* 유저 프로필 */}
      <div className="flex items-center justify-between py-2">
        <div className="flex items-center gap-3">
          <div className="w-14 h-14 rounded-full bg-gray-200 flex items-center justify-center text-xl">
            👤
          </div>
          <div>
            <h2 className="text-sm font-bold text-gray-900">{user.nickname}</h2>
            <p className="text-xs text-gray-400">{user.email}</p>
          </div>
        </div>
        <button
          onClick={logout}
          className="text-xs text-gray-400 hover:text-gray-600 transition-colors px-2 py-1"
        >
          로그아웃
        </button>
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

"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Home, Grid, HeartHandshake, Globe, BookOpenCheck, User } from "lucide-react";

const NAV_ITEMS = [
  { label: "홈", href: "/", icon: Home },
  { label: "카테고리", href: "/category", icon: Grid },
  { label: "분양", href: "/market", icon: HeartHandshake },
  { label: "피드", href: "/feed", icon: Globe },
  { label: "수첩", href: "/handbook", icon: BookOpenCheck },
  { label: "내정보", href: "/mypage", icon: User },
];

export default function BottomNav() {
  const pathname = usePathname();

  return (
    <nav className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-[430px] bg-white border-t border-gray-100 flex justify-around py-2.5 z-50">
      {NAV_ITEMS.map(({ label, href, icon: Icon }) => {
        const isActive = pathname === href;
        return (
          <Link
            key={href}
            href={href}
            className={`flex flex-col items-center text-[11px] font-medium transition-colors ${
              isActive ? "text-emerald-600" : "text-gray-400 hover:text-gray-600"
            }`}
          >
            <Icon size={20} className="mb-0.5" />
            <span>{label}</span>
          </Link>
        );
      })}
    </nav>
  );
}
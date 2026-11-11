import Link from "next/link";
import Image from "next/image";
import { Search, Bell, ShoppingCart } from "lucide-react";

export default function Header() {
  return (
    <header className="flex items-center justify-between py-2 px-1">
      <Link href="/" className="flex items-center">
            <Image
            src="/igemono_log.jpeg"
            alt="Igemons 로고"
            width={120}
            height={40}
            priority
            className="h-9 w-auto object-contain mix-blend-multiply"
            />
      </Link>

      {/* 우측 유틸리티 아이콘 */}
      <div className="flex items-center gap-3 text-gray-700">
        <Link href="/search" aria-label="검색">
          <Search size={22} className="cursor-pointer hover:text-emerald-600 transition-colors" />
        </Link>
        <Link href="/notifications" aria-label="알림">
          <Bell size={22} className="cursor-pointer hover:text-emerald-600 transition-colors" />
        </Link>
        <Link href="/cart" aria-label="장바구니">
          <ShoppingCart size={22} className="cursor-pointer hover:text-emerald-600 transition-colors" />
        </Link>
      </div>
    </header>
  );
}
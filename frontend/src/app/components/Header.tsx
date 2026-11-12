"use client";

import Link from "next/link";
import Image from "next/image";
import { Search, Bell, ShoppingCart } from "lucide-react";
import { useState } from "react";
import SearchModal from "./SearchModal";

export default function Header() {
  const [isSearchOpen, setIsSearchOpen] = useState(false);

  return (
    <>
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

        <div className="flex items-center gap-3 text-gray-700">
          <button
            type="button"
            onClick={() => setIsSearchOpen(true)}
            aria-label="검색"
            className="hover:text-emerald-600 transition-colors"
          >
            <Search size={22} />
          </button>
          <Link href="/notifications" aria-label="알림">
            <Bell size={22} className="cursor-pointer hover:text-emerald-600 transition-colors" />
          </Link>
          <Link href="/cart" aria-label="장바구니">
            <ShoppingCart size={22} className="cursor-pointer hover:text-emerald-600 transition-colors" />
          </Link>
        </div>
      </header>
      <SearchModal isOpen={isSearchOpen} onClose={() => setIsSearchOpen(false)} />
    </>
  );
}
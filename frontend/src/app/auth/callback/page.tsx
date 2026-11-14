"use client";

import { Suspense, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { api } from "../../lib/api";
import { User } from "../../context/AuthContext";

interface AuthResponse {
  token: string;
  user: User;
}

function KakaoCallbackContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  useEffect(() => {
    const code = searchParams.get("code");
    const error = searchParams.get("error");

    if (error || !code) {
      router.replace("/login");
      return;
    }

    const redirectUri = process.env.NEXT_PUBLIC_KAKAO_REDIRECT_URI!;

    api
      .post<AuthResponse>("/api/auth/kakao", { code, redirectUri })
      .then(({ token }) => {
        localStorage.setItem("token", token);
        // 전체 리로드로 AuthContext 재초기화
        window.location.href = "/mypage";
      })
      .catch(() => {
        router.replace("/login");
      });
  }, [router, searchParams]);

  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="flex flex-col items-center gap-3">
        <div className="w-8 h-8 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin" />
        <p className="text-sm text-gray-500">카카오 로그인 중...</p>
      </div>
    </div>
  );
}

export default function KakaoCallbackPage() {
  return (
    <Suspense
      fallback={
        <div className="flex items-center justify-center min-h-screen">
          <div className="w-8 h-8 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin" />
        </div>
      }
    >
      <KakaoCallbackContent />
    </Suspense>
  );
}

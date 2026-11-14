"use client";

import { useRouter } from "next/navigation";
import Image from "next/image";

export default function LoginPage() {
  const router = useRouter();

  const handleKakaoLogin = () => {
    const clientId = process.env.NEXT_PUBLIC_KAKAO_CLIENT_ID;
    const redirectUri = process.env.NEXT_PUBLIC_KAKAO_REDIRECT_URI;
    window.location.href =
      `https://kauth.kakao.com/oauth/authorize` +
      `?client_id=${clientId}` +
      `&redirect_uri=${encodeURIComponent(redirectUri!)}` +
      `&response_type=code`;
  };

  return (
    <div className="flex flex-col min-h-screen p-6">
      {/* 뒤로가기 */}
      <button
        onClick={() => router.back()}
        className="self-start text-xs text-gray-400 hover:text-gray-600 mb-8"
      >
        ← 뒤로
      </button>

      {/* 로고 */}
      <div className="flex flex-col items-center gap-2 mb-14">
        <Image
          src="/igemono_log.jpeg"
          alt="Igemons 로고"
          width={160}
          height={56}
          className="h-11 w-auto object-contain mix-blend-multiply"
        />
        <p className="text-xs text-gray-400">특수동물을 위한 All-In-One 플랫폼</p>
      </div>

      <div className="flex flex-col items-center gap-5">
        <div className="text-center">
          <h2 className="text-base font-bold text-gray-800">로그인</h2>
          <p className="text-xs text-gray-400 mt-2 leading-relaxed">
            카카오 계정으로 간편하게 시작하세요.
          </p>
        </div>

        {/* 카카오 로그인 버튼 */}
        <button
          onClick={handleKakaoLogin}
          className="w-full flex items-center justify-center gap-3 py-3.5 rounded-xl bg-[#FEE500] text-[#191919] text-sm font-semibold hover:bg-[#f0d900] transition-colors"
        >
          {/* 카카오 로고 */}
          <svg
            width="20"
            height="20"
            viewBox="0 0 20 20"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              fillRule="evenodd"
              clipRule="evenodd"
              d="M10 1.667C5.398 1.667 1.667 4.558 1.667 8.125c0 2.27 1.407 4.267 3.541 5.44l-.899 3.343c-.08.296.254.531.511.355l3.892-2.59A9.6 9.6 0 0010 14.583c4.602 0 8.333-2.891 8.333-6.458S14.602 1.667 10 1.667z"
              fill="#191919"
            />
          </svg>
          카카오로 계속하기
        </button>
      </div>
    </div>
  );
}

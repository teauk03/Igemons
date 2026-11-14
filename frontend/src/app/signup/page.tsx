"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { useAuth } from "../context/AuthContext";
import { ApiError } from "../lib/api";

export default function SignupPage() {
  const router = useRouter();
  const { register } = useAuth();
  const [email, setEmail] = useState("");
  const [nickname, setNickname] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirm, setPasswordConfirm] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    if (password !== passwordConfirm) {
      setError("비밀번호가 일치하지 않습니다.");
      return;
    }
    setLoading(true);
    try {
      await register(email, nickname, password);
      router.replace("/mypage");
    } catch (err: unknown) {
      if (err instanceof ApiError && err.status === 409) {
        setError("이미 가입된 회원 정보가 있습니다.");
      } else {
        setError("회원가입에 실패했습니다. 다시 시도해주세요.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col min-h-screen p-6">
      <button
        onClick={() => router.back()}
        className="self-start text-xs text-gray-400 hover:text-gray-600 mb-8"
      >
        ← 뒤로
      </button>

      <div className="flex flex-col items-center gap-2 mb-10">
        <Image
          src="/igemono_log.jpeg"
          alt="Igemons 로고"
          width={160}
          height={56}
          className="h-11 w-auto object-contain mix-blend-multiply"
        />
        <p className="text-xs text-gray-400">특수동물을 위한 All-In-One 플랫폼</p>
      </div>

      <form onSubmit={handleSubmit} className="flex flex-col gap-4 max-w-sm mx-auto w-full">
        <h2 className="text-base font-bold text-gray-800 text-center mb-2">회원가입</h2>

        {error && (
          <p className="text-xs text-red-500 text-center">{error}</p>
        )}

        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500">이메일</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            placeholder="example@email.com"
            className="border border-gray-200 rounded-xl px-4 py-3 text-sm outline-none focus:border-green-400"
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500">닉네임</label>
          <input
            type="text"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            required
            placeholder="닉네임"
            maxLength={50}
            className="border border-gray-200 rounded-xl px-4 py-3 text-sm outline-none focus:border-green-400"
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500">비밀번호</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            placeholder="비밀번호"
            className="border border-gray-200 rounded-xl px-4 py-3 text-sm outline-none focus:border-green-400"
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500">비밀번호 확인</label>
          <input
            type="password"
            value={passwordConfirm}
            onChange={(e) => setPasswordConfirm(e.target.value)}
            required
            placeholder="비밀번호 확인"
            className="border border-gray-200 rounded-xl px-4 py-3 text-sm outline-none focus:border-green-400"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="mt-2 py-3.5 rounded-xl bg-green-500 text-white text-sm font-semibold hover:bg-green-600 transition-colors disabled:opacity-60"
        >
          {loading ? "가입 중..." : "회원가입"}
        </button>

        <p className="text-xs text-center text-gray-400 mt-2">
          이미 계정이 있으신가요?{" "}
          <Link href="/login" className="text-green-500 font-medium">
            로그인
          </Link>
        </p>
      </form>
    </div>
  );
}

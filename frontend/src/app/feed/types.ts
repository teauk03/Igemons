export type PostCategory = "FREE" | "HUSBANDRY" | "QNA";

export interface Post {
  id: number;
  category: PostCategory;
  title: string;
  content: string;
  authorId: number;
  authorNickname: string | null;
  createdAt: string; // "2026-11-18T15:47:11.897"
  updatedAt: string;
}

export const CATEGORY_LABEL: Record<PostCategory, string> = {
  FREE: "자유",
  HUSBANDRY: "사육방식",
  QNA: "QnA",
};

export const CATEGORIES = Object.keys(CATEGORY_LABEL) as PostCategory[];

/** 백엔드와 맞춘 길이 제한 */
export const TITLE_MAX = 100;
export const CONTENT_MAX = 5000;

/** "2026-11-18T15:47:11.897" → "2026.11.18 15:47" (렌더 중 new Date()를 쓰지 않도록 문자열로 처리) */
export function formatDateTime(iso: string) {
  return `${iso.slice(0, 10).replaceAll("-", ".")} ${iso.slice(11, 16)}`;
}

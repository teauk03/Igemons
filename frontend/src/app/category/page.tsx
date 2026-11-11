import { ChevronRight } from "lucide-react";

const CATEGORIES = [
  { group: "게코", items: ["크레스티드 게코", "레오파드 게코", "가고일 게코", "차화 게코"] },
  { group: "파충류", items: ["뱀", "육지거북", "반수생거북", "카멜레온", "비어디드래곤"] },
  { group: "양서류", items: ["화이트트리프록", "팩맨", "다트프록", "우파루파", "도롱뇽"] },
];

export default function CategoryPage() {
  return (
    <div className="p-4 flex flex-col gap-5">
      <h1 className="text-lg font-bold text-gray-900">생물 카테고리</h1>
      {CATEGORIES.map((section) => (
        <div key={section.group} className="flex flex-col gap-2">
          <h2 className="text-sm font-semibold text-emerald-700">{section.group}</h2>
          <div className="bg-gray-50 rounded-xl divide-y divide-gray-100 border border-gray-100">
            {section.items.map((item) => (
              <div key={item} className="flex items-center justify-between p-3 cursor-pointer hover:bg-gray-100/60 transition">
                <span className="text-sm text-gray-700">{item}</span>
                <ChevronRight size={16} className="text-gray-400" />
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
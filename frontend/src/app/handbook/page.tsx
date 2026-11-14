"use client";

import { useEffect, useState } from "react";
import { Plus } from "lucide-react";
import { useRouter } from "next/navigation";
import { useAuth } from "../context/AuthContext";
import { api } from "../lib/api";

import EditLogSheet from "./EditLogSheet";
import Popover, { Anchor, anchorOf } from "./Popover";
import type { Pet, PetLog } from "./types";

type FormKind = "pet" | "feeding" | "shedding" | "weight" | "environment" | "note";

const FORM_TITLE: Record<FormKind, string> = {
  pet: "반려동물 등록",
  feeding: "급여 기록",
  shedding: "탈피 기록",
  weight: "체중 기록",
  environment: "사육 환경 기록",
  note: "메모",
};

const RECENT_FEEDINGS = 5;
const RECENT_WEIGHTS = 5;
const RECENT_ENVIRONMENTS = 3;
const RECENT_NOTES = 5;
const RECENT_SHEDDINGS = 5;

/** 눌러서 수정할 수 있는 기록 행 */
const rowButtonClass =
  "w-full text-left active:bg-emerald-100/60 hover:bg-emerald-100/40 transition-colors";

const fieldClass = "border border-gray-200 rounded-lg px-2.5 py-1.5 text-xs";
const saveButtonClass =
  "px-3 py-1.5 bg-emerald-600 text-white text-xs font-semibold rounded-lg disabled:opacity-50";
const DAY_MS = 24 * 60 * 60 * 1000;

/* ---------- 날짜 계산 ---------- */

/** 시각을 무시한 날짜 차이 (b - a, 일 단위) */
function daysBetween(a: Date, b: Date) {
  const ua = Date.UTC(a.getFullYear(), a.getMonth(), a.getDate());
  const ub = Date.UTC(b.getFullYear(), b.getMonth(), b.getDate());
  return Math.round((ub - ua) / DAY_MS);
}

function formatDate(d: Date) {
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, "0")}.${String(d.getDate()).padStart(2, "0")}`;
}

function formatShort(d: Date) {
  return `${d.getMonth() + 1}/${d.getDate()}`;
}

/** 오늘 날짜 "YYYY-MM-DD" (로컬 기준, <input type="date"> 기본값용) */
function todayInput() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

function relativeDay(d: Date) {
  const diff = daysBetween(d, new Date());
  if (diff <= 0) return "오늘";
  if (diff === 1) return "어제";
  return `${diff}일 전`;
}

/** 탈피 기록들의 평균 간격(일). 기록이 2개 미만이면 null */
function averageCycleDays(sheddings: Date[]) {
  if (sheddings.length < 2) return null;
  const asc = [...sheddings].sort((a, b) => a.getTime() - b.getTime());
  const gaps = asc.slice(1).map((d, i) => daysBetween(asc[i], d)).filter((g) => g > 0);
  if (gaps.length === 0) return null;
  return Math.round(gaps.reduce((s, g) => s + g, 0) / gaps.length);
}

/** 숫자 문자열 → number, 빈 값이면 null */
function toNumberOrNull(v: string) {
  return v.trim() === "" ? null : Number(v);
}

/** 52.30 → "52.3" */
function num(v: number | undefined) {
  return v == null ? "-" : String(Number(v));
}

/* ---------- 공통 카드 ---------- */

function Card({
  title,
  addLabel,
  onAdd,
  children,
}: {
  title: string;
  addLabel: string;
  onAdd: (e: React.MouseEvent<HTMLElement>) => void;
  children: React.ReactNode;
}) {
  return (
    <div className="bg-emerald-50/70 border border-emerald-100 rounded-2xl p-4 flex flex-col gap-2.5">
      <div className="flex justify-between items-center">
        <span className="text-xs font-semibold text-emerald-900">{title}</span>
        <button
          onClick={onAdd}
          className="flex items-center gap-0.5 text-xs font-semibold text-emerald-700"
        >
          <Plus size={14} /> {addLabel}
        </button>
      </div>
      {children}
    </div>
  );
}

function FormRow({ children }: { children: React.ReactNode }) {
  return <div className="flex gap-2 items-center">{children}</div>;
}

function Empty({ text }: { text: string }) {
  return <p className="text-xs text-gray-400">{text}</p>;
}

/* ---------- 화면 ---------- */

export default function HandbookPage() {
  const { user, isLoading: authLoading } = useAuth();
  const router = useRouter();

  const [pets, setPets] = useState<Pet[] | null>(null); // null = 불러오는 중
  const [petsVersion, setPetsVersion] = useState(0);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [logState, setLogState] = useState<{ petId: number; items: PetLog[] } | null>(null);
  const [logsVersion, setLogsVersion] = useState(0);
  const [error, setError] = useState<string | null>(null);

  // 등록 창 (누른 + 버튼 근처에 표시)
  const [openForm, setOpenForm] = useState<{ kind: FormKind; anchor: Anchor } | null>(null);
  const [feedForm, setFeedForm] = useState({ item: "", quantity: "1" });
  const [shedDate, setShedDate] = useState(todayInput);
  const [weightInput, setWeightInput] = useState("");
  const [envForm, setEnvForm] = useState({ temp: "", humidity: "" });
  const [noteInput, setNoteInput] = useState("");
  const [saving, setSaving] = useState(false);

  // 수정 중인 기록 (누른 기록 근처에 표시)
  const [editing, setEditing] = useState<{ log: PetLog; anchor: Anchor } | null>(null);
  const [editBusy, setEditBusy] = useState(false);

  const [newPet, setNewPet] = useState({ name: "", species: "" });
  const [adding, setAdding] = useState(false);

  // 반려동물 목록
  useEffect(() => {
    if (!user) return;
    api
      .get<Pet[]>("/api/pets")
      .then(setPets)
      .catch((e: Error) => setError(e.message));
  }, [user, petsVersion]);

  const selectedPet = pets?.find((p) => p.id === selectedId) ?? pets?.[0] ?? null;
  const selectedPetId = selectedPet?.id ?? null;

  // 선택된 반려동물의 기록
  useEffect(() => {
    if (selectedPetId == null) return;
    api
      .get<PetLog[]>(`/api/pets/${selectedPetId}/logs`)
      .then((items) => setLogState({ petId: selectedPetId, items }))
      .catch((e: Error) => setError(e.message));
  }, [selectedPetId, logsVersion]);

  const toggleForm = (form: FormKind, e: React.MouseEvent<HTMLElement>) => {
    switch (form) {
      case "pet":
        setNewPet({ name: "", species: "" });
        break;
      case "feeding": {
        // 마지막으로 준 먹이를 기본값으로
        const last = logState?.items.find((l) => l.type === "FEEDING");
        setFeedForm({ item: last?.feedItem ?? "", quantity: String(last?.quantity ?? 1) });
        break;
      }
      case "shedding":
        setShedDate(todayInput());
        break;
      case "weight":
        setWeightInput("");
        break;
      case "environment":
        setEnvForm({ temp: "", humidity: "" });
        break;
      case "note":
        setNoteInput("");
        break;
    }
    setOpenForm({ kind: form, anchor: anchorOf(e) });
  };

  const openEdit = (e: React.MouseEvent<HTMLElement>, log: PetLog) =>
    setEditing({ log, anchor: anchorOf(e) });

  const saveLog = async (body: Record<string, unknown>) => {
    if (selectedPetId == null) return;
    setSaving(true);
    try {
      await api.post(`/api/pets/${selectedPetId}/logs`, body);
      setOpenForm(null);
      setLogsVersion((v) => v + 1);
    } catch (e) {
      alert((e as Error).message);
    } finally {
      setSaving(false);
    }
  };

  const saveFeeding = () =>
    saveLog({ type: "FEEDING", feedItem: feedForm.item, quantity: Number(feedForm.quantity) });

  // 날짜만 고르므로 그날 00:00으로 기록 (오늘이어도 미래 시각이 되지 않음)
  const saveShedding = () => saveLog({ type: "SHEDDING", loggedAt: `${shedDate}T00:00:00` });

  const saveWeight = () => saveLog({ type: "WEIGHT", weightGrams: Number(weightInput) });

  // 온도·습도 중 하나만 입력해도 된다
  const saveEnvironment = () =>
    saveLog({
      type: "ENVIRONMENT",
      temperatureC: toNumberOrNull(envForm.temp),
      humidityPct: toNumberOrNull(envForm.humidity),
    });

  const saveNote = () => saveLog({ type: "NOTE", content: noteInput });

  /** 수정(PUT) 또는 삭제(DELETE) 후 시트를 닫고 목록 새로고침 */
  const runEdit = async (action: () => Promise<unknown>) => {
    setEditBusy(true);
    try {
      await action();
      setEditing(null);
      setLogsVersion((v) => v + 1);
    } catch (e) {
      alert((e as Error).message);
    } finally {
      setEditBusy(false);
    }
  };

  const logPath = (log: PetLog) => `/api/pets/${selectedPetId}/logs/${log.id}`;
  const updateLog = (body: Record<string, unknown>) =>
    editing && runEdit(() => api.put(logPath(editing.log), body));
  const deleteLog = () => editing && runEdit(() => api.delete(logPath(editing.log)));

  const addPet = async () => {
    setAdding(true);
    try {
      const created = await api.post<Pet>("/api/pets", newPet);
      setNewPet({ name: "", species: "" });
      setOpenForm(null);
      setSelectedId(created.id);
      setPetsVersion((v) => v + 1);
    } catch (e) {
      alert((e as Error).message);
    } finally {
      setAdding(false);
    }
  };

  if (authLoading || (user && pets === null && !error)) {
    return (
      <div className="flex items-center justify-center h-40 text-sm text-gray-400">
        불러오는 중...
      </div>
    );
  }

  /* 로그인 전 */
  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-5 p-6 min-h-[70vh]">
        <div className="text-5xl">📋</div>
        <div className="text-center">
          <h2 className="text-base font-bold text-gray-800">건강수첩</h2>
          <p className="text-xs text-gray-400 mt-2 leading-relaxed">
            로그인하면 내 생물의 급여·탈피 기록을<br />관리할 수 있어요.
          </p>
        </div>
        <button
          onClick={() => router.push("/login")}
          className="px-8 py-3 bg-emerald-600 text-white text-sm font-semibold rounded-xl hover:bg-emerald-700 transition-colors"
        >
          로그인하기
        </button>
      </div>
    );
  }

  if (error) {
    return (
      <div className="py-16 text-center text-sm text-red-400">
        데이터를 불러오지 못했습니다.
        <p className="text-xs text-gray-400 mt-1">{error}</p>
      </div>
    );
  }

  const petList = pets ?? [];
  const logs = selectedPet && logState?.petId === selectedPet.id ? logState.items : null;

  // 기록은 최신순으로 내려온다
  const feedings = logs?.filter((l) => l.type === "FEEDING") ?? [];
  const sheddingLogs = logs?.filter((l) => l.type === "SHEDDING") ?? [];
  const sheddings = sheddingLogs.map((l) => new Date(l.loggedAt));
  const lastFed = feedings[0] ? new Date(feedings[0].loggedAt) : null;
  const lastMolt = sheddings[0] ?? null;
  const cycleDays = averageCycleDays(sheddings);
  const nextMolt = lastMolt && cycleDays ? new Date(lastMolt.getTime() + cycleDays * DAY_MS) : null;

  const weights = logs?.filter((l) => l.type === "WEIGHT") ?? [];
  const latestWeight = weights[0]?.weightGrams;
  const prevWeight = weights[1]?.weightGrams;
  const weightDiff =
    latestWeight != null && prevWeight != null
      ? Math.round((Number(latestWeight) - Number(prevWeight)) * 10) / 10
      : null;

  const environments = logs?.filter((l) => l.type === "ENVIRONMENT") ?? [];
  const notes = logs?.filter((l) => l.type === "NOTE") ?? [];

  return (
    <div className="p-4 flex flex-col gap-5">
      <header className="flex justify-between items-center">
        <h1 className="text-lg font-bold text-gray-900">건강수첩</h1>
      </header>

      {/* 개체 선택 탭 */}
      <div className="flex items-center gap-3 overflow-x-auto pb-1">
        {petList.map((pet) => (
          <button
            key={pet.id}
            onClick={() => {
              setSelectedId(pet.id);
              setOpenForm(null);
              setEditing(null);
            }}
            className={`flex flex-col items-center gap-1 min-w-[56px] ${
              pet.id === selectedPet?.id ? "opacity-100 font-bold" : "opacity-60"
            }`}
          >
            <div
              className={`w-14 h-14 rounded-full border-2 flex items-center justify-center text-xl ${
                pet.id === selectedPet?.id
                  ? "border-emerald-500 bg-emerald-50"
                  : "border-gray-200 bg-gray-100"
              }`}
            >
              🦎
            </div>
            <span className="text-xs text-gray-700">{pet.name}</span>
          </button>
        ))}
        <button
          onClick={(ev) => toggleForm("pet", ev)}
          className="flex flex-col items-center gap-1 min-w-[56px]"
        >
          <div className="w-14 h-14 rounded-full border-2 border-dashed border-gray-300 flex items-center justify-center text-gray-400">
            <Plus size={20} />
          </div>
          <span className="text-xs text-gray-400">추가</span>
        </button>
      </div>


      {petList.length === 0 && (
        <div className="py-16 text-center text-sm text-gray-400">
          <p>등록된 생물이 없습니다.</p>
          <p className="text-xs mt-1">+ 추가 버튼으로 생물을 등록해보세요.</p>
        </div>
      )}

      {selectedPet && logs === null && (
        <div className="py-8 text-center text-xs text-gray-400">기록 불러오는 중...</div>
      )}

      {selectedPet && logs !== null && (
        <>
          {/* 급여 */}
          <Card title="급여 기록" addLabel="급여 기록" onAdd={(ev) => toggleForm("feeding", ev)}>
            <p className="text-xs text-gray-500 -mt-1">
              마지막: {lastFed ? `${formatDate(lastFed)} (${relativeDay(lastFed)})` : "-"}
            </p>
            {feedings.length === 0 ? (
              <Empty text="아직 급여 기록이 없어요." />
            ) : (
              <div className="flex flex-col gap-1.5">
                {feedings.slice(0, RECENT_FEEDINGS).map((f) => (
                  <button
                    key={f.id}
                    onClick={(ev) => openEdit(ev, f)}
                    className={`${rowButtonClass} bg-white p-2.5 rounded-xl text-xs flex justify-between`}
                  >
                    <span className="text-gray-700">
                      {f.feedItem}
                      <span className="text-gray-400 ml-1.5">{formatShort(new Date(f.loggedAt))}</span>
                    </span>
                    <span className="font-semibold text-emerald-700">× {f.quantity}</span>
                  </button>
                ))}
              </div>
            )}
          </Card>

          {/* 탈피 */}
          <Card title="탈피 주기" addLabel="탈피 기록" onAdd={(ev) => toggleForm("shedding", ev)}>
            <div className="bg-white p-3 rounded-xl flex justify-between text-xs">
              <div>
                <p className="text-gray-400">마지막 탈피</p>
                <p className="font-semibold text-gray-800 mt-0.5">
                  {lastMolt ? formatDate(lastMolt) : "기록 없음"}
                </p>
              </div>
              <div className="text-right">
                <p className="text-gray-400">
                  다음 예상{cycleDays ? ` (주기 ${cycleDays}일)` : ""}
                </p>
                <p className="font-semibold text-emerald-600 mt-0.5">
                  {nextMolt ? formatDate(nextMolt) : "탈피 기록 2회 이상 필요"}
                </p>
              </div>
            </div>
            {sheddingLogs.length > 0 && (
              <div className="flex flex-wrap gap-1.5">
                {sheddingLogs.slice(0, RECENT_SHEDDINGS).map((s) => (
                  <button
                    key={s.id}
                    onClick={(ev) => openEdit(ev, s)}
                    className="text-[11px] text-gray-600 bg-white border border-emerald-100 rounded-full px-2.5 py-1 hover:bg-emerald-100/40"
                  >
                    {formatDate(new Date(s.loggedAt))}
                  </button>
                ))}
              </div>
            )}
          </Card>

          {/* 체중 */}
          <Card title="체중" addLabel="체중 기록" onAdd={(ev) => toggleForm("weight", ev)}>
            {weights.length === 0 ? (
              <Empty text="아직 체중 기록이 없어요." />
            ) : (
              <>
                <button
                  onClick={(ev) => openEdit(ev, weights[0])}
                  className={`${rowButtonClass} bg-white p-3 rounded-xl flex justify-between items-end`}
                >
                  <div>
                    <p className="text-xs text-gray-400">현재 체중</p>
                    <p className="text-lg font-bold text-gray-800">
                      {num(latestWeight)}
                      <span className="text-xs font-normal text-gray-500 ml-0.5">g</span>
                    </p>
                  </div>
                  {weightDiff != null && (
                    <p
                      className={`text-xs font-semibold ${
                        weightDiff > 0 ? "text-emerald-600" : weightDiff < 0 ? "text-red-500" : "text-gray-400"
                      }`}
                    >
                      지난 기록 대비 {weightDiff > 0 ? "+" : ""}
                      {weightDiff}g
                    </p>
                  )}
                </button>
                <div className="flex flex-col">
                  {weights.slice(0, RECENT_WEIGHTS).map((w) => (
                    <button
                      key={w.id}
                      onClick={(ev) => openEdit(ev, w)}
                      className={`${rowButtonClass} flex justify-between text-xs px-1 py-1 rounded-md`}
                    >
                      <span className="text-gray-400">{formatDate(new Date(w.loggedAt))}</span>
                      <span className="text-gray-700">{num(w.weightGrams)} g</span>
                    </button>
                  ))}
                </div>
              </>
            )}
          </Card>

          {/* 사육 환경 */}
          <Card title="사육 환경" addLabel="환경 기록" onAdd={(ev) => toggleForm("environment", ev)}>
            {environments.length === 0 ? (
              <Empty text="아직 환경 기록이 없어요." />
            ) : (
              <>
                <button
                  onClick={(ev) => openEdit(ev, environments[0])}
                  className={`${rowButtonClass} bg-white p-3 rounded-xl grid grid-cols-2 text-xs`}
                >
                  <div>
                    <p className="text-gray-400">온도</p>
                    <p className="text-lg font-bold text-gray-800">
                      {num(environments[0].temperatureC)}
                      <span className="text-xs font-normal text-gray-500 ml-0.5">℃</span>
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-gray-400">습도</p>
                    <p className="text-lg font-bold text-gray-800">
                      {num(environments[0].humidityPct)}
                      <span className="text-xs font-normal text-gray-500 ml-0.5">%</span>
                    </p>
                  </div>
                  <p className="col-span-2 text-[11px] text-gray-400 mt-1">
                    측정: {formatDate(new Date(environments[0].loggedAt))} (
                    {relativeDay(new Date(environments[0].loggedAt))})
                  </p>
                </button>
                {environments.length > 1 && (
                  <div className="flex flex-col">
                    {environments.slice(1, RECENT_ENVIRONMENTS + 1).map((e) => (
                      <button
                        key={e.id}
                        onClick={(ev) => openEdit(ev, e)}
                        className={`${rowButtonClass} flex justify-between text-xs px-1 py-1 rounded-md`}
                      >
                        <span className="text-gray-400">{formatDate(new Date(e.loggedAt))}</span>
                        <span className="text-gray-700">
                          {num(e.temperatureC)}℃ / {num(e.humidityPct)}%
                        </span>
                      </button>
                    ))}
                  </div>
                )}
              </>
            )}
          </Card>

          {/* 메모 */}
          <Card title="메모" addLabel="메모" onAdd={(ev) => toggleForm("note", ev)}>
            {notes.length === 0 ? (
              <Empty text="아직 메모가 없어요." />
            ) : (
              <div className="flex flex-col gap-1.5">
                {notes.slice(0, RECENT_NOTES).map((n) => (
                  <button
                    key={n.id}
                    onClick={(ev) => openEdit(ev, n)}
                    className={`${rowButtonClass} bg-white p-2.5 rounded-xl text-xs`}
                  >
                    <p className="text-gray-700 whitespace-pre-wrap break-words">{n.content}</p>
                    <p className="text-[11px] text-gray-400 mt-1">
                      {formatDate(new Date(n.loggedAt))} ({relativeDay(new Date(n.loggedAt))})
                    </p>
                  </button>
                ))}
              </div>
            )}
          </Card>

          <p className="text-[11px] text-gray-400 text-center">기록을 누르면 수정하거나 삭제할 수 있어요.</p>
        </>
      )}

      {/* 등록 창 */}
      {openForm && (
        <Popover
          anchor={openForm.anchor}
          title={
            openForm.kind === "pet" || !selectedPet
              ? FORM_TITLE[openForm.kind]
              : `${selectedPet.name} · ${FORM_TITLE[openForm.kind]}`
          }
          onClose={() => setOpenForm(null)}
        >
          {openForm.kind === "pet" && (
            <>
              <input
                className={`${fieldClass} w-full`}
                placeholder="이름 (예: 모찌)"
                value={newPet.name}
                onChange={(e) => setNewPet({ ...newPet, name: e.target.value })}
              />
              <input
                className={`${fieldClass} w-full`}
                placeholder="종 (예: 레오파드 게코)"
                value={newPet.species}
                onChange={(e) => setNewPet({ ...newPet, species: e.target.value })}
              />
              <button
                onClick={addPet}
                disabled={adding}
                className="w-full py-2.5 bg-emerald-600 text-white text-sm font-semibold rounded-lg disabled:opacity-50"
              >
                {adding ? "등록 중..." : "등록"}
              </button>
            </>
          )}

          {openForm.kind === "feeding" && (
            <FormRow>
              <input
                className={`${fieldClass} flex-1 min-w-0`}
                placeholder="먹이 (예: 귀뚜라미)"
                value={feedForm.item}
                onChange={(e) => setFeedForm({ ...feedForm, item: e.target.value })}
              />
              <input
                className={`${fieldClass} w-16`}
                type="number"
                min={1}
                value={feedForm.quantity}
                onChange={(e) => setFeedForm({ ...feedForm, quantity: e.target.value })}
              />
              <button onClick={saveFeeding} disabled={saving} className={saveButtonClass}>
                저장
              </button>
            </FormRow>
          )}

          {openForm.kind === "shedding" && (
            <FormRow>
              <input
                className={`${fieldClass} flex-1 min-w-0`}
                type="date"
                max={todayInput()}
                value={shedDate}
                onChange={(e) => setShedDate(e.target.value)}
              />
              <button onClick={saveShedding} disabled={saving || !shedDate} className={saveButtonClass}>
                저장
              </button>
            </FormRow>
          )}

          {openForm.kind === "weight" && (
            <FormRow>
              <input
                className={`${fieldClass} flex-1 min-w-0`}
                type="number"
                step="0.1"
                min={0}
                placeholder="체중 (g)"
                value={weightInput}
                onChange={(e) => setWeightInput(e.target.value)}
              />
              <span className="text-xs text-gray-400">g</span>
              <button
                onClick={saveWeight}
                disabled={saving || weightInput.trim() === ""}
                className={saveButtonClass}
              >
                저장
              </button>
            </FormRow>
          )}

          {openForm.kind === "environment" && (
            <FormRow>
              <input
                className={`${fieldClass} flex-1 min-w-0`}
                type="number"
                step="0.1"
                placeholder="온도 ℃"
                value={envForm.temp}
                onChange={(e) => setEnvForm({ ...envForm, temp: e.target.value })}
              />
              <input
                className={`${fieldClass} flex-1 min-w-0`}
                type="number"
                min={0}
                max={100}
                placeholder="습도 %"
                value={envForm.humidity}
                onChange={(e) => setEnvForm({ ...envForm, humidity: e.target.value })}
              />
              <button
                onClick={saveEnvironment}
                disabled={saving || (envForm.temp.trim() === "" && envForm.humidity.trim() === "")}
                className={saveButtonClass}
              >
                저장
              </button>
            </FormRow>
          )}

          {openForm.kind === "note" && (
            <>
              <textarea
                className={`${fieldClass} w-full resize-none`}
                rows={4}
                maxLength={500}
                placeholder="특이사항을 적어주세요 (예: 눈 주변 탈피 잔여물 있음)"
                value={noteInput}
                onChange={(e) => setNoteInput(e.target.value)}
              />
              <div className="flex justify-between items-center">
                <span className="text-[11px] text-gray-400">{noteInput.length}/500</span>
                <button
                  onClick={saveNote}
                  disabled={saving || noteInput.trim() === ""}
                  className={saveButtonClass}
                >
                  저장
                </button>
              </div>
            </>
          )}
        </Popover>
      )}

      {editing && (
        <EditLogSheet
          key={editing.log.id}
          log={editing.log}
          anchor={editing.anchor}
          busy={editBusy}
          onSave={updateLog}
          onDelete={deleteLog}
          onClose={() => setEditing(null)}
        />
      )}
    </div>
  );
}

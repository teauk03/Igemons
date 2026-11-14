"use client";

import { useState } from "react";
import Popover, { Anchor } from "./Popover";
import { LOG_TYPE_LABEL, PetLog } from "./types";

const fieldClass = "w-full min-w-0 border border-gray-200 rounded-lg px-3 py-2 text-sm";
const labelClass = "text-[11px] text-gray-500 flex flex-col gap-1";

/** "2026-11-14T09:00:00.123" → "2026-11-14T09:00" (datetime-local 입력값) */
const toInput = (loggedAt: string) => loggedAt.slice(0, 16);

function nowInput() {
  const d = new Date();
  const p = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}`;
}

const str = (v: number | undefined) => (v == null ? "" : String(Number(v)));
const numOrNull = (v: string) => (v.trim() === "" ? null : Number(v));

/**
 * 기록 수정/삭제 창 (누른 기록 근처에 표시)
 * 부모에서 key={log.id}로 렌더링해 기록이 바뀌면 입력값이 새로 초기화 되도록 설정
 */
export default function EditLogSheet({
  log,
  anchor,
  busy,
  onSave,
  onDelete,
  onClose,
}: {
  log: PetLog;
  anchor: Anchor;
  busy: boolean;
  onSave: (body: Record<string, unknown>) => void;
  onDelete: () => void;
  onClose: () => void;
}) {
  const [loggedAt, setLoggedAt] = useState(toInput(log.loggedAt));
  const [memo, setMemo] = useState(log.memo ?? "");
  const [feedItem, setFeedItem] = useState(log.feedItem ?? "");
  const [quantity, setQuantity] = useState(str(log.quantity));
  const [weight, setWeight] = useState(str(log.weightGrams));
  const [temp, setTemp] = useState(str(log.temperatureC));
  const [humidity, setHumidity] = useState(str(log.humidityPct));
  const [content, setContent] = useState(log.content ?? "");

  // PUT은 전체 교체이므로 type별 값 + 시각 + 메모를 모두 보냄
  const save = () =>
    onSave({
      type: log.type,
      loggedAt: loggedAt ? `${loggedAt}:00` : null,
      memo,
      feedItem,
      quantity: numOrNull(quantity),
      weightGrams: numOrNull(weight),
      temperatureC: numOrNull(temp),
      humidityPct: numOrNull(humidity),
      content,
    });

  const remove = () => {
    if (confirm(`이 ${LOG_TYPE_LABEL[log.type]} 기록을 삭제할까요?`)) onDelete();
  };

  return (
    <Popover anchor={anchor} title={`${LOG_TYPE_LABEL[log.type]} 기록 수정`} onClose={onClose}>

        {log.type === "FEEDING" && (
          <div className="grid grid-cols-[1fr_80px] gap-2">
            <label className={labelClass}>
              먹이
              <input className={fieldClass} value={feedItem} onChange={(e) => setFeedItem(e.target.value)} />
            </label>
            <label className={labelClass}>
              수량
              <input
                className={fieldClass}
                type="number"
                min={1}
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
              />
            </label>
          </div>
        )}

        {log.type === "WEIGHT" && (
          <label className={labelClass}>
            체중 (g)
            <input
              className={fieldClass}
              type="number"
              step="0.1"
              min={0}
              value={weight}
              onChange={(e) => setWeight(e.target.value)}
            />
          </label>
        )}

        {log.type === "ENVIRONMENT" && (
          <div className="grid grid-cols-2 gap-2">
            <label className={labelClass}>
              온도 (℃)
              <input
                className={fieldClass}
                type="number"
                step="0.1"
                value={temp}
                onChange={(e) => setTemp(e.target.value)}
              />
            </label>
            <label className={labelClass}>
              습도 (%)
              <input
                className={fieldClass}
                type="number"
                min={0}
                max={100}
                value={humidity}
                onChange={(e) => setHumidity(e.target.value)}
              />
            </label>
          </div>
        )}

        {log.type === "NOTE" && (
          <label className={labelClass}>
            내용
            <textarea
              className={`${fieldClass} resize-none`}
              rows={4}
              maxLength={500}
              value={content}
              onChange={(e) => setContent(e.target.value)}
            />
          </label>
        )}

        <label className={labelClass}>
          {log.type === "SHEDDING" ? "탈피 일시" : "기록 일시"}
          <input
            className={fieldClass}
            type="datetime-local"
            max={nowInput()}
            value={loggedAt}
            onChange={(e) => setLoggedAt(e.target.value)}
          />
        </label>

        {log.type !== "NOTE" && (
          <label className={labelClass}>
            메모 (선택)
            <input
              className={fieldClass}
              maxLength={1000}
              value={memo}
              onChange={(e) => setMemo(e.target.value)}
            />
          </label>
        )}

        <div className="flex gap-2 mt-1">
          <button
            onClick={remove}
            disabled={busy}
            className="px-4 py-2.5 text-sm font-semibold text-red-500 border border-red-200 rounded-xl disabled:opacity-50"
          >
            삭제
          </button>
          <button
            onClick={save}
            disabled={busy}
            className="flex-1 py-2.5 text-sm font-semibold text-white bg-emerald-600 rounded-xl disabled:opacity-50"
          >
            {busy ? "처리 중..." : "저장"}
          </button>
        </div>
    </Popover>
  );
}

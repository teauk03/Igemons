/* 백엔드 응답 타입: GET /api/pets, GET /api/pets/{id}/logs */

export interface Pet {
  id: number;
  name: string;
  species: string;
}

export type LogType = "FEEDING" | "SHEDDING" | "WEIGHT" | "ENVIRONMENT" | "NOTE";

export interface PetLog {
  id: number;
  type: LogType;
  loggedAt: string; // "2026-11-14T09:00:00" (로컬 시각)
  memo?: string;
  feedItem?: string;
  quantity?: number;
  weightGrams?: number;
  temperatureC?: number;
  humidityPct?: number;
  content?: string;
}

export const LOG_TYPE_LABEL: Record<LogType, string> = {
  FEEDING: "급여",
  SHEDDING: "탈피",
  WEIGHT: "체중",
  ENVIRONMENT: "사육 환경",
  NOTE: "메모",
};

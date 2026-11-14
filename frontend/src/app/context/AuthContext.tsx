"use client";

import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import { api } from "../lib/api";

export interface User {
  id: number;
  nickname: string;
  email: string | null;
}

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, nickname: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      setIsLoading(false);
      return;
    }
    api
      .get<User>("/api/auth/me")
      .then(setUser)
      .catch(() => localStorage.removeItem("token"))
      .finally(() => setIsLoading(false));
  }, []);

  const login = async (email: string, password: string) => {
    const res = await api.post<{ token: string; user: User }>("/api/auth/login", { email, password });
    localStorage.setItem("token", res.token);
    setUser(res.user);
  };

  const register = async (email: string, nickname: string, password: string) => {
    const res = await api.post<{ token: string; user: User }>("/api/auth/register", { email, nickname, password });
    localStorage.setItem("token", res.token);
    setUser(res.user);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}

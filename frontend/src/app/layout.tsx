import type { Metadata } from "next";
import "./globals.css";
import Footer from "./components/Footer";
import { AuthProvider } from "./context/AuthContext";

export const metadata: Metadata = {
  title: "이게모노 (Igemons)",
  description: "특수동물을 위한 All-In-One 플랫폼",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body className="flex justify-center min-h-screen bg-gray-200">
        <AuthProvider>
          <div className="w-full max-w-[430px] min-h-screen bg-white flex flex-col shadow-2xl relative">
            <main className="flex-1 pb-20 overflow-y-auto">
              {children}
            </main>
            <Footer />
          </div>
        </AuthProvider>
      </body>
    </html>
  );
}

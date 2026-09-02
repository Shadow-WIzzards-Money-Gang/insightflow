import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { Toaster } from "react-hot-toast";

import Header from "@/components/layout/Header";
import Footer from "@/components/layout/Footer";
import { FiltrosProvider } from "@/context/FiltrosProvider";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata = {
  title: "Insight Flow",
  description: "Análises de reuniões e risco de churn",
};

export default function RootLayout({ children }) {
  return (
    <html
      lang="pt-BR"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col bg-body-bg">
        <FiltrosProvider>
          <Header />
          <main className="flex-1">
            {children}
            <Toaster position="top-center" />
          </main>
          <Footer />
        </FiltrosProvider>
      </body>
    </html>
  );
}
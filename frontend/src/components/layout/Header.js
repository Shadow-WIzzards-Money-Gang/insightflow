"use client";

import NovaAnaliseButton from "@/components/layout/NovaAnaliseButton";
import FiltroButton from "@/components/layout/FiltroButton";

export default function Header() {
    return (
        <header className="sticky top-0 z-40 flex flex-row items-center justify-between bg-header-bg px-6 py-3 shadow-lg shadow-black/20">
            <h1 className="text-2xl font-extrabold text-primary-text uppercase tracking-wider">
                Insight Flow
            </h1>
            <div className="flex flex-row items-center gap-2">
                <NovaAnaliseButton />
                <FiltroButton />
            </div>
        </header>
    );
}

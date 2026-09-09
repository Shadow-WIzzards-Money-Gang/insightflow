"use client";

import NovaAnaliseButton from "@/components/layout/NovaAnaliseButton";
import FiltroButton from "@/components/layout/FiltroButton";

export default function Header() {
    return (
        <header className="sticky top-0 z-40 flex flex-row flex-wrap items-center justify-between gap-x-4 gap-y-2 bg-header-bg px-4 py-3 shadow-lg shadow-black/20 sm:px-6">
            <h1 className="text-lg font-extrabold text-primary-text uppercase tracking-wider sm:text-2xl">
                Insight Flow
            </h1>
            <div className="flex flex-row items-center gap-2">
                <NovaAnaliseButton />
                <FiltroButton />
            </div>
        </header>
    );
}

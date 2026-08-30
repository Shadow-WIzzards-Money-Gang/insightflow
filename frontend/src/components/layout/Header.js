"use client";

import Button from "@/components/ui/Button";

export default function Header() {
    return (
        <header className="flex flex-row items-center justify-between bg-header-bg px-6 py-3">
            <h1 className="text-2xl font-extrabold text-primary-text uppercase tracking-wider">
                Insight Flow
            </h1>
            <div className="flex flex-row items-center gap-2">
                <Button label="Nova Análise" handleClick={() => {
                    
                }} />
            </div>
        </header>
    );
}
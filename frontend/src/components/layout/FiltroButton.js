"use client";

import { useState } from "react";

import Button from "@/components/ui/Button";
import FiltroModal from "@/components/layout/FiltroModal";
import { useFiltros } from "@/context/FiltrosProvider";

export default function FiltroButton() {
    const [aberto, setAberto] = useState(false);
    const { totalFiltrosAtivos } = useFiltros();

    return (
        <>
            <div className="relative">
                <Button label="Filtrar" handleClick={() => setAberto(true)} />
                {totalFiltrosAtivos > 0 && (
                    <span
                        className={`
                            absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center
                            rounded-full bg-secondary-bg-color px-1 text-xs font-bold text-secondary-text
                        `}
                        aria-label={`${totalFiltrosAtivos} filtros ativos`}
                    >
                        {totalFiltrosAtivos}
                    </span>
                )}
            </div>

            {aberto && <FiltroModal onClose={() => setAberto(false)} />}
        </>
    );
}

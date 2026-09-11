"use client";

import { useState } from "react";

// Indicador visual de que um valor foi calculado com filtros aplicados.
// Não mostra os filtros em si (podem ser muitos) — só sinaliza e, quando
// disponível, mostra o valor sem filtro para comparação.
export default function FiltroBadge({ valorSemFiltro }) {
    const [aberto, setAberto] = useState(false);

    return (
        <div
            className="absolute top-2 right-2 z-10"
            onMouseEnter={() => setAberto(true)}
            onMouseLeave={() => setAberto(false)}
        >
            <button
                type="button"
                onClick={(e) => {
                    e.stopPropagation();
                    setAberto((v) => !v);
                }}
                onBlur={() => setAberto(false)}
                aria-label="Este valor considera os filtros aplicados"
                className="flex h-5 w-5 items-center justify-center rounded-full bg-gray-100/15 text-gray-200 transition-opacity hover:opacity-80"
            >
                <svg viewBox="0 0 20 20" fill="currentColor" className="h-3 w-3">
                    <path d="M2 3.5A.5.5 0 0 1 2.5 3h15a.5.5 0 0 1 .4.8l-5.4 7.2v4.2a.5.5 0 0 1-.75.43l-3-1.7a.5.5 0 0 1-.25-.43v-2.5L2.1 3.8A.5.5 0 0 1 2 3.5z" />
                </svg>
            </button>

            {aberto && (
                <div
                    role="tooltip"
                    className="absolute right-0 top-7 w-44 rounded-lg border-2 border-secondary-bg-color bg-primary-bg-card-color p-2.5 text-left text-xs font-normal text-secondary-text shadow-lg shadow-black/20"
                >
                    <p>Calculado com filtros aplicados.</p>
                    {valorSemFiltro != null && (
                        <p className="mt-1">
                            Sem filtro:{" "}
                            <span className="font-bold text-primary-text">
                                {valorSemFiltro}
                            </span>
                        </p>
                    )}
                </div>
            )}
        </div>
    );
}

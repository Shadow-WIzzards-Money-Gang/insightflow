"use client";

import { createContext, useCallback, useContext, useMemo, useState } from "react";

const FILTROS_VAZIOS = {
    produtos: [],
    segmentos: [],
    riscos: [],
    sentimentos: [],
};

const FiltrosContext = createContext(null);

export function FiltrosProvider({ children }) {
    const [filtros, setFiltros] = useState(FILTROS_VAZIOS);

    const aplicarFiltros = useCallback((novos) => {
        setFiltros({
            produtos: novos.produtos ?? [],
            segmentos: novos.segmentos ?? [],
            riscos: novos.riscos ?? [],
            sentimentos: novos.sentimentos ?? [],
        });
    }, []);

    const limparFiltros = useCallback(() => {
        setFiltros(FILTROS_VAZIOS);
    }, []);

    const totalFiltrosAtivos =
        filtros.produtos.length +
        filtros.segmentos.length +
        filtros.riscos.length +
        filtros.sentimentos.length;

    const value = useMemo(
        () => ({ filtros, aplicarFiltros, limparFiltros, totalFiltrosAtivos }),
        [filtros, aplicarFiltros, limparFiltros, totalFiltrosAtivos]
    );

    return <FiltrosContext.Provider value={value}>{children}</FiltrosContext.Provider>;
}

export function useFiltros() {
    const ctx = useContext(FiltrosContext);
    if (!ctx) {
        throw new Error("useFiltros deve ser usado dentro de <FiltrosProvider>.");
    }
    return ctx;
}

export { FILTROS_VAZIOS };

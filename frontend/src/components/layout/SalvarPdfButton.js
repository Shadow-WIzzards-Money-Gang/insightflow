"use client";

import { useCallback, useState } from "react";
import toast from "react-hot-toast";

import Button from "@/components/ui/Button";
import GraficosExport from "@/components/layout/GraficosExport";
import { gerarRelatorioPdf } from "@/lib/relatorio/gerarRelatorioPdf";

export default function SalvarPdfButton({ metricas, filtros }) {
    const [gerando, setGerando] = useState(false);

    const semDados = metricas == null || (metricas.totalReunioes ?? 0) === 0;
    const desabilitado = gerando || semDados;

    const iniciar = () => {
        if (!desabilitado) setGerando(true);
    };

    const aoPronto = useCallback(
        async (imagens) => {
            try {
                await gerarRelatorioPdf({ metricas, filtros, imagens });
                toast.success("PDF gerado.");
            } catch (e) {
                toast.error(e?.message ?? "Não foi possível gerar o PDF.");
            } finally {
                setGerando(false);
            }
        },
        [metricas, filtros]
    );

    return (
        <>
            <Button
                label={gerando ? "Gerando PDF..." : "Salvar PDF"}
                handleClick={iniciar}
                disabled={desabilitado}
            />
            {gerando && <GraficosExport metricas={metricas} onPronto={aoPronto} />}
        </>
    );
}

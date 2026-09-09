"use client";

import { useCallback, useState } from "react";
import toast from "react-hot-toast";

import Button from "@/components/ui/Button";
import GraficosExport from "@/components/layout/GraficosExport";
import SalvarPdfModal from "@/components/layout/SalvarPdfModal";
import { getAnalisesReuniaoTodas } from "@/services/api";
import { gerarRelatorioPdf } from "@/lib/relatorio/gerarRelatorioPdf";

export default function SalvarPdfButton({ metricas, filtros }) {
    const [modalAberto, setModalAberto] = useState(false);
    const [gerando, setGerando] = useState(false);
    const [pendente, setPendente] = useState(null);

    const semDados = metricas == null || (metricas.totalReunioes ?? 0) === 0;
    const desabilitado = gerando || semDados;

    const confirmar = async ({ graficosIds, incluirAnalises }) => {
        setModalAberto(false);
        setGerando(true);

        try {
            const analises = incluirAnalises
                ? await getAnalisesReuniaoTodas(filtros)
                : null;
            setPendente({ graficosIds, analises });
        } catch (e) {
            toast.error(e?.message ?? "Não foi possível carregar as análises.");
            setGerando(false);
        }
    };

    const aoPronto = useCallback(
        async (imagens) => {
            try {
                await gerarRelatorioPdf({
                    metricas,
                    filtros,
                    imagens,
                    graficosIds: pendente?.graficosIds ?? [],
                    analises: pendente?.analises ?? null,
                });
                toast.success("PDF gerado.");
            } catch (e) {
                toast.error(e?.message ?? "Não foi possível gerar o PDF.");
            } finally {
                setGerando(false);
                setPendente(null);
            }
        },
        [metricas, filtros, pendente]
    );

    return (
        <>
            <Button
                label={gerando ? "Gerando PDF..." : "Salvar PDF"}
                handleClick={() => !desabilitado && setModalAberto(true)}
                disabled={desabilitado}
            />

            {modalAberto && (
                <SalvarPdfModal
                    metricas={metricas}
                    onClose={() => setModalAberto(false)}
                    onConfirmar={confirmar}
                />
            )}

            {pendente && (
                <GraficosExport
                    metricas={metricas}
                    ids={pendente.graficosIds}
                    onPronto={aoPronto}
                />
            )}
        </>
    );
}

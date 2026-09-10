"use client";

import { useCallback, useState } from "react";
import toast from "react-hot-toast";

import Button from "@/components/ui/Button";
import GraficosExport from "@/components/layout/GraficosExport";
import ExportarModal from "@/components/layout/ExportarModal";
import SalvarPdfModal from "@/components/layout/SalvarPdfModal";
import { exportarAnalisesCsv, getAnalisesReuniaoTodas } from "@/services/api";
import { gerarRelatorioPdf } from "@/lib/relatorio/gerarRelatorioPdf";

export default function ExportarButton({ metricas, filtros }) {
    const [escolhaAberta, setEscolhaAberta] = useState(false);
    const [pdfConfigAberto, setPdfConfigAberto] = useState(false);
    const [baixandoCsv, setBaixandoCsv] = useState(false);
    const [gerandoPdf, setGerandoPdf] = useState(false);
    const [pendentePdf, setPendentePdf] = useState(null);

    const semDados = metricas == null || (metricas.totalReunioes ?? 0) === 0;
    const ocupado = baixandoCsv || gerandoPdf;
    const desabilitado = semDados || ocupado;

    // --- CSV ---
    const baixarCsv = async () => {
        setBaixandoCsv(true);
        try {
            const { blob, filename } = await exportarAnalisesCsv(filtros);
            const url = URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(url);
            toast.success("CSV exportado.");
            setEscolhaAberta(false);
        } catch (e) {
            toast.error(e?.message ?? "Não foi possível exportar o CSV.");
        } finally {
            setBaixandoCsv(false);
        }
    };

    const escolher = (formato) => {
        if (formato === "pdf") {
            setEscolhaAberta(false);
            setPdfConfigAberto(true);
        } else if (formato === "csv") {
            baixarCsv();
        }
    };

    // --- PDF ---
    const confirmarPdf = async ({ graficosIds, incluirAnalises }) => {
        setPdfConfigAberto(false);
        setGerandoPdf(true);
        try {
            const analises = incluirAnalises
                ? await getAnalisesReuniaoTodas(filtros)
                : null;
            setPendentePdf({ graficosIds, analises });
        } catch (e) {
            toast.error(e?.message ?? "Não foi possível carregar as análises.");
            setGerandoPdf(false);
        }
    };

    const aoPronto = useCallback(
        async (imagens) => {
            try {
                await gerarRelatorioPdf({
                    metricas,
                    filtros,
                    imagens,
                    graficosIds: pendentePdf?.graficosIds ?? [],
                    analises: pendentePdf?.analises ?? null,
                });
                toast.success("PDF gerado.");
            } catch (e) {
                toast.error(e?.message ?? "Não foi possível gerar o PDF.");
            } finally {
                setGerandoPdf(false);
                setPendentePdf(null);
            }
        },
        [metricas, filtros, pendentePdf]
    );

    return (
        <>
            <Button
                label={gerandoPdf ? "Gerando PDF..." : "Exportar"}
                handleClick={() => !desabilitado && setEscolhaAberta(true)}
                disabled={desabilitado}
            />

            {escolhaAberta && (
                <ExportarModal
                    onClose={() => !baixandoCsv && setEscolhaAberta(false)}
                    onEscolher={escolher}
                    baixandoCsv={baixandoCsv}
                />
            )}

            {pdfConfigAberto && (
                <SalvarPdfModal
                    metricas={metricas}
                    onClose={() => setPdfConfigAberto(false)}
                    onConfirmar={confirmarPdf}
                />
            )}

            {pendentePdf && (
                <GraficosExport
                    metricas={metricas}
                    ids={pendentePdf.graficosIds}
                    onPronto={aoPronto}
                />
            )}
        </>
    );
}

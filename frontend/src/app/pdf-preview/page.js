"use client";

// PÁGINA TEMPORÁRIA DE TESTE — remover depois.
import GraficosSection from "@/components/layout/GraficosSection";
import SalvarPdfButton from "@/components/layout/SalvarPdfButton";

const metricas = {
    totalReunioes: 42,
    totalRiscoMuitoAlto: 5,
    totalRiscoAlto: 8,
    totalRiscoModerado: 12,
    totalRiscoBaixo: 17,
    totalSentimentoPositivo: 18,
    totalSentimentoNeutro: 14,
    totalSentimentoNegativo: 10,
    sentimentoMedio: "NEUTRO",
    notaMedia: 6.4,
    produtoMaisCritico: { rotulo: "Protheus" },
    segmentoMaisCritico: { rotulo: "Varejo" },
    riscoPorProduto: [
        { rotulo: "Protheus", muitoAlto: 3, alto: 4, moderado: 2, baixo: 1, total: 10 },
        { rotulo: "RM", muitoAlto: 1, alto: 2, moderado: 5, baixo: 4, total: 12 },
        { rotulo: "Datasul", muitoAlto: 0, alto: 1, moderado: 3, baixo: 6, total: 10 },
    ],
    riscoPorSegmento: [
        { rotulo: "Varejo", muitoAlto: 3, alto: 3, moderado: 4, baixo: 2, total: 12 },
        { rotulo: "Indústria", muitoAlto: 1, alto: 3, moderado: 5, baixo: 8, total: 17 },
        { rotulo: "Serviços", muitoAlto: 1, alto: 2, moderado: 3, baixo: 7, total: 13 },
    ],
};

const filtros = { produtos: ["Protheus"], segmentos: [], riscos: ["ALTO", "MUITO_ALTO"], sentimentos: ["NEGATIVO"] };

export default function PdfPreview() {
    return (
        <div className="flex flex-col gap-8 p-6">
            <div className="flex flex-row items-center justify-between gap-4">
                <h2 className="text-xl font-bold text-secondary-text">Visão geral</h2>
                <SalvarPdfButton metricas={metricas} filtros={filtros} />
            </div>
            <GraficosSection metricas={metricas} loading={false} />
        </div>
    );
}

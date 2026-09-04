"use client";

import ChartCard from "@/components/charts/ChartCard";
import BarChart from "@/components/charts/BarChart";
import { construirOpcoes, COR_SUPERFICIE } from "@/components/charts/chartSetup";
import {
    COR_RISCO,
    COR_SENTIMENTO,
    LABEL_RISCO,
    LABEL_SENTIMENTO,
    percentual,
} from "@/components/charts/chartTokens";

const TOP_PRODUTOS = 8;
const RISCOS_ORDEM = ["MUITO_ALTO", "ALTO", "MODERADO", "BAIXO"];

function riscoDaChave(item, chave) {
    switch (chave) {
        case "MUITO_ALTO":
            return item.muitoAlto ?? 0;
        case "ALTO":
            return item.alto ?? 0;
        case "MODERADO":
            return item.moderado ?? 0;
        case "BAIXO":
            return item.baixo ?? 0;
        default:
            return 0;
    }
}

// Ordena pelos grupos que mais precisam de atenção (mais reuniões em risco alto+)
function ordenarPorAtencao(lista) {
    return [...lista].sort(
        (a, b) => b.muitoAlto + b.alto - (a.muitoAlto + a.alto) || b.total - a.total
    );
}

function dataRiscoEmpilhado(itens) {
    return {
        labels: itens.map((i) => i.rotulo),
        datasets: RISCOS_ORDEM.map((chave) => ({
            label: LABEL_RISCO[chave],
            data: itens.map((i) => riscoDaChave(i, chave)),
            backgroundColor: COR_RISCO[chave],
            borderColor: COR_SUPERFICIE,
            borderWidth: { top: 2, right: 0, bottom: 0, left: 0 },
            borderRadius: 2,
            borderSkipped: false,
            maxBarThickness: 64,
        })),
    };
}

function Skeleton() {
    return (
        <div className="grid grid-cols-1 gap-3 lg:grid-cols-2">
            {Array.from({ length: 4 }).map((_, i) => (
                <div
                    key={i}
                    className={`h-56 animate-pulse rounded-lg border-2 border-secondary-bg-color bg-primary-bg-card-color ${
                        i >= 2 ? "lg:col-span-2" : ""
                    }`}
                />
            ))}
        </div>
    );
}

export default function GraficosSection({ metricas, loading }) {
    if (loading && !metricas) return <Skeleton />;
    if (!metricas) return null;

    const total = metricas.totalReunioes ?? 0;

    if (total === 0) {
        return (
            <ChartCard title="Gráficos" subtitle="Visão consolidada das análises">
                <p className="py-8 text-center text-sm text-secondary-text opacity-60">
                    Nenhuma análise para exibir gráficos.
                </p>
            </ChartCard>
        );
    }

    // --- Sentimento das reuniões ---
    const sentimentoValores = [
        metricas.totalSentimentoPositivo ?? 0,
        metricas.totalSentimentoNeutro ?? 0,
        metricas.totalSentimentoNegativo ?? 0,
    ];
    const totalSentimento = sentimentoValores.reduce((s, v) => s + v, 0);
    const dataSentimento = {
        labels: [
            LABEL_SENTIMENTO.POSITIVO,
            LABEL_SENTIMENTO.NEUTRO,
            LABEL_SENTIMENTO.NEGATIVO,
        ],
        datasets: [
            {
                label: "Reuniões",
                data: sentimentoValores,
                backgroundColor: [
                    COR_SENTIMENTO.POSITIVO,
                    COR_SENTIMENTO.NEUTRO,
                    COR_SENTIMENTO.NEGATIVO,
                ],
                borderRadius: 4,
                maxBarThickness: 72,
            },
        ],
    };
    const opcoesSentimento = construirOpcoes({
        tooltipLabel: (ctx) =>
            ` ${ctx.parsed.y} reunião(ões) · ${percentual(ctx.parsed.y, totalSentimento)}%`,
    });

    // --- Risco de churn ---
    const riscoValor = {
        MUITO_ALTO: metricas.totalRiscoMuitoAlto ?? 0,
        ALTO: metricas.totalRiscoAlto ?? 0,
        MODERADO: metricas.totalRiscoModerado ?? 0,
        BAIXO: metricas.totalRiscoBaixo ?? 0,
    };
    const emRisco = riscoValor.MUITO_ALTO + riscoValor.ALTO;
    const dataRisco = {
        labels: RISCOS_ORDEM.map((chave) => LABEL_RISCO[chave]),
        datasets: [
            {
                label: "Reuniões",
                data: RISCOS_ORDEM.map((chave) => riscoValor[chave]),
                backgroundColor: RISCOS_ORDEM.map((chave) => COR_RISCO[chave]),
                borderRadius: 4,
                maxBarThickness: 72,
            },
        ],
    };
    const opcoesRisco = construirOpcoes({
        tooltipLabel: (ctx) =>
            ` ${ctx.parsed.y} reunião(ões) · ${percentual(ctx.parsed.y, total)}%`,
    });

    const opcoesRiscoEmpilhado = construirOpcoes({
        empilhado: true,
        legenda: true,
        tooltipLabel: (ctx) => ` ${ctx.dataset.label}: ${ctx.parsed.y}`,
    });

    // --- Risco de churn por produto (mais frequentes primeiro) ---
    const produtos = ordenarPorAtencao(metricas.riscoPorProduto ?? []).slice(0, TOP_PRODUTOS);

    // --- Risco de churn por segmento ---
    const segmentos = ordenarPorAtencao(metricas.riscoPorSegmento ?? []);

    return (
        <div className="grid grid-cols-1 gap-3 lg:grid-cols-2">
            <ChartCard
                title="Sentimento das reuniões"
                subtitle={`${total} análise(s) no período filtrado`}
            >
                <BarChart data={dataSentimento} options={opcoesSentimento} altura="h-56" />
            </ChartCard>

            <ChartCard
                title="Risco de churn"
                subtitle={`${emRisco} de ${total} em risco alto ou muito alto (${percentual(
                    emRisco,
                    total
                )}%)`}
            >
                <BarChart data={dataRisco} options={opcoesRisco} altura="h-56" />
            </ChartCard>

            <ChartCard
                title="Risco de churn por produto"
                subtitle="Composição do risco nos produtos mais frequentes"
                className="lg:col-span-2"
            >
                {produtos.length === 0 ? (
                    <p className="text-xs text-secondary-text opacity-60">
                        Nenhum produto associado às análises.
                    </p>
                ) : (
                    <BarChart
                        data={dataRiscoEmpilhado(produtos)}
                        options={opcoesRiscoEmpilhado}
                        altura="h-72"
                    />
                )}
            </ChartCard>

            <ChartCard
                title="Risco de churn por segmento"
                subtitle="Composição do risco em cada segmento de cliente"
                className="lg:col-span-2"
            >
                {segmentos.length === 0 ? (
                    <p className="text-xs text-secondary-text opacity-60">
                        Nenhum segmento associado às análises.
                    </p>
                ) : (
                    <BarChart
                        data={dataRiscoEmpilhado(segmentos)}
                        options={opcoesRiscoEmpilhado}
                        altura="h-72"
                    />
                )}
            </ChartCard>
        </div>
    );
}

"use client";

import { useEffect, useMemo, useRef } from "react";

import BarChart from "@/components/charts/BarChart";
import PieChart from "@/components/charts/PieChart";
import { montarGraficos } from "@/components/layout/GraficosSection";

// Dimensões (px CSS) das telas de renderização fora de tela usadas só na exportação.
const TAMANHO = {
    meia: { w: 480, h: 320 },
    inteira: { w: 1000, h: 360 },
};

// Renderiza os gráficos do dashboard em canvases grandes, fora da viewport, e
// devolve as imagens PNG (via Chart.js) para o gerador de PDF.
export default function GraficosExport({ metricas, onPronto }) {
    const graficos = useMemo(
        () => montarGraficos(metricas).filter((g) => g.data),
        [metricas]
    );
    const refs = useRef([]);

    useEffect(() => {
        if (graficos.length === 0) {
            onPronto({});
            return;
        }

        let cancelado = false;
        const timer = setTimeout(() => {
            if (cancelado) return;

            const imagens = {};
            graficos.forEach((g, i) => {
                const chart = refs.current[i];
                if (!chart) return;
                try {
                    imagens[g.id] = {
                        dataUrl: chart.toBase64Image("image/png"),
                        w: chart.canvas.width,
                        h: chart.canvas.height,
                    };
                } catch {
                    // ignora gráfico que falhar ao exportar
                }
            });
            onPronto(imagens);
        }, 350);

        return () => {
            cancelado = true;
            clearTimeout(timer);
        };
    }, [graficos, onPronto]);

    return (
        <div
            aria-hidden="true"
            style={{
                position: "fixed",
                left: -10000,
                top: 0,
                opacity: 0,
                pointerEvents: "none",
            }}
        >
            {graficos.map((g, i) => {
                const tam = TAMANHO[g.largura] ?? TAMANHO.meia;
                const Grafico = g.tipo === "pizza" ? PieChart : BarChart;
                return (
                    <div
                        key={g.id}
                        style={{
                            width: tam.w,
                            height: tam.h,
                            display: "grid",
                            background: "#1E3450",
                        }}
                    >
                        <Grafico
                            ref={(el) => {
                                refs.current[i] = el;
                            }}
                            data={g.data}
                            options={{
                                ...g.options,
                                animation: false,
                                devicePixelRatio: 2,
                            }}
                            altura=""
                        />
                    </div>
                );
            })}
        </div>
    );
}

"use client";

import {
    Chart as ChartJS,
    BarElement,
    CategoryScale,
    LinearScale,
    Tooltip,
    Legend,
} from "chart.js";

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend);

// Tokens do tema escuro do app
export const COR_TEXTO = "#FFFFFF";
export const COR_TEXTO_SUAVE = "rgba(255, 255, 255, 0.72)";
export const COR_GRID = "rgba(255, 255, 255, 0.09)";
export const COR_SUPERFICIE = "#1E3450"; // separa segmentos empilhados
export const COR_TOOLTIP_BG = "#0B1829";

export function construirOpcoes({ empilhado = false, legenda = false, tooltipLabel } = {}) {
    return {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: legenda,
                position: "bottom",
                labels: {
                    color: COR_TEXTO,
                    boxWidth: 12,
                    boxHeight: 12,
                    padding: 14,
                    font: { size: 11 },
                },
            },
            tooltip: {
                backgroundColor: COR_TOOLTIP_BG,
                borderColor: COR_GRID,
                borderWidth: 1,
                titleColor: COR_TEXTO,
                bodyColor: COR_TEXTO_SUAVE,
                padding: 10,
                callbacks: tooltipLabel ? { label: tooltipLabel } : {},
            },
        },
        scales: {
            x: {
                stacked: empilhado,
                grid: { display: false },
                border: { color: COR_GRID },
                ticks: {
                    color: COR_TEXTO_SUAVE,
                    font: { size: 11 },
                    maxRotation: 45,
                    minRotation: 0,
                },
            },
            y: {
                stacked: empilhado,
                beginAtZero: true,
                grid: { color: COR_GRID },
                border: { display: false },
                ticks: { color: COR_TEXTO_SUAVE, font: { size: 11 }, precision: 0 },
            },
        },
    };
}

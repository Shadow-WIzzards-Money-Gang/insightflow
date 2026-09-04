// Cores de dados. Sentimento e risco usam as cores semânticas (status) do tema;
// volume usa hue único (cyan primário) — comparação de magnitude.

export const COR_SENTIMENTO = {
    POSITIVO: "#10B981",
    NEUTRO: "#F59E0B",
    NEGATIVO: "#EF4444",
};

export const COR_RISCO = {
    BAIXO: "#10B981",
    MODERADO: "#F59E0B",
    ALTO: "#F97316",
    MUITO_ALTO: "#EF4444",
};

export const COR_VOLUME = "#00B4D8";

export const LABEL_SENTIMENTO = {
    POSITIVO: "Positivo",
    NEUTRO: "Neutro",
    NEGATIVO: "Negativo",
};

export const LABEL_RISCO = {
    MUITO_ALTO: "Muito alto",
    ALTO: "Alto",
    MODERADO: "Moderado",
    BAIXO: "Baixo",
};

export function percentual(valor, total) {
    if (!total) return 0;
    return Math.round((valor / total) * 100);
}

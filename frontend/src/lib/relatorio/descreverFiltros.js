import { LABEL_RISCO, LABEL_SENTIMENTO } from "@/components/charts/chartTokens";

// Texto legível dos filtros ativos, para o cabeçalho do relatório em PDF.
export function descreverFiltros(filtros) {
    if (!filtros) return "Sem filtros — todos os dados";

    const partes = [];

    if (filtros.produtos?.length) {
        partes.push(`Produtos: ${filtros.produtos.join(", ")}`);
    }
    if (filtros.segmentos?.length) {
        partes.push(`Segmentos: ${filtros.segmentos.join(", ")}`);
    }
    if (filtros.riscos?.length) {
        partes.push(
            `Risco: ${filtros.riscos.map((v) => LABEL_RISCO[v] ?? v).join(", ")}`
        );
    }
    if (filtros.sentimentos?.length) {
        partes.push(
            `Sentimento: ${filtros.sentimentos
                .map((v) => LABEL_SENTIMENTO[v] ?? v)
                .join(", ")}`
        );
    }

    return partes.length > 0 ? partes.join("  ·  ") : "Sem filtros — todos os dados";
}

import { montarGraficos } from "@/components/layout/GraficosSection";
import { LABEL_RISCO, LABEL_SENTIMENTO } from "@/components/charts/chartTokens";
import { descreverFiltros } from "@/lib/relatorio/descreverFiltros";

const MARGEM = 15;
const LARGURA_PAGINA = 210;
const ALTURA_PAGINA = 297;
const LARGURA_CONTEUDO = LARGURA_PAGINA - MARGEM * 2;

const COR = {
    tinta: [15, 23, 42],
    suave: [100, 116, 139],
    linha: [203, 213, 225],
    cardClaro: [241, 245, 249],
    marca: [0, 180, 216],
    cardEscuro: [30, 52, 80],
    cardEscuroTexto: [205, 214, 228],
    branco: [255, 255, 255],
    erro: [239, 68, 68],
    alerta: [245, 158, 11],
    sucesso: [16, 185, 129],
};

const SENTIMENTO_LABEL = {
    POSITIVO: "Positivo",
    NEUTRO: "Neutro",
    NEGATIVO: "Negativo",
};

function formatarData(iso) {
    if (!iso) return "—";
    const d = new Date(iso);
    return Number.isNaN(d.getTime()) ? "—" : d.toLocaleDateString("pt-BR");
}

// Mesmas regras de cor do dashboard (page.js)
function corSentimento(s) {
    if (s === "POSITIVO") return COR.sucesso;
    if (s === "NEUTRO") return COR.alerta;
    if (s === "NEGATIVO") return COR.erro;
    return COR.tinta;
}

function corNota(n) {
    if (n == null) return COR.tinta;
    if (n >= 8) return COR.sucesso;
    if (n >= 3) return COR.alerta;
    return COR.erro;
}

function montarCards(metricas) {
    const totalChurn =
        (metricas.totalRiscoMuitoAlto ?? 0) + (metricas.totalRiscoAlto ?? 0);

    return [
        {
            label: "Reuniões analisadas",
            valor: String(metricas.totalReunioes ?? 0),
            cor: COR.tinta,
        },
        {
            label: "Risco de Churn",
            valor: String(totalChurn),
            cor: COR.erro,
        },
        {
            label: "Sentimento médio",
            valor: SENTIMENTO_LABEL[metricas.sentimentoMedio] ?? "—",
            cor: corSentimento(metricas.sentimentoMedio),
        },
        {
            label: "Score médio",
            valor:
                metricas.notaMedia != null ? metricas.notaMedia.toFixed(1) : "—",
            cor: corNota(metricas.notaMedia),
        },
        {
            label: "Produto mais crítico",
            valor: metricas.produtoMaisCritico?.rotulo ?? "—",
            cor: metricas.produtoMaisCritico ? COR.erro : COR.tinta,
        },
        {
            label: "Segmento mais crítico",
            valor: metricas.segmentoMaisCritico?.rotulo ?? "—",
            cor: metricas.segmentoMaisCritico ? COR.erro : COR.tinta,
        },
    ];
}

function desenharCardKpi(doc, x, y, w, h, card) {
    doc.setFillColor(...COR.cardClaro);
    doc.setDrawColor(...COR.linha);
    doc.setLineWidth(0.2);
    doc.roundedRect(x, y, w, h, 2, 2, "FD");

    doc.setFont("helvetica", "normal");
    doc.setFontSize(8);
    doc.setTextColor(...COR.suave);
    doc.text(card.label, x + w / 2, y + h - 4, { align: "center", maxWidth: w - 6 });

    const curto = card.valor.length <= 10;
    doc.setFont("helvetica", "bold");
    doc.setFontSize(curto ? 19 : 11);
    doc.setTextColor(...card.cor);

    const linhas = doc.splitTextToSize(card.valor, w - 6).slice(0, 3);
    const alturaLinha = curto ? 8 : 4.8;
    const areaTopo = y + 4;
    const areaFundo = y + h - 9;
    let ty =
        (areaTopo + areaFundo) / 2 - (linhas.length * alturaLinha) / 2 + alturaLinha * 0.75;

    linhas.forEach((ln) => {
        doc.text(ln, x + w / 2, ty, { align: "center" });
        ty += alturaLinha;
    });
}

function alturaCardGrafico(w, img) {
    const imgW = w - 10;
    const imgH = imgW * (img.h / img.w);
    return 17 + imgH + 5;
}

function desenharCardGrafico(doc, x, y, w, h, grafico, img) {
    doc.setFillColor(...COR.cardEscuro);
    doc.roundedRect(x, y, w, h, 2.5, 2.5, "F");

    doc.setFont("helvetica", "bold");
    doc.setFontSize(10);
    doc.setTextColor(...COR.branco);
    doc.text(grafico.title, x + 5, y + 7);

    if (grafico.subtitle) {
        doc.setFont("helvetica", "normal");
        doc.setFontSize(7.5);
        doc.setTextColor(...COR.cardEscuroTexto);
        doc.text(doc.splitTextToSize(grafico.subtitle, w - 10)[0], x + 5, y + 12);
    }

    const imgW = w - 10;
    const imgH = imgW * (img.h / img.w);
    doc.addImage(img.dataUrl, "PNG", x + 5, y + 17, imgW, imgH);
}

export async function gerarRelatorioPdf({
    metricas,
    filtros,
    imagens,
    graficosIds,
    analises,
}) {
    const { jsPDF } = await import("jspdf");
    const doc = new jsPDF({ unit: "mm", format: "a4", orientation: "portrait" });

    const limiteInferior = ALTURA_PAGINA - MARGEM - 8;
    const garantirEspaco = (y, necessario) => {
        if (y + necessario > limiteInferior) {
            doc.addPage();
            return MARGEM;
        }
        return y;
    };

    // --- Cabeçalho ---
    let y = MARGEM + 2;

    doc.setFont("helvetica", "bold");
    doc.setFontSize(18);
    doc.setTextColor(...COR.marca);
    doc.text("INSIGHT FLOW", MARGEM, y);
    y += 8;

    doc.setFont("helvetica", "normal");
    doc.setFontSize(12);
    doc.setTextColor(...COR.tinta);
    doc.text("Relatório de Análises de Reunião", MARGEM, y);
    y += 6;

    doc.setFontSize(9);
    doc.setTextColor(...COR.suave);
    doc.text(`Gerado em ${new Date().toLocaleString("pt-BR")}`, MARGEM, y);
    y += 6;

    const linhasFiltro = doc.splitTextToSize(
        `Filtros aplicados:  ${descreverFiltros(filtros)}`,
        LARGURA_CONTEUDO
    );
    doc.text(linhasFiltro, MARGEM, y);
    y += linhasFiltro.length * 4 + 3;

    doc.setDrawColor(...COR.linha);
    doc.setLineWidth(0.3);
    doc.line(MARGEM, y, LARGURA_PAGINA - MARGEM, y);
    y += 9;

    // --- Cards de indicadores ---
    const cards = montarCards(metricas);
    const colGap = 4;
    const linGap = 4;
    const cw = (LARGURA_CONTEUDO - colGap * 2) / 3;
    const ch = 26;

    cards.forEach((card, i) => {
        const col = i % 3;
        const lin = Math.floor(i / 3);
        desenharCardKpi(
            doc,
            MARGEM + col * (cw + colGap),
            y + lin * (ch + linGap),
            cw,
            ch,
            card
        );
    });
    y += 2 * ch + linGap + 10;

    // --- Gráficos ---
    const graficos = montarGraficos(metricas).filter(
        (g) =>
            imagens[g.id] &&
            (!graficosIds?.length || graficosIds.includes(g.id))
    );
    const meias = graficos.filter((g) => g.largura !== "inteira");
    const inteiras = graficos.filter((g) => g.largura === "inteira");

    const larguraMeia = (LARGURA_CONTEUDO - 6) / 2;
    for (let i = 0; i < meias.length; i += 2) {
        const par = meias.slice(i, i + 2);
        const hLinha = Math.max(
            ...par.map((g) => alturaCardGrafico(larguraMeia, imagens[g.id]))
        );
        y = garantirEspaco(y, hLinha);
        par.forEach((g, j) => {
            desenharCardGrafico(
                doc,
                MARGEM + j * (larguraMeia + 6),
                y,
                larguraMeia,
                hLinha,
                g,
                imagens[g.id]
            );
        });
        y += hLinha + 6;
    }

    for (const g of inteiras) {
        const h = alturaCardGrafico(LARGURA_CONTEUDO, imagens[g.id]);
        y = garantirEspaco(y, h);
        desenharCardGrafico(doc, MARGEM, y, LARGURA_CONTEUDO, h, g, imagens[g.id]);
        y += h + 6;
    }

    // --- Análises de reunião ---
    if (Array.isArray(analises) && analises.length > 0) {
        const { default: autoTable } = await import("jspdf-autotable");

        y = garantirEspaco(y, 24);
        doc.setFont("helvetica", "bold");
        doc.setFontSize(12);
        doc.setTextColor(...COR.tinta);
        doc.text(`Análises de reunião (${analises.length})`, MARGEM, y);
        y += 4;

        autoTable(doc, {
            startY: y,
            head: [["Nº", "Assunto", "Data", "Sentimento", "Risco", "Score"]],
            body: analises.map((a) => [
                `#${a.id}`,
                a.assunto || "—",
                formatarData(a.reuniao?.dataReuniao),
                LABEL_SENTIMENTO[a.sentimentoReuniao] ?? a.sentimentoReuniao ?? "—",
                LABEL_RISCO[a.riscoCancelamento] ?? a.riscoCancelamento ?? "—",
                a.nota ?? "—",
            ]),
            margin: { left: MARGEM, right: MARGEM },
            styles: {
                font: "helvetica",
                fontSize: 8,
                cellPadding: 2,
                textColor: COR.tinta,
                lineColor: COR.linha,
                lineWidth: 0.1,
            },
            headStyles: {
                fillColor: COR.cardEscuro,
                textColor: COR.branco,
                fontStyle: "bold",
            },
            alternateRowStyles: { fillColor: COR.cardClaro },
            columnStyles: {
                0: { cellWidth: 14 },
                1: { cellWidth: "auto" },
                2: { cellWidth: 22 },
                3: { cellWidth: 24 },
                4: { cellWidth: 22 },
                5: { cellWidth: 14, halign: "right" },
            },
        });

        y = (doc.lastAutoTable?.finalY ?? y) + 10;

        // Resumo do motivo do risco para análises de risco alto / muito alto
        const criticas = analises.filter(
            (a) =>
                (a.riscoCancelamento === "ALTO" ||
                    a.riscoCancelamento === "MUITO_ALTO") &&
                a.motivoCancelamento &&
                a.motivoCancelamento.trim()
        );

        if (criticas.length > 0) {
            y = garantirEspaco(y, 16);
            doc.setFont("helvetica", "bold");
            doc.setFontSize(11);
            doc.setTextColor(...COR.tinta);
            doc.text("Resumo — risco alto e muito alto", MARGEM, y);
            y += 6;

            criticas.forEach((a) => {
                const titulo = `#${a.id} — ${a.assunto || "Sem assunto"} (${
                    LABEL_RISCO[a.riscoCancelamento] ?? a.riscoCancelamento
                })`;

                doc.setFontSize(9);
                const tituloLinhas = doc.splitTextToSize(titulo, LARGURA_CONTEUDO);
                const corpoLinhas = doc.splitTextToSize(
                    a.motivoCancelamento.trim(),
                    LARGURA_CONTEUDO
                );
                const alturaBloco =
                    (tituloLinhas.length + corpoLinhas.length) * 4.4 + 4;

                y = garantirEspaco(y, alturaBloco);

                doc.setFont("helvetica", "bold");
                doc.setTextColor(...COR.tinta);
                doc.text(tituloLinhas, MARGEM, y);
                y += tituloLinhas.length * 4.4;

                doc.setFont("helvetica", "normal");
                doc.setTextColor(...COR.suave);
                doc.text(corpoLinhas, MARGEM, y);
                y += corpoLinhas.length * 4.4 + 4;
            });
        }
    }

    // --- Rodapé ---
    const totalPaginas = doc.getNumberOfPages();
    for (let p = 1; p <= totalPaginas; p++) {
        doc.setPage(p);
        doc.setFont("helvetica", "normal");
        doc.setFontSize(8);
        doc.setTextColor(...COR.suave);
        doc.text("Insight Flow", MARGEM, ALTURA_PAGINA - 8);
        doc.text(
            `Página ${p} de ${totalPaginas}`,
            LARGURA_PAGINA - MARGEM,
            ALTURA_PAGINA - 8,
            { align: "right" }
        );
    }

    const agora = new Date();
    const data = `${agora.getFullYear()}-${String(agora.getMonth() + 1).padStart(
        2,
        "0"
    )}-${String(agora.getDate()).padStart(2, "0")}`;
    doc.save(`insight-flow-relatorio-${data}.pdf`);
}

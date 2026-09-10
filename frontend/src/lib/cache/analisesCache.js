// Cache no cliente para as análises: evita reesperar a API quando o usuário volta
// a uma combinação de página/filtro já vista ou reabre uma análise.
//
// Estratégia stale-while-revalidate: entrega o que está em cache imediatamente e
// deixa quem chamou revalidar em segundo plano.
//
// Camadas:
// - memória (rápida, sincrona, some no refresh);
// - espelho em sessionStorage (sobrevive ao refresh) guardando apenas os campos que
//   a lista usa — sem a transcrição bruta, que é grande.

const MAX_LISTAS = 24;
const MAX_DETALHES = 20;
const STORAGE_KEY = "insightflow:cache-analises:v1";

const listas = new Map(); // chave -> { analises, totalPages, metricas, ts }
const detalhes = new Map(); // id -> { dados, ts }

let hidratado = false;

function norm(arr) {
    return [...(arr ?? [])].map(String).sort();
}

function chaveLista(page, filtros = {}) {
    return JSON.stringify({
        page,
        produtos: norm(filtros.produtos),
        segmentos: norm(filtros.segmentos),
        riscos: norm(filtros.riscos),
        sentimentos: norm(filtros.sentimentos),
    });
}

// Mantém só o que o ReuniaoCard renderiza.
function enxugarAnalises(analises) {
    return (analises ?? []).map((a) => ({
        id: a.id,
        assunto: a.assunto,
        nota: a.nota,
        sentimentoReuniao: a.sentimentoReuniao,
        riscoCancelamento: a.riscoCancelamento,
        reuniao: { dataReuniao: a.reuniao?.dataReuniao ?? null },
    }));
}

function podar(mapa, max) {
    while (mapa.size > max) mapa.delete(mapa.keys().next().value);
}

function hidratar() {
    if (hidratado) return;
    hidratado = true;
    if (typeof window === "undefined") return;
    try {
        const bruto = window.sessionStorage.getItem(STORAGE_KEY);
        if (!bruto) return;
        const salvo = JSON.parse(bruto);
        for (const [k, v] of Object.entries(salvo?.listas ?? {})) {
            listas.set(k, v);
        }
    } catch {
        // sessionStorage indisponível ou conteúdo corrompido — ignora.
    }
}

function persistir() {
    if (typeof window === "undefined") return;
    try {
        const obj = { listas: {} };
        for (const [k, v] of [...listas.entries()].slice(-MAX_LISTAS)) {
            obj.listas[k] = { ...v, analises: enxugarAnalises(v.analises) };
        }
        window.sessionStorage.setItem(STORAGE_KEY, JSON.stringify(obj));
    } catch {
        // Quota estourou ou indisponível — o cache em memória continua valendo.
    }
}

/* ---------- lista + métricas ---------- */

export function lerListaCache(page, filtros) {
    hidratar();
    return listas.get(chaveLista(page, filtros)) ?? null;
}

export function gravarListaCache(page, filtros, { analises, totalPages, metricas }) {
    hidratar();
    const k = chaveLista(page, filtros);
    listas.delete(k);
    listas.set(k, { analises, totalPages, metricas, ts: Date.now() });
    podar(listas, MAX_LISTAS);
    persistir();
}

/* ---------- detalhe por id ---------- */

export function lerDetalheCache(id) {
    hidratar();
    return detalhes.get(String(id))?.dados ?? null;
}

export function gravarDetalheCache(id, dados) {
    detalhes.delete(String(id));
    detalhes.set(String(id), { dados, ts: Date.now() });
    podar(detalhes, MAX_DETALHES);
}

/* ---------- invalidação (ao criar uma nova análise) ---------- */

export function invalidarCacheAnalises() {
    listas.clear();
    detalhes.clear();
    if (typeof window === "undefined") return;
    try {
        window.sessionStorage.removeItem(STORAGE_KEY);
    } catch {
        // ignora
    }
}

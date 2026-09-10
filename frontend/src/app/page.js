"use client";

import { useCallback, useEffect, useRef, useState } from "react";

import ReuniaoCard from "@/components/layout/ReuniaoCard";
import AnaliseDetalhesModal from "@/components/layout/AnaliseDetalhesModal";
import GraficosSection from "@/components/layout/GraficosSection";
import ExportarButton from "@/components/layout/ExportarButton";
import Card from "@/components/ui/Card";
import NextPage from "@/components/ui/nextPage";
import PrevPage from "@/components/ui/prevPage";
import Loading from "@/components/ui/Loading";
import ErrorMessage from "@/components/ui/ErrorMessage";
import { getAnalisesReuniao } from "@/services/api";
import {
  lerListaCache,
  gravarListaCache,
  invalidarCacheAnalises,
} from "@/lib/cache/analisesCache";
import { useFiltros } from "@/context/FiltrosProvider";

const PAGE_SIZE = 10;

const SENTIMENTO_LABEL = {
  POSITIVO: "Positivo",
  NEUTRO: "Neutro",
  NEGATIVO: "Negativo",
};

const CORES = {
  verde: {
    textColor: "text-success-color",
    bgColor: "bg-tertiary-bg-card-color",
    borderColor: "border-success-color",
  },
  amarelo: {
    textColor: "text-warning-color",
    bgColor: "bg-fourth-bg-card-color",
    borderColor: "border-warning-color",
  },
  vermelho: {
    textColor: "text-error-color",
    bgColor: "bg-secondary-bg-card-color",
    borderColor: "border-error-color",
  },
  neutra: {
    textColor: "text-secondary-text",
    bgColor: "bg-primary-bg-card-color",
    borderColor: "border-secondary-bg-color",
  },
};

const SENTIMENTO_CORES = {
  POSITIVO: CORES.verde,
  NEUTRO: CORES.amarelo,
  NEGATIVO: CORES.vermelho,
};

// Mesmos limites do SentimentoReuniao.fromValor no backend
function coresPorNota(nota) {
  if (nota == null) return CORES.neutra;
  if (nota >= 8) return CORES.verde;
  if (nota >= 3) return CORES.amarelo;
  return CORES.vermelho;
}

export default function Home() {
  const { filtros, totalFiltrosAtivos } = useFiltros();

  const [analises, setAnalises] = useState([]);
  const [metricas, setMetricas] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [revalidando, setRevalidando] = useState(false);
  const [error, setError] = useState(null);
  const [analiseSelecionada, setAnaliseSelecionada] = useState(null);

  // Descarta respostas de requisições antigas (troca rápida de filtro/página)
  const requisicaoRef = useRef(0);

  // Volta para a primeira página sempre que os filtros mudam
  const [filtrosAnteriores, setFiltrosAnteriores] = useState(filtros);
  if (filtros !== filtrosAnteriores) {
    setFiltrosAnteriores(filtros);
    setPage(0);
  }

  const carregarAnalises = useCallback(async (paginaAtual, filtrosAtuais) => {
    const idReq = ++requisicaoRef.current;
    const ehAtual = () => idReq === requisicaoRef.current;

    // stale-while-revalidate: se já vimos essa página/filtro, mostra na hora
    const cache = lerListaCache(paginaAtual, filtrosAtuais);
    if (cache) {
      setAnalises(cache.analises ?? []);
      setTotalPages(cache.totalPages ?? 0);
      setMetricas(cache.metricas ?? null);
      setError(null);
      setLoading(false);
      setRevalidando(true);
    } else {
      setLoading(true);
      setError(null);
    }

    try {
      const dados = await getAnalisesReuniao(paginaAtual, PAGE_SIZE, filtrosAtuais);
      if (!ehAtual()) return;

      const analisesNovas = dados.analises?.content ?? [];
      const totalPagesNovo = dados.analises?.totalPages ?? 0;
      const metricasNovas = dados.metricas ?? null;

      setAnalises(analisesNovas);
      setTotalPages(totalPagesNovo);
      setMetricas(metricasNovas);
      gravarListaCache(paginaAtual, filtrosAtuais, {
        analises: analisesNovas,
        totalPages: totalPagesNovo,
        metricas: metricasNovas,
      });
    } catch (e) {
      if (!ehAtual()) return;
      // Com cache em tela, ignora erro de revalidação e mantém o que já aparece.
      if (!cache) {
        setError(e.message ?? "Erro inesperado ao carregar as análises.");
        setAnalises([]);
        setTotalPages(0);
        setMetricas(null);
      }
    } finally {
      if (ehAtual()) {
        setLoading(false);
        setRevalidando(false);
      }
    }
  }, []);

  const totalChurn =
    metricas != null
      ? (metricas.totalRiscoMuitoAlto ?? 0) + (metricas.totalRiscoAlto ?? 0)
      : null;

  const coresSentimento =
    (metricas != null && SENTIMENTO_CORES[metricas.sentimentoMedio]) || CORES.neutra;
  const coresNota =
    metricas != null ? coresPorNota(metricas.notaMedia) : CORES.neutra;

  // Soma de risco moderado + alto + muito alto, como no backend (DistribuicaoRiscoResponse.totalCritico)
  const totalCritico = (item) =>
    item != null
      ? (item.muitoAlto ?? 0) + (item.alto ?? 0) + (item.moderado ?? 0)
      : 0;

  const produtoCritico = metricas?.produtoMaisCritico ?? null;
  const segmentoCritico = metricas?.segmentoMaisCritico ?? null;

  useEffect(() => {
    carregarAnalises(page, filtros);
  }, [page, filtros, carregarAnalises]);

  useEffect(() => {
    const recarregar = () => {
      // Uma nova análise muda contagens e métricas de qualquer recorte.
      invalidarCacheAnalises();
      carregarAnalises(page, filtros);
    };
    window.addEventListener("analise:criada", recarregar);
    return () => window.removeEventListener("analise:criada", recarregar);
  }, [page, filtros, carregarAnalises]);

  const podeVoltar = page > 0 && !loading;
  const podeAvancar = page < totalPages - 1 && !loading;

  return (
    <div className="flex flex-col flex-1 gap-6 px-3 py-4 sm:gap-8 sm:px-5 sm:py-6">

      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <h2 className="text-lg font-bold text-secondary-text sm:text-xl">Visão geral</h2>
        <ExportarButton metricas={metricas} filtros={filtros} />
      </div>

      <div className="relative flex flex-col gap-6 sm:gap-8" aria-busy={loading}>

      {loading && metricas != null && (
        <div className="pointer-events-none absolute inset-0 z-20 flex justify-center bg-body-bg/60 backdrop-blur-[1px]">
          <div className="sticky top-20 h-fit self-start rounded-xl border-2 border-secondary-bg-color bg-primary-bg-card-color/95 px-8 shadow-lg shadow-black/20">
            <Loading label="Atualizando resultados..." />
          </div>
        </div>
      )}

      <div className={`flex flex-col gap-2 sm:gap-3 transition-opacity ${loading && metricas != null ? "opacity-40" : ""}`}>
      <div className="grid grid-cols-2 gap-2 sm:gap-3 md:grid-cols-4">
        <Card
          value={metricas != null ? String(metricas.totalReunioes ?? 0) : "—"}
          label="Reuniões analisadas"
          textColor="text-primary-text"
          bgColor="bg-primary-bg-card-color"
          borderColor="border-primary-text"
        />
        <Card
          value={totalChurn != null ? String(totalChurn) : "—"}
          label="Risco de Churn"
          textColor="text-error-color"
          bgColor="bg-secondary-bg-card-color"
          borderColor="border-error-color"
        />
        <Card
          value={
            metricas != null
              ? SENTIMENTO_LABEL[metricas.sentimentoMedio] ?? "—"
              : "—"
          }
          label="Sentimento médio"
          textColor={coresSentimento.textColor}
          bgColor={coresSentimento.bgColor}
          borderColor={coresSentimento.borderColor}
        />
        <Card
          value={
            metricas != null && metricas.notaMedia != null
              ? metricas.notaMedia.toFixed(1)
              : "—"
          }
          label="Score médio"
          textColor={coresNota.textColor}
          bgColor={coresNota.bgColor}
          borderColor={coresNota.borderColor}
        />
      </div>

      <div className="grid grid-cols-2 gap-2 sm:gap-3">
        <Card
          value={produtoCritico?.rotulo ?? "—"}
          label={"Produto mais crítico"}
          textColor={produtoCritico != null ? CORES.vermelho.textColor : CORES.neutra.textColor}
          bgColor={produtoCritico != null ? CORES.vermelho.bgColor : CORES.neutra.bgColor}
          borderColor={produtoCritico != null ? CORES.vermelho.borderColor : CORES.neutra.borderColor}
        />
        <Card
          value={segmentoCritico?.rotulo ?? "—"}
          label={"Segmento mais crítico"}
          textColor={segmentoCritico != null ? CORES.vermelho.textColor : CORES.neutra.textColor}
          bgColor={segmentoCritico != null ? CORES.vermelho.bgColor : CORES.neutra.bgColor}
          borderColor={segmentoCritico != null ? CORES.vermelho.borderColor : CORES.neutra.borderColor}
        />
      </div>
      </div>

      <div className={`transition-opacity ${loading && metricas != null ? "opacity-40" : ""}`}>
        <GraficosSection metricas={metricas} loading={loading} />
      </div>

      <section className={`flex flex-col gap-3 transition-opacity ${loading && analises.length > 0 ? "opacity-40" : ""}`}>
        <div className="flex flex-row flex-wrap items-center justify-between gap-2">
          <h2 className="flex items-center gap-2 text-lg font-bold text-secondary-text sm:text-xl">
            Análises de reunião
            {revalidando && (
              <span
                className="h-3.5 w-3.5 animate-spin rounded-full border-2 border-secondary-bg-color border-t-primary-text"
                role="status"
                aria-label="Atualizando"
              />
            )}
          </h2>
          <div className="flex flex-row items-center gap-2">
            <PrevPage onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={!podeVoltar} />
            <span className="text-secondary-text text-sm">
              {totalPages > 0 ? `${page + 1} / ${totalPages}` : "0 / 0"}
            </span>
            <NextPage onClick={() => setPage((p) => p + 1)} disabled={!podeAvancar} />
          </div>
        </div>

        {loading && analises.length === 0 && !error && (
          <Loading label="Carregando análises..." />
        )}

        {!loading && error && (
          <ErrorMessage message={error} onRetry={() => carregarAnalises(page, filtros)} />
        )}

        {!loading && !error && analises.length === 0 && (
          <p className="text-secondary-text text-center py-16">
            {totalFiltrosAtivos > 0
              ? "Nenhuma análise encontrada para os filtros aplicados."
              : "Nenhuma análise encontrada."}
          </p>
        )}

        {!error && analises.length > 0 && (
          <div className="flex flex-col gap-2">
            <div className="w-full h-fit py-3 px-4 hidden sm:flex flex-row justify-between items-center gap-4">
              <span className="font-bold text-secondary-text whitespace-nowrap">
                Nº Reunião
            </span>

            <span className="font-bold flex-1 text-secondary-text truncate">
                Assunto
            </span>

            <span className="font-bold text-secondary-text whitespace-nowrap w-24 text-center">
                Data
            </span>

            <span className="font-bold text-secondary-text whitespace-nowrap w-20 text-center">
                Sentimento
            </span>

            <span className="font-bold text-secondary-text whitespace-nowrap w-24 text-center">
                Risco
            </span>

            <span className="font-bold text-secondary-text whitespace-nowrap w-10 text-right">
                Score
            </span>
          </div>
          {analises.map((analise) => (
              <ReuniaoCard
                key={analise.id}
                id={analise.id}
                assunto={analise.assunto}
                data={analise.reuniao?.dataReuniao}
                sentimento={analise.sentimentoReuniao}
                risco={analise.riscoCancelamento}
                score={analise.nota}
                onClick={() => setAnaliseSelecionada(analise.id)}
              />
            ))}
          </div>
        )}
      </section>

      </div>

      {analiseSelecionada != null && (
        <AnaliseDetalhesModal
          id={analiseSelecionada}
          onClose={() => setAnaliseSelecionada(null)}
        />
      )}

    </div>
  );
}

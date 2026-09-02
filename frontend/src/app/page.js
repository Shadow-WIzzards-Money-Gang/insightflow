"use client";

import { useCallback, useEffect, useState } from "react";

import ReuniaoCard from "@/components/layout/ReuniaoCard";
import AnaliseDetalhesModal from "@/components/layout/AnaliseDetalhesModal";
import Card from "@/components/ui/Card";
import NextPage from "@/components/ui/nextPage";
import PrevPage from "@/components/ui/prevPage";
import Loading from "@/components/ui/Loading";
import ErrorMessage from "@/components/ui/ErrorMessage";
import { getAnalisesReuniao } from "@/services/api";
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
  const [error, setError] = useState(null);
  const [analiseSelecionada, setAnaliseSelecionada] = useState(null);

  // Volta para a primeira página sempre que os filtros mudam
  const [filtrosAnteriores, setFiltrosAnteriores] = useState(filtros);
  if (filtros !== filtrosAnteriores) {
    setFiltrosAnteriores(filtros);
    setPage(0);
  }

  const carregarAnalises = useCallback(async (paginaAtual, filtrosAtuais) => {
    setLoading(true);
    setError(null);

    try {
      const dados = await getAnalisesReuniao(paginaAtual, PAGE_SIZE, filtrosAtuais);
      setAnalises(dados.analises?.content ?? []);
      setTotalPages(dados.analises?.totalPages ?? 0);
      setMetricas(dados.metricas ?? null);
    } catch (e) {
      setError(e.message ?? "Erro inesperado ao carregar as análises.");
      setAnalises([]);
      setTotalPages(0);
      setMetricas(null);
    } finally {
      setLoading(false);
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

  useEffect(() => {
    carregarAnalises(page, filtros);
  }, [page, filtros, carregarAnalises]);

  useEffect(() => {
    const recarregar = () => carregarAnalises(page, filtros);
    window.addEventListener("analise:criada", recarregar);
    return () => window.removeEventListener("analise:criada", recarregar);
  }, [page, filtros, carregarAnalises]);

  const podeVoltar = page > 0 && !loading;
  const podeAvancar = page < totalPages - 1 && !loading;

  return (
    <div className="flex flex-col flex-1 gap-8 px-5 py-6">

      <div className="flex flex-row gap-2 justify-center">
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

      <section className="flex flex-col gap-3">
        <div className="flex flex-row items-center justify-between">
          <h2 className="text-xl font-bold text-secondary-text">
            Análises de reunião
          </h2>
          <div className="flex flex-row items-center gap-2">
            <PrevPage onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={!podeVoltar} />
            <span className="text-secondary-text text-sm">
              {totalPages > 0 ? `${page + 1} / ${totalPages}` : "0 / 0"}
            </span>
            <NextPage onClick={() => setPage((p) => p + 1)} disabled={!podeAvancar} />
          </div>
        </div>

        {loading && <Loading label="Carregando análises..." />}

        {!loading && error && (
          <ErrorMessage message={error} onRetry={() => carregarAnalises(page)} />
        )}

        {!loading && !error && analises.length === 0 && (
          <p className="text-secondary-text text-center py-16">
            {totalFiltrosAtivos > 0
              ? "Nenhuma análise encontrada para os filtros aplicados."
              : "Nenhuma análise encontrada."}
          </p>
        )}

        {!loading && !error && analises.length > 0 && (
          <div className="flex flex-col gap-2">
            <div className="w-full h-fit py-3 px-4 flex flex-row justify-between items-center gap-4">
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

      {analiseSelecionada != null && (
        <AnaliseDetalhesModal
          id={analiseSelecionada}
          onClose={() => setAnaliseSelecionada(null)}
        />
      )}

    </div>
  );
}

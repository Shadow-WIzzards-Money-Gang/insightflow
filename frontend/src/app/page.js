"use client";

import { useCallback, useEffect, useState } from "react";

import ReuniaoCard from "@/components/layout/ReuniaoCard";
import Card from "@/components/ui/Card";
import NextPage from "@/components/ui/nextPage";
import PrevPage from "@/components/ui/prevPage";
import Loading from "@/components/ui/Loading";
import ErrorMessage from "@/components/ui/ErrorMessage";
import { getAnalisesReuniao } from "@/services/api";

const PAGE_SIZE = 10;

export default function Home() {
  const [analises, setAnalises] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const carregarAnalises = useCallback(async (paginaAtual) => {
    setLoading(true);
    setError(null);

    try {
      const dados = await getAnalisesReuniao(paginaAtual, PAGE_SIZE);
      setAnalises(dados.content ?? []);
      setTotalPages(dados.totalPages ?? 0);
    } catch (e) {
      setError(e.message ?? "Erro inesperado ao carregar as análises.");
      setAnalises([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    carregarAnalises(page);
  }, [page, carregarAnalises]);

  const podeVoltar = page > 0 && !loading;
  const podeAvancar = page < totalPages - 1 && !loading;

  return (
    <div className="flex flex-col flex-1 gap-8 px-5 py-6">

      <div className="flex flex-row gap-2 justify-center">
        <Card
          value="123"
          label="Reuniões analisadas"
          textColor="text-primary-text"
          bgColor="bg-primary-bg-card-color"
          borderColor="border-primary-text"
        />
        <Card
          value="123"
          label="Risco de Churn"
          textColor="text-error-color"
          bgColor="bg-secondary-bg-card-color"
          borderColor="border-error-color"
        />
        <Card
          value="Positivo"
          label="Sentimento médio"
          textColor="text-success-color"
          bgColor="bg-tertiary-bg-card-color"
          borderColor="border-success-color"
        />
        <Card
          value="5.6"
          label="Score médio"
          textColor="text-warning-color"
          bgColor="bg-fourth-bg-card-color"
          borderColor="border-warning-color"
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
            Nenhuma análise encontrada.
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
              />
            ))}
          </div>
        )}
      </section>

    </div>
  );
}

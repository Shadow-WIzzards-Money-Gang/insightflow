"use client";

import { useEffect, useState } from "react";

import Modal from "@/components/ui/Modal";
import Loading from "@/components/ui/Loading";
import ErrorMessage from "@/components/ui/ErrorMessage";
import { getAnaliseReuniaoById } from "@/services/api";

const SENTIMENTO_LABEL = {
    POSITIVO: "Positivo",
    NEUTRO: "Neutro",
    NEGATIVO: "Negativo",
};

const SENTIMENTO_COR = {
    POSITIVO: "text-success-color",
    NEUTRO: "text-warning-color",
    NEGATIVO: "text-error-color",
};

const RISCO_LABEL = {
    MUITO_ALTO: "Muito alto",
    ALTO: "Alto",
    MODERADO: "Moderado",
    BAIXO: "Baixo",
};

const RISCO_COR = {
    MUITO_ALTO: "text-error-color",
    ALTO: "text-error-color",
    MODERADO: "text-warning-color",
    BAIXO: "text-success-color",
};

function formatarDataHora(iso) {
    if (!iso) return "—";
    const data = new Date(iso);
    return Number.isNaN(data.getTime()) ? "—" : data.toLocaleString("pt-BR");
}

function Campo({ label, children, className = "" }) {
    return (
        <div className={`flex flex-col gap-1 ${className}`}>
            <span className="text-xs font-medium uppercase tracking-wide text-primary-text">
                {label}
            </span>
            <span className="text-sm text-secondary-text">{children}</span>
        </div>
    );
}

function BlocoTexto({ label, texto }) {
    return (
        <div className="flex flex-col gap-1">
            <span className="text-xs font-medium uppercase tracking-wide text-primary-text">
                {label}
            </span>
            <p className="whitespace-pre-wrap rounded border-2 border-secondary-bg-color bg-primary-bg-card-color px-3 py-2 text-sm text-secondary-text">
                {texto || "—"}
            </p>
        </div>
    );
}

export default function AnaliseDetalhesModal({ id, onClose }) {
    const [analise, setAnalise] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [tentativa, setTentativa] = useState(0);

    const tentarNovamente = () => {
        setLoading(true);
        setError(null);
        setTentativa((n) => n + 1);
    };

    useEffect(() => {
        let ativo = true;

        getAnaliseReuniaoById(id)
            .then((dados) => {
                if (ativo) {
                    setAnalise(dados);
                    setError(null);
                }
            })
            .catch((e) => {
                if (ativo) {
                    setAnalise(null);
                    setError(e.message ?? "Erro ao carregar os detalhes da análise.");
                }
            })
            .finally(() => {
                if (ativo) setLoading(false);
            });

        return () => {
            ativo = false;
        };
    }, [id, tentativa]);

    const reuniao = analise?.reuniao;

    return (
        <Modal title={`Reunião #${id}`} onClose={onClose}>
            {loading && <Loading label="Carregando detalhes..." />}

            {!loading && error && (
                <ErrorMessage message={error} onRetry={tentarNovamente} />
            )}

            {!loading && !error && analise && (
                <div className="flex flex-col gap-5">
                    <Campo label="Assunto">{analise.assunto || "—"}</Campo>

                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                        <Campo label="Data da reunião">
                            {formatarDataHora(reuniao?.dataReuniao)}
                        </Campo>
                        <Campo label="Duração">{reuniao?.duracao || "—"}</Campo>

                        <Campo label="Segmento do cliente">
                            {reuniao?.segmentoCliente?.nome || "—"}
                        </Campo>

                        <Campo label="Sentimento">
                            <span
                                className={`font-semibold ${
                                    SENTIMENTO_COR[analise.sentimentoReuniao] ?? ""
                                }`}
                            >
                                {SENTIMENTO_LABEL[analise.sentimentoReuniao] ??
                                    analise.sentimentoReuniao ??
                                    "—"}
                            </span>
                        </Campo>
                        <Campo label="Score">
                            <span className="font-semibold">{analise.nota ?? "—"}</span>
                        </Campo>

                        <Campo label="Risco de cancelamento">
                            <span
                                className={`font-semibold ${
                                    RISCO_COR[analise.riscoCancelamento] ?? ""
                                }`}
                            >
                                {RISCO_LABEL[analise.riscoCancelamento] ??
                                    analise.riscoCancelamento ??
                                    "—"}
                            </span>
                        </Campo>
                        <Campo label="Produto TOTVS">
                            {analise.produtoTotvs?.nome
                                ? `${analise.produtoTotvs.nome}${
                                      analise.produtoTotvs.categoria
                                          ? ` (${analise.produtoTotvs.categoria})`
                                          : ""
                                  }`
                                : "—"}
                        </Campo>
                    </div>

                    {analise.motivoCancelamento && (
                        <BlocoTexto
                            label="Motivo do risco"
                            texto={analise.motivoCancelamento}
                        />
                    )}

                    <BlocoTexto
                        label="Pontos positivos"
                        texto={analise.pontosPositivos}
                    />
                    <BlocoTexto
                        label="Pontos negativos"
                        texto={analise.pontosNegativos}
                    />

                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium uppercase tracking-wide text-primary-text">
                            Transcrição da reunião
                        </span>
                        <p className="max-h-64 overflow-y-auto whitespace-pre-wrap rounded border-2 border-secondary-bg-color bg-primary-bg-card-color px-3 py-2 text-sm text-secondary-text">
                            {reuniao?.transcricaoBruta || "—"}
                        </p>
                    </div>
                </div>
            )}
        </Modal>
    );
}

"use client";

import { useEffect, useRef, useState } from "react";
import toast from "react-hot-toast";

import Modal from "@/components/ui/Modal";
import { analisarReuniao, getSegmentosClientes } from "@/services/api";

function normalizarDuracao(valor) {
    if (!valor) return valor;
    return valor.length === 5 ? `${valor}:00` : valor;
}

function validar({ transcricao, dataReuniao, duracao, segmentoId }) {
    const erros = {};

    if (!transcricao || !transcricao.trim()) {
        erros.transcricao = "A transcrição não pode estar vazia.";
    }
    if (!dataReuniao) {
        erros.dataReuniao = "Informe a data e hora da reunião.";
    }
    if (!duracao) {
        erros.duracao = "Informe a duração da reunião.";
    }
    if (!segmentoId) {
        erros.segmentoId = "Selecione o segmento do cliente.";
    }

    return erros;
}

export default function NovaAnaliseModal({ onClose, onAnalisado }) {
    const [transcricao, setTranscricao] = useState("");
    const [dataReuniao, setDataReuniao] = useState("");
    const [duracao, setDuracao] = useState("");
    const [segmentoId, setSegmentoId] = useState("");

    const [erros, setErros] = useState({});
    const [segmentos, setSegmentos] = useState([]);
    const [carregandoSegmentos, setCarregandoSegmentos] = useState(true);
    const [enviando, setEnviando] = useState(false);

    const primeiroCampoRef = useRef(null);

    const limparErro = (campo) => {
        setErros((atual) => {
            if (!atual[campo]) return atual;
            const copia = { ...atual };
            delete copia[campo];
            return copia;
        });
    };

    useEffect(() => {
        let ativo = true;

        getSegmentosClientes()
            .then((dados) => {
                if (ativo) setSegmentos(Array.isArray(dados) ? dados : []);
            })
            .catch((e) => {
                if (ativo) {
                    setSegmentos([]);
                    toast.error(e.message ?? "Não foi possível carregar os segmentos.");
                }
            })
            .finally(() => {
                if (ativo) setCarregandoSegmentos(false);
            });

        return () => {
            ativo = false;
        };
    }, []);

    useEffect(() => {
        primeiroCampoRef.current?.focus();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (enviando) return;

        const form = { transcricao, dataReuniao, duracao, segmentoId };
        const errosValidacao = validar(form);
        if (Object.keys(errosValidacao).length > 0) {
            setErros(errosValidacao);
            toast.error("Preencha todos os campos antes de analisar.");
            return;
        }

        setErros({});
        setEnviando(true);

        const toastId = toast.loading(
            "Analisando reunião... isso pode levar alguns instantes."
        );

        try {
            await analisarReuniao(
                transcricao.trim(),
                dataReuniao,
                normalizarDuracao(duracao),
                Number(segmentoId)
            );

            toast.success("Reunião analisada com sucesso!", { id: toastId });
            onAnalisado?.();
            onClose?.();
        } catch (erro) {
            toast.error(erro.message ?? "Erro ao analisar a reunião.", { id: toastId });
            setEnviando(false);
        }
    };

    const classeCampo = (campo) => `
        w-full rounded border-2 bg-primary-bg-card-color text-secondary-text
        px-3 py-2 text-sm outline-none transition-colors
        focus:border-primary-text
        ${erros[campo] ? "border-error-color" : "border-secondary-bg-color"}
    `;

    return (
        <Modal title="Nova Análise" onClose={onClose} disableClose={enviando}>
            <form onSubmit={handleSubmit} className="flex flex-col gap-4" noValidate>
                <div className="flex flex-col gap-1">
                    <label
                        htmlFor="transcricao"
                        className="text-sm font-medium text-secondary-text"
                    >
                        Transcrição bruta
                    </label>
                    <textarea
                        id="transcricao"
                        ref={primeiroCampoRef}
                        value={transcricao}
                        onChange={(e) => {
                            setTranscricao(e.target.value);
                            limparErro("transcricao");
                        }}
                        rows={8}
                        placeholder="Cole aqui a transcrição completa da reunião..."
                        className={`${classeCampo("transcricao")} resize-y min-h-40`}
                    />
                    {erros.transcricao && (
                        <span className="text-xs text-error-color">{erros.transcricao}</span>
                    )}
                </div>

                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="dataReuniao"
                            className="text-sm font-medium text-secondary-text"
                        >
                            Data e hora
                        </label>
                        <input
                            id="dataReuniao"
                            type="datetime-local"
                            value={dataReuniao}
                            onChange={(e) => {
                                setDataReuniao(e.target.value);
                                limparErro("dataReuniao");
                            }}
                            className={classeCampo("dataReuniao")}
                        />
                        {erros.dataReuniao && (
                            <span className="text-xs text-error-color">
                                {erros.dataReuniao}
                            </span>
                        )}
                    </div>

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="duracao"
                            className="text-sm font-medium text-secondary-text"
                        >
                            Duração (HH:mm:ss)
                        </label>
                        <input
                            id="duracao"
                            type="time"
                            step="1"
                            value={duracao}
                            onChange={(e) => {
                                setDuracao(e.target.value);
                                limparErro("duracao");
                            }}
                            className={classeCampo("duracao")}
                        />
                        {erros.duracao && (
                            <span className="text-xs text-error-color">{erros.duracao}</span>
                        )}
                    </div>
                </div>

                <div className="flex flex-col gap-1">
                    <label
                        htmlFor="segmentoId"
                        className="text-sm font-medium text-secondary-text"
                    >
                        Segmento do cliente
                    </label>
                    <select
                        id="segmentoId"
                        value={segmentoId}
                        onChange={(e) => {
                            setSegmentoId(e.target.value);
                            limparErro("segmentoId");
                        }}
                        disabled={carregandoSegmentos}
                        className={`${classeCampo("segmentoId")} disabled:opacity-50`}
                    >
                        <option value="">
                            {carregandoSegmentos
                                ? "Carregando segmentos..."
                                : "Selecione um segmento"}
                        </option>
                        {segmentos.map((segmento) => (
                            <option key={segmento.id} value={segmento.id}>
                                {segmento.nome}
                            </option>
                        ))}
                    </select>
                    {erros.segmentoId && (
                        <span className="text-xs text-error-color">{erros.segmentoId}</span>
                    )}
                </div>

                <div className="mt-2 flex items-center justify-end gap-2">
                    <button
                        type="button"
                        onClick={() => onClose?.()}
                        disabled={enviando}
                        className={`
                            rounded px-4 py-2 text-sm font-medium text-secondary-text
                            transition-opacity hover:opacity-70
                            disabled:cursor-not-allowed disabled:opacity-40
                        `}
                    >
                        Cancelar
                    </button>
                    <button
                        type="submit"
                        disabled={enviando}
                        className={`
                            flex items-center gap-2 rounded bg-primary-text px-4 py-2
                            text-sm font-medium text-primary-bg-card-color
                            transition-opacity hover:opacity-90
                            disabled:cursor-not-allowed disabled:opacity-60
                        `}
                    >
                        {enviando && (
                            <span
                                className="h-4 w-4 animate-spin rounded-full border-2 border-primary-bg-card-color border-t-transparent"
                                aria-hidden="true"
                            />
                        )}
                        {enviando ? "Analisando..." : "Analisar"}
                    </button>
                </div>
            </form>
        </Modal>
    );
}

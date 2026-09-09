"use client";

import { useState } from "react";

import Modal from "@/components/ui/Modal";
import { montarGraficos } from "@/components/layout/GraficosSection";

export default function SalvarPdfModal({ metricas, onClose, onConfirmar }) {
    const graficos = montarGraficos(metricas).filter((g) => g.data);

    const [selecionados, setSelecionados] = useState(() => graficos.map((g) => g.id));
    const [incluirAnalises, setIncluirAnalises] = useState(false);

    const alternar = (id) =>
        setSelecionados((atual) =>
            atual.includes(id) ? atual.filter((x) => x !== id) : [...atual, id]
        );

    const todos = graficos.length > 0 && selecionados.length === graficos.length;
    const alternarTodos = () =>
        setSelecionados(todos ? [] : graficos.map((g) => g.id));

    const nadaSelecionado = selecionados.length === 0 && !incluirAnalises;

    const confirmar = () => {
        if (nadaSelecionado) return;
        onConfirmar({ graficosIds: selecionados, incluirAnalises });
    };

    return (
        <Modal title="Salvar PDF" onClose={onClose}>
            <div className="flex flex-col gap-5">
                <p className="text-sm text-secondary-text opacity-70">
                    O relatório usa os filtros aplicados no momento.
                </p>

                <fieldset className="flex flex-col gap-2">
                    <legend className="flex w-full items-center justify-between text-sm font-bold text-secondary-text">
                        <span>Gráficos</span>
                        {graficos.length > 0 && (
                            <button
                                type="button"
                                onClick={alternarTodos}
                                className="text-xs font-normal text-primary-text transition-opacity hover:opacity-70"
                            >
                                {todos ? "Limpar" : "Selecionar todos"}
                            </button>
                        )}
                    </legend>

                    {graficos.length === 0 ? (
                        <span className="text-xs text-secondary-text opacity-60">
                            Nenhum gráfico disponível.
                        </span>
                    ) : (
                        <div className="flex flex-col gap-2">
                            {graficos.map((g) => (
                                <label
                                    key={g.id}
                                    className="flex cursor-pointer items-center gap-2 text-sm text-secondary-text"
                                >
                                    <input
                                        type="checkbox"
                                        className="h-4 w-4 accent-primary-text"
                                        checked={selecionados.includes(g.id)}
                                        onChange={() => alternar(g.id)}
                                    />
                                    {g.title}
                                </label>
                            ))}
                        </div>
                    )}
                </fieldset>

                <fieldset className="flex flex-col gap-2">
                    <legend className="text-sm font-bold text-secondary-text">
                        Análises de reunião
                    </legend>
                    <label className="flex cursor-pointer items-center gap-2 text-sm text-secondary-text">
                        <input
                            type="checkbox"
                            className="h-4 w-4 accent-primary-text"
                            checked={incluirAnalises}
                            onChange={(e) => setIncluirAnalises(e.target.checked)}
                        />
                        Incluir a lista de análises no PDF (respeita os filtros)
                    </label>
                    <span className="text-xs text-secondary-text opacity-60">
                        Gera uma tabela com todas as análises filtradas e um resumo do
                        motivo para as de risco alto ou muito alto.
                    </span>
                </fieldset>

                <div className="-mt-1 flex items-center justify-between gap-2">
                    <button
                        type="button"
                        onClick={() => onClose?.()}
                        className="rounded px-4 py-2 text-sm font-medium text-secondary-text transition-opacity hover:opacity-70"
                    >
                        Cancelar
                    </button>
                    <button
                        type="button"
                        onClick={confirmar}
                        disabled={nadaSelecionado}
                        className={`
                            rounded bg-primary-text px-4 py-2 text-sm font-medium
                            text-primary-bg-card-color transition-opacity hover:opacity-90
                            disabled:cursor-not-allowed disabled:opacity-40
                        `}
                    >
                        Gerar PDF
                    </button>
                </div>
            </div>
        </Modal>
    );
}

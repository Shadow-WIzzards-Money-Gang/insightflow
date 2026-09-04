"use client";

import { useEffect, useState } from "react";
import toast from "react-hot-toast";

import Modal from "@/components/ui/Modal";
import { getProdutosTotvs, getSegmentosClientes } from "@/services/api";
import { useFiltros } from "@/context/FiltrosProvider";

const CAMPOS = ["produtos", "segmentos", "riscos", "sentimentos"];

function mesmosFiltros(a, b) {
    return CAMPOS.every(
        (campo) =>
            a[campo].length === b[campo].length &&
            a[campo].every((valor) => b[campo].includes(valor))
    );
}

const RISCOS = [
    { valor: "MUITO_ALTO", label: "Muito alto" },
    { valor: "ALTO", label: "Alto" },
    { valor: "MODERADO", label: "Moderado" },
    { valor: "BAIXO", label: "Baixo" },
];

const SENTIMENTOS = [
    { valor: "POSITIVO", label: "Positivo" },
    { valor: "NEUTRO", label: "Neutro" },
    { valor: "NEGATIVO", label: "Negativo" },
];

const LABELS = Object.fromEntries(
    [...RISCOS, ...SENTIMENTOS].map((o) => [o.valor, o.label])
);

function alternarValor(lista, valor) {
    return lista.includes(valor)
        ? lista.filter((v) => v !== valor)
        : [...lista, valor];
}

function contar(rascunho) {
    return (
        rascunho.produtos.length +
        rascunho.segmentos.length +
        rascunho.riscos.length +
        rascunho.sentimentos.length
    );
}

function Secao({ titulo, campo, opcoes, selecionados, onToggle, vazio }) {
    return (
        <fieldset className="flex flex-col gap-2">
            <legend className="text-sm font-bold text-secondary-text">
                {titulo}
                {selecionados.length > 0 && (
                    <span className="ml-2 text-xs font-normal text-primary-text">
                        ({selecionados.length})
                    </span>
                )}
            </legend>

            {opcoes.length === 0 ? (
                <span className="text-xs text-secondary-text opacity-60">{vazio}</span>
            ) : (
                <div className="grid grid-cols-1 gap-x-4 gap-y-2 sm:grid-cols-2">
                    {opcoes.map((opcao) => (
                        <label
                            key={opcao.valor}
                            className="flex cursor-pointer items-center gap-2 text-sm text-secondary-text"
                        >
                            <input
                                type="checkbox"
                                className="h-4 w-4 accent-primary-text"
                                checked={selecionados.includes(opcao.valor)}
                                onChange={() => onToggle(campo, opcao.valor)}
                            />
                            {opcao.label}
                        </label>
                    ))}
                </div>
            )}
        </fieldset>
    );
}

export default function FiltroModal({ onClose }) {
    const { filtros, aplicarFiltros, limparFiltros, totalFiltrosAtivos } = useFiltros();

    const [rascunho, setRascunho] = useState(filtros);
    const [produtos, setProdutos] = useState([]);
    const [segmentos, setSegmentos] = useState([]);
    const [carregando, setCarregando] = useState(true);

    useEffect(() => {
        let ativo = true;

        Promise.all([getProdutosTotvs(), getSegmentosClientes()])
            .then(([prods, segs]) => {
                if (!ativo) return;
                setProdutos(Array.isArray(prods) ? prods : []);
                setSegmentos(Array.isArray(segs) ? segs : []);
            })
            .catch((e) => {
                if (ativo) {
                    toast.error(
                        e.message ?? "Não foi possível carregar as opções de filtro."
                    );
                }
            })
            .finally(() => {
                if (ativo) setCarregando(false);
            });

        return () => {
            ativo = false;
        };
    }, []);

    const totalRascunho = contar(rascunho);

    const alternar = (campo, valor) => {
        setRascunho((atual) => ({
            ...atual,
            [campo]: alternarValor(atual[campo], valor),
        }));
    };

    const removerChip = (campo, valor) => alternar(campo, valor);

    // Fechar o modal (clicar fora, Esc, X ou "Filtrar") aplica o que estiver selecionado
    const fecharAplicando = () => {
        if (!mesmosFiltros(rascunho, filtros)) {
            aplicarFiltros(rascunho);
            toast.success(
                totalRascunho > 0
                    ? `Filtro aplicado (${totalRascunho}).`
                    : "Filtros limpos."
            );
        }
        onClose?.();
    };

    const limpar = () => {
        if (totalFiltrosAtivos > 0 || totalRascunho > 0) {
            limparFiltros();
            toast.success("Filtros limpos.");
        }
        onClose?.();
    };

    const chips = [
        ...rascunho.produtos.map((v) => ({ campo: "produtos", valor: v, label: v })),
        ...rascunho.segmentos.map((v) => ({ campo: "segmentos", valor: v, label: v })),
        ...rascunho.riscos.map((v) => ({
            campo: "riscos",
            valor: v,
            label: `Risco: ${LABELS[v] ?? v}`,
        })),
        ...rascunho.sentimentos.map((v) => ({
            campo: "sentimentos",
            valor: v,
            label: `Sentimento: ${LABELS[v] ?? v}`,
        })),
    ];

    const nadaParaLimpar = totalRascunho === 0 && totalFiltrosAtivos === 0;

    return (
        <Modal title="Filtrar" onClose={fecharAplicando}>
            <div className="flex flex-col gap-5">
                <div className="flex flex-col gap-2 rounded border-2 border-secondary-bg-color bg-primary-bg-card-color px-3 py-2">
                    <span className="text-xs font-medium uppercase tracking-wide text-primary-text">
                        {chips.length > 0
                            ? `Filtrando por (${chips.length})`
                            : "Nenhum filtro selecionado"}
                    </span>
                    {chips.length > 0 && (
                        <div className="flex flex-wrap gap-2">
                            {chips.map((chip) => (
                                <button
                                    key={`${chip.campo}-${chip.valor}`}
                                    type="button"
                                    onClick={() => removerChip(chip.campo, chip.valor)}
                                    className={`
                                        flex items-center gap-1 rounded-full border border-primary-text
                                        px-2 py-0.5 text-xs text-secondary-text
                                        transition-opacity hover:opacity-70
                                    `}
                                >
                                    {chip.label}
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                <Secao
                    titulo="Produtos"
                    campo="produtos"
                    opcoes={produtos.map((p) => ({ valor: p.nome, label: p.nome }))}
                    selecionados={rascunho.produtos}
                    onToggle={alternar}
                    vazio={carregando ? "Carregando produtos..." : "Nenhum produto disponível."}
                />

                <Secao
                    titulo="Segmentos"
                    campo="segmentos"
                    opcoes={segmentos.map((s) => ({ valor: s.nome, label: s.nome }))}
                    selecionados={rascunho.segmentos}
                    onToggle={alternar}
                    vazio={carregando ? "Carregando segmentos..." : "Nenhum segmento disponível."}
                />

                <Secao
                    titulo="Risco de cancelamento"
                    campo="riscos"
                    opcoes={RISCOS}
                    selecionados={rascunho.riscos}
                    onToggle={alternar}
                />

                <Secao
                    titulo="Sentimento"
                    campo="sentimentos"
                    opcoes={SENTIMENTOS}
                    selecionados={rascunho.sentimentos}
                    onToggle={alternar}
                />

                <p className="text-xs text-secondary-text opacity-60">
                    Fechar o modal (clicar fora, Esc ou X) já aplica o que estiver selecionado.
                </p>

                <div className="-mt-2 flex items-center justify-between gap-2">
                    <button
                        type="button"
                        onClick={limpar}
                        disabled={nadaParaLimpar}
                        className={`
                            rounded px-4 py-2 text-sm font-medium text-secondary-text
                            transition-opacity hover:opacity-70
                            disabled:cursor-not-allowed disabled:opacity-40
                        `}
                    >
                        Limpar filtros
                    </button>
                    <button
                        type="button"
                        onClick={fecharAplicando}
                        className={`
                            rounded bg-primary-text px-4 py-2 text-sm font-medium
                            text-primary-bg-card-color transition-opacity hover:opacity-90
                        `}
                    >
                        Filtrar
                    </button>
                </div>
            </div>
        </Modal>
    );
}

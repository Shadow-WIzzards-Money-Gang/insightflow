"use client";

import Modal from "@/components/ui/Modal";

export default function ExportarModal({ onClose, onEscolher, baixandoCsv = false }) {
    return (
        <Modal title="Exportar" onClose={onClose}>
            <div className="flex flex-col gap-3">
                <p className="text-sm text-secondary-text opacity-70">
                    Escolha o formato. Os dois consideram os filtros aplicados.
                </p>

                <button
                    type="button"
                    onClick={() => onEscolher("pdf")}
                    disabled={baixandoCsv}
                    className={`
                        flex flex-col gap-1 rounded-lg border-2 border-secondary-bg-color
                        bg-primary-bg-card-color p-4 text-left
                        transition-opacity hover:opacity-80
                        disabled:cursor-not-allowed disabled:opacity-50
                    `}
                >
                    <span className="font-bold text-secondary-text">PDF</span>
                    <span className="text-xs text-secondary-text opacity-60">
                        Relatório visual com indicadores e gráficos. Abre as opções de
                        configuração do PDF.
                    </span>
                </button>

                <button
                    type="button"
                    onClick={() => onEscolher("csv")}
                    disabled={baixandoCsv}
                    className={`
                        flex flex-col gap-1 rounded-lg border-2 border-secondary-bg-color
                        bg-primary-bg-card-color p-4 text-left
                        transition-opacity hover:opacity-80
                        disabled:cursor-not-allowed disabled:opacity-50
                    `}
                >
                    <span className="font-bold text-secondary-text">
                        {baixandoCsv ? "Baixando CSV..." : "CSV"}
                    </span>
                    <span className="text-xs text-secondary-text opacity-60">
                        Planilha com todas as análises filtradas (assunto, sentimento,
                        risco, pontos, transcrição, etc.).
                    </span>
                </button>
            </div>
        </Modal>
    );
}

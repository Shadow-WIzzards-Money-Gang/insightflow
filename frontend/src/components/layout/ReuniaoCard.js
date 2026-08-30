const SENTIMENTO_ESTILOS = {
    POSITIVO: "bg-tertiary-bg-card-color text-success-color border-success-color",
    NEUTRO: "bg-fourth-bg-card-color text-warning-color border-warning-color",
    NEGATIVO: "bg-secondary-bg-card-color text-error-color border-error-color",
};

const SENTIMENTO_LABEL = {
    POSITIVO: "Positivo",
    NEUTRO: "Neutro",
    NEGATIVO: "Negativo",
};

const RISCO_LABEL = {
    MUITO_ALTO: "Muito alto",
    ALTO: "Alto",
    MODERADO: "Moderado",
    BAIXO: "Baixo",
};

function formatarData(iso) {
    if (!iso) return "—";
    const data = new Date(iso);
    return Number.isNaN(data.getTime()) ? "—" : data.toLocaleDateString("pt-BR");
}

export default function ReuniaoCard({ id, data, assunto, sentimento, risco, score, onClick }) {
    const estilo = SENTIMENTO_ESTILOS[sentimento] ?? SENTIMENTO_ESTILOS.NEUTRO;

    return (
        <div
            onClick={onClick}
            className={`
                ${estilo}
                border-4 rounded-lg w-full h-fit py-3 px-4 cursor-pointer
                flex flex-row justify-between items-center gap-4
                transition-opacity hover:opacity-90
            `}
        >
            <span className="font-bold whitespace-nowrap">
                Reunião #{id}
            </span>

            <span className="flex-1 text-secondary-text truncate" title={assunto}>
                {assunto}
            </span>

            <span className="text-secondary-text whitespace-nowrap w-24 text-center">
                {formatarData(data)}
            </span>

            <span className="font-semibold whitespace-nowrap w-20 text-center">
                {SENTIMENTO_LABEL[sentimento] ?? sentimento ?? "—"}
            </span>

            <span className="text-secondary-text whitespace-nowrap w-24 text-center">
                {RISCO_LABEL[risco] ?? risco ?? "—"}
            </span>

            <span className="font-bold whitespace-nowrap w-10 text-right">
                {score ?? "—"}
            </span>
        </div>
    );
}

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
                flex flex-col gap-2
                sm:flex-row sm:items-center sm:justify-between sm:gap-4
                transition-opacity hover:opacity-90
            `}
        >
            {/* No mobile: nº da reunião + score na mesma linha. No desktop: some (contents). */}
            <div className="flex items-center justify-between gap-2 sm:contents">
                <span className="font-bold whitespace-nowrap">Reunião #{id}</span>
                <span className="font-bold whitespace-nowrap sm:hidden">
                    {score ?? "—"}
                </span>
            </div>

            <span
                className="truncate text-sm text-secondary-text sm:flex-1 sm:text-base"
                title={assunto}
            >
                {assunto}
            </span>

            {/* No mobile: metadados agrupados e com rótulo. No desktop: colunas alinhadas. */}
            <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm sm:contents">
                <span className="text-secondary-text whitespace-nowrap sm:w-24 sm:text-center">
                    <span className="opacity-60 sm:hidden">Data: </span>
                    {formatarData(data)}
                </span>

                <span className="font-semibold whitespace-nowrap sm:w-20 sm:text-center">
                    {SENTIMENTO_LABEL[sentimento] ?? sentimento ?? "—"}
                </span>

                <span className="text-secondary-text whitespace-nowrap sm:w-24 sm:text-center">
                    <span className="opacity-60 sm:hidden">Risco: </span>
                    {RISCO_LABEL[risco] ?? risco ?? "—"}
                </span>

                <span className="hidden font-bold whitespace-nowrap sm:block sm:w-10 sm:text-right">
                    {score ?? "—"}
                </span>
            </div>
        </div>
    );
}

export default function Loading({ label = "Carregando..." }) {
    return (
        <div className="flex flex-col items-center justify-center gap-3 py-16 text-secondary-text">
            <div
                className="h-10 w-10 animate-spin rounded-full border-4 border-primary-bg-card-color border-t-primary-text"
                role="status"
                aria-label={label}
            />
            <span className="text-sm">{label}</span>
        </div>
    );
}

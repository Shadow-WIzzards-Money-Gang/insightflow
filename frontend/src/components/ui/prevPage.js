export default function PrevPage({ onClick, disabled }) {
    return (
        <button
            type="button"
            onClick={onClick}
            disabled={disabled}
            aria-label="Página anterior"
            className={`
                bg-primary-bg-color text-secondary-text flex justify-center items-center
                border-2 border-secondary-bg-color rounded-lg w-8 h-8 cursor-pointer
                transition-opacity hover:opacity-80
                disabled:opacity-40 disabled:cursor-not-allowed
            `}
        >
            <span>{"<"}</span>
        </button>
    );
}

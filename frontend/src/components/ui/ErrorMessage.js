"use client";

export default function ErrorMessage({ message, onRetry }) {
    return (
        <div
            className={`
                flex flex-col items-center justify-center gap-3 py-16 px-6
                border-2 border-error-color bg-secondary-bg-card-color rounded-lg text-center
            `}
            role="alert"
        >
            <span className="text-error-color font-bold text-lg">
                Erro ao comunicar com a API
            </span>
            <p className="text-secondary-text text-sm max-w-md">
                {message}
            </p>
            {onRetry && (
                <button
                    onClick={onRetry}
                    className={`
                        mt-2 py-2 px-4 rounded font-medium text-sm cursor-pointer
                        bg-error-color text-secondary-text
                        transition-opacity hover:opacity-90
                    `}
                >
                    Tentar novamente
                </button>
            )}
        </div>
    );
}

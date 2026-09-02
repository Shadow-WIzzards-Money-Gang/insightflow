"use client";

import { useCallback, useEffect } from "react";

export default function Modal({ title, onClose, disableClose = false, children }) {
    const fechar = useCallback(() => {
        if (disableClose) return;
        onClose?.();
    }, [disableClose, onClose]);

    // Bloqueia o scroll do site enquanto o modal estiver aberto
    useEffect(() => {
        const overflowOriginal = document.body.style.overflow;
        document.body.style.overflow = "hidden";
        return () => {
            document.body.style.overflow = overflowOriginal;
        };
    }, []);

    // Fecha com a tecla Escape
    useEffect(() => {
        const onKeyDown = (e) => {
            if (e.key === "Escape") fechar();
        };
        window.addEventListener("keydown", onKeyDown);
        return () => window.removeEventListener("keydown", onKeyDown);
    }, [fechar]);

    return (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center p-4"
            role="dialog"
            aria-modal="true"
            aria-label={typeof title === "string" ? title : undefined}
        >
            <div
                className="absolute inset-0 bg-black/60 backdrop-blur-sm"
                onClick={fechar}
                aria-hidden="true"
            />

            <div
                className={`
                    relative z-10 flex max-h-[90vh] w-full max-w-2xl flex-col
                    overflow-hidden rounded-lg border-2 border-primary-text
                    bg-primary-bg-color shadow-2xl
                `}
            >
                <div className="flex items-center justify-between border-b border-secondary-bg-color px-5 py-4">
                    <h2 className="text-xl font-bold text-secondary-text">{title}</h2>
                    <button
                        type="button"
                        onClick={fechar}
                        disabled={disableClose}
                        aria-label="Fechar"
                        className={`
                            flex h-8 w-8 items-center justify-center rounded text-2xl leading-none
                            text-secondary-text transition-opacity hover:opacity-70
                            disabled:cursor-not-allowed disabled:opacity-40
                        `}
                    >
                        &times;
                    </button>
                </div>

                <div className="flex flex-col gap-4 overflow-y-auto px-5 py-5">
                    {children}
                </div>
            </div>
        </div>
    );
}

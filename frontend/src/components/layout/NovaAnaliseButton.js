"use client";

import { useState } from "react";

import Button from "@/components/ui/Button";
import NovaAnaliseModal from "@/components/layout/NovaAnaliseModal";

export default function NovaAnaliseButton() {
    const [aberto, setAberto] = useState(false);

    return (
        <>
            <Button label="Nova Análise" handleClick={() => setAberto(true)} />
            {aberto && (
                <NovaAnaliseModal
                    onClose={() => setAberto(false)}
                    onAnalisado={(novaAnalise) => {
                        window.dispatchEvent(
                            new CustomEvent("analise:criada", {
                                detail: { analise: novaAnalise },
                            })
                        );
                    }}
                />
            )}
        </>
    );
}

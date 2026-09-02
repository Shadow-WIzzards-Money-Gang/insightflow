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
                    onAnalisado={() => {
                        window.dispatchEvent(new Event("analise:criada"));
                    }}
                />
            )}
        </>
    );
}

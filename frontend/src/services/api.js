const API_BASE_URL = "http://localhost:8080";

async function request(path, options) {
    let response;

    try {
        response = await fetch(`${API_BASE_URL}${path}`, options);
    } catch {
        throw new Error("Não foi possível conectar à API.");
    }

    if (!response.ok) {
        let mensagem = `A API respondeu com erro (HTTP ${response.status}).`;

        try {
            const corpo = await response.json();
            if (Array.isArray(corpo?.messages) && corpo.messages.length > 0) {
                mensagem = corpo.messages.join(" ");
            } else if (typeof corpo?.message === "string" && corpo.message) {
                mensagem = corpo.message;
            }
        } catch {
            // corpo sem json, mantém mensagem padrão
        }

        throw new Error(mensagem);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

export const getAnalisesReuniao = async (page = 0, size = 10, filtros = {}) => {
    const params = new URLSearchParams();
    params.set("page", String(page));
    params.set("size", String(size));

    for (const produto of filtros.produtos ?? []) params.append("produtos", produto);
    for (const segmento of filtros.segmentos ?? []) params.append("segmentos", segmento);
    for (const risco of filtros.riscos ?? []) params.append("riscos", risco);
    for (const sentimento of filtros.sentimentos ?? []) params.append("sentimentos", sentimento);

    return request(`/api/analises?${params.toString()}`);
};

export const getAnaliseReuniaoById = async (id) => {
    return request(`/api/analises/${id}`);
};

// Percorre todas as páginas e devolve todas as análises que batem com os filtros.
// Usado na exportação em PDF (a tela em si continua paginada).
export const getAnalisesReuniaoTodas = async (filtros = {}) => {
    const TAMANHO = 200;
    const todas = [];
    let pagina = 0;
    let totalPaginas = 1;

    do {
        const dados = await getAnalisesReuniao(pagina, TAMANHO, filtros);
        const conteudo = dados.analises?.content ?? [];
        todas.push(...conteudo);
        totalPaginas = dados.analises?.totalPages ?? 1;
        pagina += 1;
        if (conteudo.length === 0) break;
    } while (pagina < totalPaginas);

    return todas;
};

export const getSegmentosClientes = async () => {
    return request(`/api/segmentos`);
};

export const getProdutosTotvs = async () => {
    return request(`/api/produtos`);
};

export const analisarReuniao = async (transcricao, data, duracao, segmento) => {
    const reuniao = {
        transcricaoBruta: transcricao,
        dataReuniao: data,
        duracao: duracao,
        segmentoClienteId: segmento,
    };

    return request(`/api/analises`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reuniao),
    });
};

const API_BASE_URL = "http://localhost:8080";

async function request(path) {
    let response;

    try {
        response = await fetch(`${API_BASE_URL}${path}`);
    } catch {
        throw new Error("Não foi possível conectar à API.");
    }

    if (!response.ok) {
        throw new Error(`A API respondeu com erro (HTTP ${response.status}).`);
    }

    return response.json();
}

export const getAnalisesReuniao = async (page = 0, size = 10) => {
    return request(`/api/analises?size=${size}&page=${page}`);
};

export const getAnaliseReuniaoById = async (id) => {
    return request(`/api/analises/${id}`);
};

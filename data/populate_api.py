"""
Popula a API do InsightFlow a partir de reunioes.json.

Melhorias em relacao a versao antiga:
- aceita 2xx (o endpoint responde 201, nao 200);
- nunca deixa uma linha derrubar o script inteiro (try/except por item);
- ate 2 tentativas em 429/5xx e erros de conexao, sem delay entre itens;
- timeout nas requisicoes (a chamada da IA e lenta);
- checkpoint em .populate_state.json -> pode parar (Ctrl+C) e retomar de onde parou;
- resolve os segmentos uma unica vez e mapeia nomes divergentes.

Uso:
    python populate_api.py
    python populate_api.py --only-failed      # reprocessa so os que falharam
"""

import argparse
import json
import sys
import time
import unicodedata
from pathlib import Path

import requests

BASE_URL = "http://localhost:8080/api"
API_ANALISE = f"{BASE_URL}/analises"
API_SEGMENTOS = f"{BASE_URL}/segmentos"

ARQUIVO_REUNIOES = Path(__file__).parent / "reunioes.json"
ARQUIVO_STATE = Path(__file__).parent / ".populate_state.json"

# (connect timeout, read timeout) - a analise da IA pode levar minutos
TIMEOUT = (10, 300)
MAX_RETRIES = 2
BACKOFF = 2       # segundos entre tentativas
BACKOFF_MAX = 10  # teto de espera mesmo com Retry-After alto

# Nomes de segmento no JSON que nao batem exatamente com o cadastro do banco.
OVERRIDE_SEGMENTOS = {
    "CONSTRUCAO E PROJETOS": "Contrucao e Projetos",  # typo na migration V2
}


def normalizar(texto: str) -> str:
    texto = unicodedata.normalize("NFD", texto or "")
    texto = "".join(c for c in texto if unicodedata.category(c) != "Mn")
    return texto.strip().lower()


def carregar_state() -> dict:
    if ARQUIVO_STATE.exists():
        return json.loads(ARQUIVO_STATE.read_text(encoding="utf-8"))
    return {"done": [], "failed": {}}


def salvar_state(state: dict) -> None:
    ARQUIVO_STATE.write_text(json.dumps(state, ensure_ascii=False, indent=2), encoding="utf-8")


def carregar_mapa_segmentos() -> dict:
    resp = requests.get(API_SEGMENTOS, timeout=TIMEOUT)
    resp.raise_for_status()
    mapa = {normalizar(s["nome"]): s["id"] for s in resp.json()}
    for origem, destino in OVERRIDE_SEGMENTOS.items():
        if normalizar(destino) in mapa:
            mapa[normalizar(origem)] = mapa[normalizar(destino)]
    return mapa


def normalizar_duracao(valor: str) -> str:
    partes = (valor or "").split(":")
    if len(partes) == 3:
        h, m, s = partes
        return f"{int(h):02d}:{int(m):02d}:{int(s):02d}"
    return valor


def montar_request(reuniao: dict, mapa_segmentos: dict) -> dict | None:
    segmento_id = mapa_segmentos.get(normalizar(reuniao.get("NOME_SEGMENTO", "")))
    if segmento_id is None:
        return None
    return {
        "transcricaoBruta": reuniao["ANON_TRANSCRICAO"],
        "dataReuniao": reuniao["DT_MEETING"].replace(" ", "T"),
        "duracao": normalizar_duracao(reuniao["DURACAO_MEETING"]),
        "segmentoClienteId": segmento_id,
    }


def enviar_analise(payload: dict) -> tuple[bool, str]:
    """Retorna (sucesso, detalhe). Faz retry em 429/5xx e erros de conexao."""
    for tentativa in range(1, MAX_RETRIES + 1):
        try:
            resp = requests.post(API_ANALISE, json=payload, timeout=TIMEOUT)
        except (requests.ConnectionError, requests.Timeout) as e:
            if tentativa == MAX_RETRIES:
                return False, e.__class__.__name__
            time.sleep(BACKOFF)
            continue

        if resp.ok:  # 2xx
            return True, f"HTTP {resp.status_code}"

        if resp.status_code in (429, 500, 502, 503, 504) and tentativa < MAX_RETRIES:
            espera = min(int(resp.headers.get("Retry-After", BACKOFF)), BACKOFF_MAX)
            print(f"  HTTP {resp.status_code}; retry em {espera}s ({tentativa}/{MAX_RETRIES})")
            time.sleep(espera)
            continue

        # 4xx (exceto 429) ou estourou as tentativas -> falha definitiva
        return False, f"HTTP {resp.status_code}: {resp.text[:300]}"

    return False, "esgotou as tentativas"


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--only-failed", action="store_true",
                        help="reprocessa apenas os IDs que falharam antes")
    args = parser.parse_args()

    reunioes = json.loads(ARQUIVO_REUNIOES.read_text(encoding="utf-8"))
    state = carregar_state()
    done = set(state["done"])
    failed = dict(state["failed"])

    try:
        mapa_segmentos = carregar_mapa_segmentos()
    except requests.RequestException as e:
        sys.exit(f"Nao consegui carregar os segmentos ({e}). A API esta de pe?")

    if args.only_failed:
        alvos = [r for r in reunioes if str(r["ID_MEETING"]) in failed]
    else:
        alvos = [r for r in reunioes if str(r["ID_MEETING"]) not in done]

    print(f"{len(reunioes)} reunioes no arquivo | {len(done)} ja processadas | "
          f"{len(alvos)} nesta rodada")

    try:
        for i, reuniao in enumerate(alvos, start=1):
            rid = str(reuniao["ID_MEETING"])
            payload = montar_request(reuniao, mapa_segmentos)

            if payload is None:
                motivo = f"segmento nao encontrado: {reuniao.get('NOME_SEGMENTO')!r}"
                print(f"[{i}/{len(alvos)}] {rid} SKIP - {motivo}")
                failed[rid] = motivo
                continue

            print(f"[{i}/{len(alvos)}] {rid} enviando "
                  f"({len(payload['transcricaoBruta'])} chars)...")
            sucesso, detalhe = enviar_analise(payload)

            if sucesso:
                done.add(rid)
                failed.pop(rid, None)
                print(f"    OK ({detalhe})")
            else:
                failed[rid] = detalhe
                print(f"    FALHOU: {detalhe}")

            if i % 10 == 0:
                salvar_state({"done": sorted(done), "failed": failed})
    except KeyboardInterrupt:
        print("\nInterrompido. Salvando progresso...")
    finally:
        salvar_state({"done": sorted(done), "failed": failed})

    print(f"\nResumo: {len(done)} ok | {len(failed)} com falha")
    if failed:
        print("Falhas (use --only-failed para retentar):")
        for rid, motivo in list(failed.items())[:20]:
            print(f"  {rid}: {motivo}")


if __name__ == "__main__":
    main()

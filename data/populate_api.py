import json
import requests
import time

API_URL_ANALISE = "http://localhost:8080/api/analises"
API_URL_SEGMENTO = "http://localhost:8080/api/segmentos"

def analisar(reuniao: dict) -> dict:
    print("Enviando:")
    print(json.dumps(reuniao, indent=2, ensure_ascii=False))

    response = requests.post(API_URL_ANALISE, json=reuniao)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Error: {response.status_code} - {response.text} | Response: {response} - {response.json()}")
        return None

def buscar_segmento_id(segmento: str) -> int:
    response = requests.get(f"{API_URL_SEGMENTO}/{segmento}")
    if response.status_code == 200:
        return response.json()['id']
    else:
        print(f"Error: {response.status_code} - {response.text}")
        return None

def montar_request(reuniao: dict) -> dict:
    segmento_id = buscar_segmento_id(reuniao['NOME_SEGMENTO'])
    
    return {
        'transcricaoBruta': reuniao['ANON_TRANSCRICAO'],
        'dataReuniao': reuniao['DT_MEETING'].replace(" ", "T"),
        'duracao': reuniao['DURACAO_MEETING'],
        'segmentoClienteId': segmento_id
    }

with open("reunioes.json", "r", encoding="utf-8") as f:
    reunioes = json.load(f)

contagem = 0

for reuniao in reunioes:

    reuniao_request = montar_request(reuniao)

    analise = analisar(reuniao_request)
    if analise:
        print(analise)

    contagem+=1
    print(f"Reuniao #{contagem}")

    time.sleep(1)

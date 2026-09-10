# InsightFlow

Plataforma que transforma **transcrições brutas de reuniões** comerciais e de suporte da
TOTVS em **análises estruturadas e acionáveis** para times de Customer Success, vendas e
suporte.

Cada transcrição é enviada a um modelo de IA que extrai assunto, sentimento do cliente,
risco de cancelamento (churn), motivo do risco, produto TOTVS em discussão, pontos
positivos/negativos e uma nota de 0 a 10. Os resultados alimentam um **dashboard** com
indicadores, gráficos e filtros, e podem ser exportados em **PDF** (relatório visual) ou
**CSV** (planilha completa).

---

## Objetivo

- Eliminar a leitura manual de dezenas de transcrições longas.
- Dar visibilidade rápida sobre **quais clientes, produtos e segmentos estão em risco**.
- Padronizar a análise (mesmos critérios de sentimento, risco e nota para todas as reuniões).
- Permitir recortes por produto / segmento / risco / sentimento e levar essa visão para
  fora da ferramenta (PDF para apresentar, CSV para cruzar com outros dados).

---

## Arquitetura

Monorepo com três partes:

| Pasta            | Papel                                                                 |
|------------------|----------------------------------------------------------------------|
| `backend/`       | API REST (Spring Boot) + integração com IA + persistência (Oracle)   |
| `frontend/`      | Dashboard web (Next.js)                                              |
| `data/`          | Script Python para popular a API a partir de `reunioes.json`          |
| `backend/docker/`| `docker-compose` do banco Oracle para desenvolvimento               |

O backend segue arquitetura em camadas: `presentation` (controllers) → `application`
(services / DTOs) → `domain` (entidades e enums) → repositórios (Spring Data JPA +
Specifications para os filtros). A listagem e as métricas do dashboard usam **projeções
DTO e agregação no banco** (Criteria API): a página vem numa única query, as métricas
(`COUNT` / `AVG` / `GROUP BY`) são calculadas no Oracle — sem N+1 e sem carregar as
transcrições na memória.

---

## Tecnologias

### Backend
- **Java 21**, **Spring Boot 4.1**
- Spring Web MVC, Spring Data JPA, Bean Validation
- **Spring AI 2.0** (`spring-ai-starter-model-openai`) apontando para a **API da Groq**
  (`https://api.groq.com/openai/v1`), modelo `openai/gpt-oss-120b`
- **Oracle Database** (driver `ojdbc11`; imagem `gvenzl/oracle-free` em dev)
- **Flyway** para versionamento do schema (`V1__create_tables.sql`, `V2__insert_into.sql`)
- **Apache Commons CSV** (geração do CSV de exportação)
- Lombok
- `springboot4-dotenv` (carrega variáveis de um arquivo `.env`)
- Maven (wrapper `mvnw` incluído)

### Frontend
- **Next.js 16** (App Router, Turbopack) + **React 19**
- **Tailwind CSS v4** — layout responsivo (desktop e mobile)
- **Chart.js 4** + `react-chartjs-2` (gráficos de pizza e de barra empilhada)
- **jsPDF** + `jspdf-autotable` (geração do relatório em PDF no cliente)
- `react-hot-toast` (notificações)
- Cache próprio (memória + `sessionStorage`) com _stale-while-revalidate_ para a lista e
  as análises já abertas

### Carga de dados
- **Python 3** + `requests`

---

## Funcionalidades

- **Nova análise:** cola-se a transcrição bruta + data/hora + duração + segmento do
  cliente; a IA analisa e o resultado é persistido. Transcrições duplicadas são
  detectadas por hash.
- **Dashboard – indicadores:** reuniões analisadas, risco de churn (alto + muito alto),
  sentimento médio, score médio, produto mais crítico e segmento mais crítico.
- **Dashboard – gráficos:**
  - Sentimento das reuniões (pizza)
  - Risco de churn (pizza)
  - Risco de churn por produto (barra empilhada)
  - Risco de churn por segmento (barra empilhada)
- **Filtros** por produto, segmento, risco e sentimento — refletem simultaneamente nos
  indicadores, gráficos e na lista. Enquanto a resposta não chega, a tela mostra um
  _loading_ sem descartar os dados anteriores.
- **Lista paginada de análises** com modal de detalhes (transcrição, pontos
  positivos/negativos, motivo do risco, etc.).
- **Exportar** (botão único que abre um modal para escolher o formato — os dois respeitam
  os filtros aplicados):
  - **PDF:** relatório visual; um passo de configuração permite escolher **quais gráficos**
    entram e se a **lista de análises** deve ser incluída (tabela completa das análises
    filtradas + resumo do motivo para as de risco alto/muito alto).
  - **CSV:** planilha (`;` + UTF-8 com BOM, pronta para o Excel pt-BR) com **todas** as
    análises filtradas e todas as colunas — id, assunto, pontos +/-, nota, sentimento,
    risco, motivo, produto, segmento, data, duração, hash e transcrição.
- **Cache no cliente:** voltar a uma página/filtro já visto (ou reabrir uma análise) é
  instantâneo — o dado em cache aparece na hora e é revalidado em segundo plano.
- **Layout responsivo** (desktop e mobile).

---

## API (principais rotas)

Base: `http://localhost:8080`

| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/api/analises` | Cria uma análise a partir de uma transcrição (chama a IA) |
| `GET`  | `/api/analises` | Lista paginada de análises + métricas agregadas. Query params: `page`, `size`, `produtos`, `segmentos`, `riscos`, `sentimentos` |
| `GET`  | `/api/analises/export` | Baixa um **CSV** com todas as análises que batem com os filtros (mesmos query params da listagem, sem `page`/`size`) |
| `GET`  | `/api/analises/{id}` | Detalhe de uma análise |
| `GET`  | `/api/reunioes`, `/api/reunioes/{id}` | Reuniões |
| `GET`  | `/api/produtos` | Produtos TOTVS (aceita `?categoria=`) |
| `GET`  | `/api/segmentos` | Segmentos de cliente |

---

## Modelo de dados

`segmentos_clientes` → `reunioes` (transcrição, data, duração) → `analises_reuniao`
(resultado da IA) → `produtos_totvs`.

Enums: `SentimentoReuniao` (`POSITIVO`, `NEUTRO`, `NEGATIVO`) e `RiscoCancelamento`
(`MUITO_ALTO`, `ALTO`, `MODERADO`, `BAIXO`).

Produtos e segmentos são pré-carregados pela migration `V2`.

---

## Como executar

### Pré-requisitos
- **Java 21**
- **Node.js 20+** e npm
- **Docker** (para subir o banco Oracle) — ou uma instância Oracle própria
- Uma **chave de API da Groq** (https://console.groq.com) para o `IA_API_KEY`
- Python 3 (opcional, apenas para a carga em massa)

### 1. Banco de dados (Docker)

```bash
cd backend/docker
docker compose up -d
```

Sobe um Oracle Free em `localhost:1521`, database `FREEPDB1`, usuário `app` / senha
`app123` (valores que já são o padrão do backend).

### 2. Backend

```bash
cd backend
cp .env.example .env
```

Edite o `.env`. Usando o banco do Docker acima, basta a chave da IA (as credenciais de
banco já têm esses mesmos valores como padrão no `application.properties`):

```dotenv
IA_API_KEY=coloque_sua_chave_groq_aqui

DB_HOST=localhost
DB_PORT=1521
DB_NAME=FREEPDB1
DB_USERNAME=app
DB_PASSWORD=app123
```

> Preencha **todas** as variáveis presentes no arquivo (uma variável declarada vazia
> sobrescreve o valor padrão). Para apontar a um Oracle próprio, ajuste os `DB_*`.

Rode a aplicação (o Flyway aplica as migrations automaticamente):

```bash
./mvnw spring-boot:run       # Linux/macOS
mvnw.cmd spring-boot:run     # Windows
```

API disponível em `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Dashboard em `http://localhost:3000`. O endereço da API está em
`frontend/src/services/api.js` (`API_BASE_URL`), apontando para `http://localhost:8080`.

Build de produção:

```bash
npm run build
npm run start
```

### 4. Popular a base (opcional)

Com a API de pé:

```bash
cd data
pip install requests
python populate_api.py                # processa reunioes.json
python populate_api.py --only-failed   # reprocessa apenas os que falharam
```

O script chama a IA para cada reunião (é lento) e grava um checkpoint em
`.populate_state.json` — pode ser interrompido com `Ctrl+C` e retomado.

---

## Estrutura do repositório

```
insightflow/
├── backend/
│   ├── src/main/java/br/com/bytestorm/insightflow/
│   │   ├── presentation/controller/   # endpoints REST
│   │   ├── application/                # services, DTOs
│   │   ├── domain/                     # entidades, enums, exceções
│   │   └── infra/                      # repositórios, IA, config
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── db/migration/               # Flyway
│   │   └── ai/prompts/                 # prompt de sistema da análise
│   ├── docker/docker-compose.yml       # Oracle para dev
│   └── .env.example
├── frontend/
│   └── src/
│       ├── app/                        # rotas (App Router)
│       ├── components/                 # charts, layout, ui
│       ├── context/                    # FiltrosProvider
│       ├── lib/relatorio/              # geração do PDF
│       ├── lib/cache/                  # cache stale-while-revalidate das análises
│       └── services/api.js
└── data/
    ├── populate_api.py
    └── reunioes.json
```

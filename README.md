# InsightFlow - Analisador Inteligente de Reuniões 🚀

O **InsightFlow** é uma solução avançada para análise de reuniões comerciais, projetada para transformar transcrições brutas em insights estratégicos. Utilizando Inteligência Artificial de ponta (LLaMA 3.3 via Groq API), o sistema identifica automaticamente o contexto, o sentimento e o risco de cada interação com o cliente.

## 👥 Integrantes do Grupo

- Antônio Neto (RM: 561777)
- Felipe Mendes (RM: 563524)
- Mauro Carlos (RM: 556645)

---

## 📋 Funcionalidades

- **Análise Automática**: Extração de insights a partir de textos de transcrição.
- **Detecção de Sentimento**: Classifica a reunião como POSITIVO, NEUTRO ou NEGATIVO.
- **Análise de Risco (Churn)**: Avalia a probabilidade de cancelamento ou perda do cliente.
- **Identificação de Segmento**: Categoriza o cliente (Saúde, Agronegócio, Varejo, etc.).
- **Mapeamento de Produtos**: Identifica quais soluções TOTVS foram mencionadas ou são relevantes.
- **Interface Interativa**: Menu CLI amigável para navegação e operações.

## 📄 Transcrições de Teste

Para facilitar o teste da aplicação, disponibilizamos um conjunto de transcrições reais (anonimizadas) que podem ser utilizadas como entrada.
- **Localização**: `docs/transcricoes.json`
- **Uso**: Você pode copiar o conteúdo destas transcrições e colá-lo na aplicação quando solicitado.


## 🛠️ Tecnologias Utilizadas

- **Java 21**: Linguagem base do projeto.
- **Maven**: Gerenciamento de dependências e automação de build.
- **Groq API (AI)**: Motor de processamento de linguagem natural (LLM).
- **OkHttp/Jackson**: Comunicação com APIs externas e manipulação de JSON.
- **Arquitetura DDD**: Foco em Domain-Driven Design para um código limpo e manutenível.

## 🚀 Como Executar o Projeto

### Pré-requisitos
- JDK 21 ou superior instalado.
- Maven 3.6+ instalado.

### Passo 1: Configurar a Chave da Groq
O projeto necessita de uma chave de API para funcionar. Siga as instruções na seção [Como conseguir a chave da Groq](#-como-conseguir-a-chave-da-groq).

### Passo 2: Clonar e Compilar
```bash
# Clone o repositório
git clone https://github.com/Shadow-WIzzards-Money-Gang/insightflow.git

# Entre no diretório
cd insightflow

# Compile o projeto
mvn clean install
```

### Passo 3: Executar a Aplicação
Você pode executar o projeto diretamente via Maven:
```bash
mvn exec:java -Dexec.mainClass="com.bytestorm.insightflow.Main"
```
Ou executando o JAR gerado:
```bash
java -jar target/insightflow-1.0-SNAPSHOT.jar
```

## 🔑 Como conseguir a chave da Groq

1. Acesse o [Groq Console](https://console.groq.com/).
2. Crie uma conta ou faça login.
3. No menu lateral, clique em **API Keys**.
4. Clique no botão **Create API Key**.
5. Dê um nome para sua chave (ex: `InsightFlow-Key`).
6. **Copie a chave gerada imediatamente**, pois ela não será exibida novamente.
7. Abra o arquivo `src/main/resources/application.properties` no seu editor favorito.
8. Localize a linha `ai.api.key=` e cole sua chave após o sinal de igual:
   ```properties
   ai.api.key=SUA_CHAVE_AQUI
   ```

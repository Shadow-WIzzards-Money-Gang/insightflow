package br.com.bytestorm.insightflow.application.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class TranscricaoCleaner {

    /** Ex.: "[00:12]", "(01:03:44)" - marcacoes de tempo de transcricao. */
    private static final Pattern MARCACAO_TEMPO =
        Pattern.compile("[\\[(]\\s*\\d{1,2}:\\d{2}(?::\\d{2})?\\s*[\\])]");

    /** Vicios de linguagem e hesitacoes tipicos de fala transcrita. */
    private static final Pattern VICIOS_LINGUAGEM = Pattern.compile(
        "\\b(?:"
            + "n[eé]|"                                  // "ne", "né"
            + "aham|uhum|ahan|aha|"                     // backchannels
            + "ha|h[ãa]|ahn|ahm|hum+|mm+|hmm+|"         // hesitacoes ("ha" != "há")
            + "e+h|é+h?|a+h|o+h|u+h|ã+|"                // "ééé", "aaah", "ãã"
            + "tipo assim|ent[ãa]o assim"               // muletas de frase
            + ")\\b",
        Pattern.UNICODE_CHARACTER_CLASS);

    /**
     * Stopwords conservadoras: apenas artigos, contracoes e conectivos de
     * baixissima carga semantica. Rodam ANTES de remover acentos, entao
     * "e" (conjuncao) e alvo, mas "é" (verbo ser) nao e tocado.
     */
    private static final List<String> STOPWORDS = List.of(
        "a", "o", "as", "os",
        "um", "uma", "uns", "umas",
        "de", "da", "do", "das", "dos",
        "em", "no", "na", "nos", "nas", "num", "numa", "dum", "duma",
        "ao", "aos",
        "pelo", "pela", "pelos", "pelas",
        "para", "pra", "pro", "por", "com",
        "e", "ou", "que"
    );

    private static final Pattern STOPWORDS_PATTERN =
        Pattern.compile("\\b(?:" + String.join("|", STOPWORDS) + ")\\b", Pattern.UNICODE_CHARACTER_CLASS);

    /** Sobra apenas letras sem acento, digitos, espacos e pontuacao de frase. */
    private static final Pattern CARACTERES_RUIDO = Pattern.compile("[^a-z0-9\\s.,?!;:%\\-]");

    private static final Pattern LETRA_ESTICADA = Pattern.compile("([a-z])\\1{2,}");
    private static final Pattern PALAVRA_REPETIDA = Pattern.compile("\\b([a-z0-9]+)(?:\\s+\\1\\b)+");
    private static final Pattern PONTUACAO_REPETIDA = Pattern.compile("\\s*([.,?!;:])(?:\\s*[.,?!;:])+");
    private static final Pattern ESPACO_ANTES_PONTUACAO = Pattern.compile("\\s+([.,?!;:])");
    private static final Pattern ESPACOS = Pattern.compile("\\s+");

    public String limpar(String transcricao) {
        if (transcricao == null || transcricao.isBlank()) {
            return "";
        }

        String texto = transcricao.toLowerCase(Locale.ROOT);

        texto = MARCACAO_TEMPO.matcher(texto).replaceAll(" ");
        texto = texto.replaceAll("[\\r\\n\\t\\f\\u000B]+", " ");
        texto = VICIOS_LINGUAGEM.matcher(texto).replaceAll(" ");
        texto = STOPWORDS_PATTERN.matcher(texto).replaceAll(" ");

        texto = removerAcentos(texto);

        texto = CARACTERES_RUIDO.matcher(texto).replaceAll(" ");
        texto = LETRA_ESTICADA.matcher(texto).replaceAll("$1");
        texto = PALAVRA_REPETIDA.matcher(texto).replaceAll("$1");
        texto = PONTUACAO_REPETIDA.matcher(texto).replaceAll("$1");
        texto = ESPACO_ANTES_PONTUACAO.matcher(texto).replaceAll("$1");
        texto = ESPACOS.matcher(texto).replaceAll(" ").trim();

        // Se a limpeza agressiva zerou o texto, cai para uma versao minima.
        return texto.isBlank() ? limpezaMinima(transcricao) : texto;
    }

    private String limpezaMinima(String transcricao) {
        String texto = removerAcentos(transcricao.toLowerCase(Locale.ROOT));
        return ESPACOS.matcher(texto).replaceAll(" ").trim();
    }

    private String removerAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "");
    }
}

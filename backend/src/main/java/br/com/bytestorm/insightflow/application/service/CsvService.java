package br.com.bytestorm.insightflow.application.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.response.AnaliseCsvRow;

/**
 * Mecanica de geracao de CSV. Nao conhece o dominio: recebe o cabecalho + as
 * linhas ja como texto e devolve o conteudo do arquivo.
 *
 * Formato voltado para o Excel pt-BR: separador ';' e UTF-8 com BOM.
 */
@Service
public class CsvService {

    private static final Logger log = LoggerFactory.getLogger(CsvService.class);

    private static final char SEPARADOR = ';';
    private static final char BOM = 0xFEFF;

    public String gerar(List<String> cabecalho, List<List<String>> linhas) {
        CSVFormat formato = CSVFormat.DEFAULT.builder()
            .setDelimiter(SEPARADOR)
            .setHeader(cabecalho.toArray(String[]::new))
            .setRecordSeparator("\r\n")
            .build();

        StringWriter writer = new StringWriter();
        writer.append(BOM);

        try (CSVPrinter printer = new CSVPrinter(writer, formato)) {
            for (List<String> linha : linhas) {
                printer.printRecord(linha);
            }
        } catch (IOException e) {
            // StringWriter nao lanca IOException na pratica.
            throw new UncheckedIOException("Falha ao gerar o CSV", e);
        }

        log.info("CSV gerado - {} linha(s), {} coluna(s)", linhas.size(), cabecalho.size());
        return writer.toString();
    }

    public List<String> montarLinhaCsv(AnaliseCsvRow row) {
        return Arrays.asList(
            String.valueOf(row.id()),
            row.assunto(),
            row.pontosPositivos(),
            row.pontosNegativos(),
            String.valueOf(row.nota()),
            row.sentimentoReuniao().name(),
            row.riscoCancelamento().name(),
            row.motivoCancelamento(),
            row.produtoTotvs(),
            row.segmentoCliente(),
            row.dataReuniao() == null ? null : row.dataReuniao().toString(),
            row.duracao() == null ? null : row.duracao().toString(),
            row.hashTranscricao(),
            limparTexto(row.transcricaoBruta())
        );
    }

    private String limparTexto(String texto) {
        if (texto == null) return null;
        return texto.replaceAll("[\\r\\n]+", " ").trim();
    }
}

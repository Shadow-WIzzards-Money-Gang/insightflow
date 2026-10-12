package br.com.bytestorm.insightflow.domain.vo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Impressao digital da transcricao (MD5 em hexadecimal), usada para reconhecer reunioes repetidas. */
public record HashTranscricao(String valor) {

    public static HashTranscricao de(String transcricao) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(transcricao.getBytes(StandardCharsets.UTF_8));
            return new HashTranscricao(HexFormat.of().formatHex(digest));
        } catch (NoSuchAlgorithmException e) {
            // MD5 e obrigatorio em toda JVM.
            throw new IllegalStateException(e);
        }
    }
}

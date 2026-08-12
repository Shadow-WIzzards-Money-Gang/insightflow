package br.com.bytestorm.insightflow.application.dto.erro;

import java.util.List;

public record Error(
    Integer status,
    List<String> messages
) {
}

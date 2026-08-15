package br.com.bytestorm.insightflow.domain.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import br.com.bytestorm.insightflow.application.dto.erro.Error;
import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro400Exception;
import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro404Exception;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        Error error = new Error(HttpStatus.BAD_REQUEST.value(), messages);
        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(Erro404Exception.class)
    public ResponseEntity<Error> handleErro404(Erro404Exception ex) {
        Error error = new Error(HttpStatus.NOT_FOUND.value(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(Erro400Exception.class)
    public ResponseEntity<Error> handleErro400(Erro400Exception ex) {
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.status()).body(error);
    }
}

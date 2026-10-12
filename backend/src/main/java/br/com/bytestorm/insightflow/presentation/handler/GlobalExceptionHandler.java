package br.com.bytestorm.insightflow.presentation.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import br.com.bytestorm.insightflow.application.dto.erro.Error;
import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;
import br.com.bytestorm.insightflow.domain.exceptions.erro.NaoEncontradoException;
import br.com.bytestorm.insightflow.domain.exceptions.erro.IntegracaoInvalidaException;
import br.com.bytestorm.insightflow.domain.exceptions.erro.ServicoIndisponivelException;

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

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Error> handleNaoEncontrado(NaoEncontradoException ex) {
        Error error = new Error(HttpStatus.NOT_FOUND.value(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Error> handleRegraDeNegocio(RegraDeNegocioException ex) {
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(IntegracaoInvalidaException.class)
    public ResponseEntity<Error> handleIntegracaoInvalida(IntegracaoInvalidaException ex) {
        Error error = new Error(HttpStatus.BAD_GATEWAY.value(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(ServicoIndisponivelException.class)
    public ResponseEntity<Error> handleServicoIndisponivel(ServicoIndisponivelException ex) {
        Error error = new Error(HttpStatus.SERVICE_UNAVAILABLE.value(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.status()).body(error);
    }
}

package co.com.pragma.crediya.api.exceptions.handler;

import co.com.pragma.crediya.api.constants.HttpErrorTitles;
import co.com.pragma.crediya.api.exceptions.FieldValidationError;
import co.com.pragma.crediya.api.exceptions.JwtAuthenticationException;
import co.com.pragma.crediya.api.exceptions.ProblemDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        ProblemDetails problemDetails = createProblemDetails(ex);

        response.setStatusCode(problemDetails.getHttpStatus());

        try {
            String responseBody = objectMapper.writeValueAsString(problemDetails);
            DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes());
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.writeWith(Mono.empty());
        }
    }

    private ProblemDetails createProblemDetails(Throwable ex) {
        if (ex instanceof WebExchangeBindException bindEx) {
            return handleWebExchangeBindException(bindEx);
        }

        if (ex instanceof JwtAuthenticationException) {
            return ProblemDetails.unauthorized(HttpErrorTitles.UNAUTHORIZED, ex.getMessage());
        }

        return new ProblemDetails(HttpErrorTitles.INTERNAL_SERVER_ERROR, "An unexpected error occurred while processing your request. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    private ProblemDetails handleWebExchangeBindException(WebExchangeBindException ex) {
        List<FieldValidationError> errors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(fieldError ->
                        new FieldValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return ProblemDetails.badRequest(HttpErrorTitles.BAD_REQUEST, errors);
    }

}
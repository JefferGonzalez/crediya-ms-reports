package co.com.pragma.crediya.api.exceptions.handler;

import co.com.pragma.crediya.api.constants.AuthErrorMessages;
import co.com.pragma.crediya.api.constants.HttpErrorTitles;
import co.com.pragma.crediya.api.exceptions.ProblemDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.FORBIDDEN);

        ProblemDetails problemDetails = ProblemDetails.forbidden(HttpErrorTitles.FORBIDDEN, AuthErrorMessages.FORBIDDEN_ACCESS);

        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(problemDetails))
                .map(response.bufferFactory()::wrap)
                .flatMap(buffer -> response.writeWith(Mono.just(buffer)))
                .onErrorResume(e -> {
                    byte[] fallbackBytes = ("{\"title\":\"" + HttpErrorTitles.FORBIDDEN + "\",\"message\":\"" + AuthErrorMessages.FORBIDDEN_ACCESS + "\"}").getBytes();
                    DataBuffer buffer = response.bufferFactory().wrap(fallbackBytes);
                    return response.writeWith(Mono.just(buffer));
                });
    }

}

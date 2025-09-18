package co.com.pragma.crediya.api.config.security;

import co.com.pragma.crediya.api.constants.ApiConstants;
import co.com.pragma.crediya.api.exceptions.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager jwtAuthenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();

        if (isPublicPath(path)) {
            return Mono.empty();
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            return Mono.error(JwtAuthenticationException.missingToken());
        }

        if (!authHeader.startsWith(ApiConstants.BEARER_PREFIX)) {
            return Mono.error(JwtAuthenticationException.invalidToken());
        }

        String token = authHeader.substring(ApiConstants.BEARER_PREFIX_LENGTH);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(token, token);

        return jwtAuthenticationManager
                .authenticate(auth)
                .map(SecurityContextImpl::new);
    }

    private boolean isPublicPath(String path) {
        return Arrays.stream(ApiConstants.PUBLIC_PATTERNS)
                .anyMatch(pattern -> {
                    if (pattern.endsWith("/**")) {
                        String basePath = pattern.substring(0, pattern.length() - 3);
                        return path.startsWith(basePath);
                    }
                    return path.equals(pattern);
                });
    }

}

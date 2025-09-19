package co.com.pragma.crediya.api.config.security;

import co.com.pragma.crediya.api.exceptions.JwtAuthenticationException;
import co.com.pragma.crediya.model.jwt.gateways.JwtProviderPort;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProviderPort jwtProviderPort;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return Mono.just(authentication)
                .map(auth -> jwtProviderPort.parseToken(auth.getCredentials().toString()))
                .onErrorResume(e -> {
                    if (e instanceof ExpiredJwtException) {
                        return Mono.error(JwtAuthenticationException.expiredToken());
                    } else {
                        return Mono.error(JwtAuthenticationException.invalidToken());
                    }
                })
                .map(jwt -> new UsernamePasswordAuthenticationToken(
                        jwt.subject(),
                        token,
                        jwt.roles().stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                ));
    }

}
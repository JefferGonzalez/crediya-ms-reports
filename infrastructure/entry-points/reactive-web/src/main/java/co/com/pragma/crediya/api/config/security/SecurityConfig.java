package co.com.pragma.crediya.api.config.security;

import co.com.pragma.crediya.api.constants.ApiConstants;
import co.com.pragma.crediya.api.exceptions.handler.CustomAccessDeniedHandler;
import co.com.pragma.crediya.model.common.constants.DomainConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityContextRepository securityContextRepository;

    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchangeSpec -> exchangeSpec
                        .pathMatchers(ApiConstants.PUBLIC_PATTERNS)
                        .permitAll()
                        .pathMatchers(HttpMethod.GET, ApiConstants.BASE_PATH)
                        .hasAnyAuthority(
                                DomainConstants.ADMIN_ROLE
                        )
                        .anyExchange().authenticated()
                )
                .securityContextRepository(securityContextRepository)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }
}

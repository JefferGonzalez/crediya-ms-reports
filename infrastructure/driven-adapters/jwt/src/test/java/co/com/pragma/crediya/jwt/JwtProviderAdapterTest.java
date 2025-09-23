package co.com.pragma.crediya.jwt;

import co.com.pragma.crediya.jwt.config.JwtProperties;
import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.jwt.Jwt;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderAdapterTest {

    private JwtProviderAdapter adapter;

    private User user;

    private final JwtProperties properties = new JwtProperties("q/MbiTiaKL9wCSeISqOlOQvDjg7s+xmYRtNhYbq7T3A=", 10000L);

    @BeforeEach
    void setUp() {
        adapter = new JwtProviderAdapter(properties);

        user = new User("johndoe@example.com");
    }

    @Test
    void parseToken_shouldReturnJwt() {
        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.email())
                .claim(UserFieldNames.ROLES, List.of(DomainConstants.ADMIN_ROLE))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secretKey())))
                .compact();

        Jwt jwt = adapter.parseToken(token);

        assertThat(jwt).isNotNull();
        assertThat(jwt.subject()).isEqualTo(user.email());
        assertThat(jwt.roles()).containsExactly(DomainConstants.ADMIN_ROLE);
    }

}
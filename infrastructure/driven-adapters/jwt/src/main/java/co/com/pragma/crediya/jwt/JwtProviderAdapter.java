package co.com.pragma.crediya.jwt;

import co.com.pragma.crediya.jwt.config.JwtProperties;
import co.com.pragma.crediya.model.jwt.Jwt;
import co.com.pragma.crediya.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProviderAdapter implements JwtProviderPort {

    private final JwtProperties properties;

    @Override
    public Jwt parseToken(String token) {
        Claims claims = extractClaims(token);

        String subject = extractSubject(claims);
        List<String> roles = extractRole(claims);

        return new Jwt(subject, roles);
    }

    private SecretKey getSecretKey() {
        String secretKey = properties.secretKey();
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRole(Claims claims) {
        Object value = claims.get(UserFieldNames.ROLES);
        return value != null ? (List<String>) value : Collections.emptyList();
    }

    private String extractSubject(Claims claims) {
        return claims.getSubject();
    }

}

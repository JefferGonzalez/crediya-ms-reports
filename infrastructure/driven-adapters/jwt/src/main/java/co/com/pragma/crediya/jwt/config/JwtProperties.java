package co.com.pragma.crediya.jwt.config;

public record JwtProperties(String secretKey, long expiration) {
}

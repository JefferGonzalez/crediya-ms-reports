package co.com.pragma.crediya.model.jwt;

import java.util.List;

public record Jwt(String subject, List<String> roles) {
}

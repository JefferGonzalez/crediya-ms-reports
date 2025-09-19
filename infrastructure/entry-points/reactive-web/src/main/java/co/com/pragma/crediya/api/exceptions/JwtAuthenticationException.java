package co.com.pragma.crediya.api.exceptions;

import co.com.pragma.crediya.api.constants.AuthErrorMessages;

public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException(String message) {
        super(message);
    }

    public static JwtAuthenticationException invalidToken() {
        return new JwtAuthenticationException(AuthErrorMessages.INVALID_TOKEN);
    }

    public static JwtAuthenticationException expiredToken() {
        return new JwtAuthenticationException(AuthErrorMessages.EXPIRED_TOKEN);
    }

    public static JwtAuthenticationException missingToken() {
        return new JwtAuthenticationException(AuthErrorMessages.MISSING_TOKEN);
    }

}

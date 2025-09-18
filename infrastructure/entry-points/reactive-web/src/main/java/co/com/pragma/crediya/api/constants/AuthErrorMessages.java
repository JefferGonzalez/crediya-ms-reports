package co.com.pragma.crediya.api.constants;

public final class AuthErrorMessages {

    private AuthErrorMessages() {
    }

    public static final String INVALID_TOKEN = "Invalid JWT token. Please log in again.";

    public static final String EXPIRED_TOKEN = "JWT token has expired. Please log in again.";

    public static final String MISSING_TOKEN = "Authorization token is missing.";

    public static final String FORBIDDEN_ACCESS = "You do not have permission to access this resource.";

}

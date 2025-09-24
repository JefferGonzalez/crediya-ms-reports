package co.com.pragma.crediya.api.constants;

public final class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_V1 = "/api/v1";

    public static final String BASE_PATH = API_V1 + "/reports";

    public static final String[] PUBLIC_PATTERNS = {
            BASE_PATH + "/actuator",
            BASE_PATH + "/actuator/health",
            BASE_PATH + "/actuator/prometheus",
            BASE_PATH + "/swagger-ui.html",
            BASE_PATH + "/swagger-ui/**",
            BASE_PATH + "/api-docs/**"
    };

    public static final String BEARER_PREFIX = "Bearer ";

    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

}

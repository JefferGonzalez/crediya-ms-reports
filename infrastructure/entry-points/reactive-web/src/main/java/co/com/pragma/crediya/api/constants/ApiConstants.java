package co.com.pragma.crediya.api.constants;

public final class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_V1 = "/api/v1";

    public static final String REPORTS_PATH = API_V1 + "/reports";

    public static final String[] PUBLIC_PATTERNS = {
            "/actuator",
            "/actuator/health",
            "/actuator/prometheus",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public static final String BEARER_PREFIX = "Bearer ";

    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

}

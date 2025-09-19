package co.com.pragma.crediya.api.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(
        name = "ProblemDetails",
        description = "Standardized error response body following RFC 7807 style"
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetails {

    @Schema(description = "Short, human-readable title of the error", example = "Invalid Request")
    private String title;

    @Schema(description = "Detailed description of the error", example = "Invalid credentials provided. Please check your email and password.")
    private String message;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @JsonIgnore
    private HttpStatus httpStatus;

    @Schema(
            description = "List of field-level validation errors (if any)",
            implementation = FieldValidationError.class
    )
    private List<FieldValidationError> errors;

    @Schema(description = "Timestamp when the error occurred", example = "2025-08-27 15:45:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ProblemDetails(String title, String message, HttpStatus status, List<FieldValidationError> errors) {
        this.title = title;
        this.message = message;
        this.httpStatus = status;
        this.status = status.value();
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public static ProblemDetails forbidden(String title, String message) {
        return new ProblemDetails(title, message, HttpStatus.FORBIDDEN, null);
    }

    public static ProblemDetails unauthorized(String title, String message) {
        return new ProblemDetails(title, message, HttpStatus.UNAUTHORIZED, null);
    }

    public static ProblemDetails badRequest(String title, List<FieldValidationError> errors) {
        return new ProblemDetails(title, null, HttpStatus.BAD_REQUEST, errors);
    }

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}

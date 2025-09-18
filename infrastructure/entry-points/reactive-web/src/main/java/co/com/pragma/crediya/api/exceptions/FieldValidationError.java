package co.com.pragma.crediya.api.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Details of a specific field validation error")
public class FieldValidationError {

    @Schema(description = "Field name that caused the validation error", example = "email")
    private String field;

    @Schema(description = "Validation error message for the field", example = "Email format is invalid")
    private String message;

}

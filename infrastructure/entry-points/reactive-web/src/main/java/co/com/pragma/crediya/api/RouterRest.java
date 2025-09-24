package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.constants.ApiConstants;
import co.com.pragma.crediya.api.dto.ApprovedApplicationsSummaryResponse;
import co.com.pragma.crediya.api.exceptions.ProblemDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = ApiConstants.BASE_PATH,
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = ReportsHandler.class,
                    beanMethod = "getApprovedLoansCount",
                    operation = @Operation(
                            operationId = "getApprovedLoansCount",
                            summary = "Get approved loans count",
                            description = "Retrieves the total number of approved loan applications and the last updated timestamp.",
                            security = @SecurityRequirement(name = "bearerAuth"),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Approved loans count retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = ApprovedApplicationsSummaryResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error while fetching the approved loans count",
                                            content = @Content(schema = @Schema(implementation = ProblemDetails.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(ReportsHandler handler) {
        return RouterFunctions.route()
                .GET(ApiConstants.BASE_PATH, serverRequest -> handler.getApprovedLoansCount())
                .build();
    }

}

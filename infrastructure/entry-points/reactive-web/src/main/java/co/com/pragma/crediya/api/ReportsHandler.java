package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.mapper.ApprovedApplicationsSummaryRestMapper;
import co.com.pragma.crediya.usecase.loan.ApprovedApplicationSummaryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReportsHandler {

    private final ApprovedApplicationSummaryUseCase approvedApplicationSummaryUseCase;

    private final ApprovedApplicationsSummaryRestMapper mapper;

    public Mono<ServerResponse> getApprovedLoansCount() {
        return approvedApplicationSummaryUseCase.getApprovedApplicationsSummary()
                .map(mapper::toResponse)
                .flatMap(report ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(report)
                );
    }

}

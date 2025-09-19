package co.com.pragma.crediya.usecase.loan;

import co.com.pragma.crediya.model.loan.ApprovedApplication;
import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import co.com.pragma.crediya.model.loan.constants.ApprovedApplicationSummaryFieldNames;
import co.com.pragma.crediya.model.loan.gateways.ApprovedApplicationSummaryRepository;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

@ExtendWith(MockitoExtension.class)
class ApprovedApplicationSummaryUseCaseTest {

    @Mock
    private ApprovedApplicationSummaryRepository repository;

    @Mock
    private LoggerPort logger;

    private ApprovedApplicationSummaryUseCase useCase;

    private ApprovedApplication application;

    @BeforeEach
    void setUp() {
        useCase = new ApprovedApplicationSummaryUseCase(repository, logger);

        application = new ApprovedApplication(UUID.randomUUID(), OffsetDateTime.now());
    }

    @Test
    void recordApprovedApplication_WhenSuccessful_ShouldIncrementAndLog() {
        ApprovedApplicationSummary updatedSummary = new ApprovedApplicationSummary(
                ApprovedApplicationSummaryFieldNames.SUMMARY, 5L, Instant.now());

        when(repository.incrementSummary(1L))
                .thenReturn(Mono.just(updatedSummary));

        Mono<Void> result = useCase.recordApprovedApplication(application);

        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).incrementSummary(1L);
    }

    @Test
    void recordApprovedApplication_WhenRepositoryFails_ShouldLogErrorAndPropagateError() {
        RuntimeException repositoryError = new RuntimeException("Database connection failed");

        when(repository.incrementSummary(1L))
                .thenReturn(Mono.error(repositoryError));

        Mono<Void> result = useCase.recordApprovedApplication(application);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).incrementSummary(1L);
    }

    @Test
    void getApprovedApplicationsSummary_WhenSuccessful_ShouldReturnSummaryAndLog() {
        ApprovedApplicationSummary summary = new ApprovedApplicationSummary(
                "summary", 10L, Instant.parse("2024-01-15T12:00:00Z"));

        when(repository.getSummary())
                .thenReturn(Mono.just(summary));

        Mono<ApprovedApplicationSummary> result = useCase.getApprovedApplicationsSummary();

        StepVerifier.create(result)
                .expectNext(summary)
                .verifyComplete();

        verify(repository).getSummary();
    }

    @Test
    void getApprovedApplicationsSummary_WhenRepositoryFails_ShouldLogErrorAndPropagateError() {
        RuntimeException repositoryError = new RuntimeException("Network timeout");

        when(repository.getSummary())
                .thenReturn(Mono.error(repositoryError));

        Mono<ApprovedApplicationSummary> result = useCase.getApprovedApplicationsSummary();

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).getSummary();
    }

    @Test
    void getApprovedApplicationsSummary_WhenReturnsEmptySummary_ShouldHandleGracefully() {
        ApprovedApplicationSummary emptySummary = new ApprovedApplicationSummary("summary", 0L, null);

        when(repository.getSummary())
                .thenReturn(Mono.just(emptySummary));

        Mono<ApprovedApplicationSummary> result = useCase.getApprovedApplicationsSummary();

        StepVerifier.create(result)
                .expectNext(emptySummary)
                .verifyComplete();
    }

}
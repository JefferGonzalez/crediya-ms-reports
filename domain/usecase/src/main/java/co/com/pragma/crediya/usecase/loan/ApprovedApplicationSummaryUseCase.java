package co.com.pragma.crediya.usecase.loan;

import co.com.pragma.crediya.model.loan.ApprovedApplication;
import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import co.com.pragma.crediya.model.loan.gateways.ApprovedApplicationSummaryRepository;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import reactor.core.publisher.Mono;

public record ApprovedApplicationSummaryUseCase(ApprovedApplicationSummaryRepository repository, LoggerPort logger) {

    public Mono<Void> recordApprovedApplication(ApprovedApplication application) {
        logger.info("Recording approved application ID {} at {}", application.id(), application.approvedAt());

        return repository.incrementSummary(1L, application.amount())
                .doOnSuccess(summary ->
                        logger.info("Updated summary: total approved count = {}, total approved amount = {}",
                                summary.approvedApplicationsCount(), summary.approvedApplicationsAmount()))
                .doOnError(e -> logger.error("Failed to update approved applications summary. Reason: {}", e.getMessage(), e))
                .then();
    }

    public Mono<ApprovedApplicationSummary> getApprovedApplicationsSummary() {
        logger.info("Retrieving approved applications summary");

        return repository.getSummary()
                .doOnSuccess(summary ->
                        logger.info("Current summary: total approved count = {}, total approved amount = {}",
                                summary.approvedApplicationsCount(), summary.approvedApplicationsAmount()))
                .doOnError(e -> logger.error("Failed to retrieve approved applications summary. Reason: {}", e.getMessage(), e));
    }

}


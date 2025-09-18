package co.com.pragma.crediya.model.loan.gateways;

import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import reactor.core.publisher.Mono;

public interface ApprovedApplicationSummaryRepository {

    Mono<ApprovedApplicationSummary> incrementSummary(long countToAdd);

    Mono<ApprovedApplicationSummary> getSummary();

}

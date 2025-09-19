package co.com.pragma.crediya.model.loan.gateways;

import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ApprovedApplicationSummaryRepository {

    Mono<ApprovedApplicationSummary> incrementSummary(long countToAdd, BigDecimal amountToAdd);

    Mono<ApprovedApplicationSummary> getSummary();

}

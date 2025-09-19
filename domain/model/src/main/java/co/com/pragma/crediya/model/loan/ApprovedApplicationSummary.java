package co.com.pragma.crediya.model.loan;

import java.math.BigDecimal;
import java.time.Instant;

public record ApprovedApplicationSummary(
        String id,
        long approvedApplicationsCount,
        BigDecimal approvedApplicationsAmount,
        Instant lastUpdated) {
}

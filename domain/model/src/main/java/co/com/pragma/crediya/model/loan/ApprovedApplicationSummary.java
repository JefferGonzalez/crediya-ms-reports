package co.com.pragma.crediya.model.loan;

import java.time.Instant;

public record ApprovedApplicationSummary(
        String id,
        long approvedApplicationsCount,
        Instant lastUpdated) {
}

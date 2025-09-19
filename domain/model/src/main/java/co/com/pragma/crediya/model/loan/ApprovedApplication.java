package co.com.pragma.crediya.model.loan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ApprovedApplication(
        UUID id,
        BigDecimal amount,
        OffsetDateTime approvedAt) {
}

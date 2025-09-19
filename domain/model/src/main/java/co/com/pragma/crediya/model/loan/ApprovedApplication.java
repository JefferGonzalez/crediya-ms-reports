package co.com.pragma.crediya.model.loan;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ApprovedApplication(
        UUID id,
        OffsetDateTime approvedAt) {
}

package co.com.pragma.crediya.dynamodb.converters;

import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import co.com.pragma.crediya.model.loan.constants.ApprovedApplicationSummaryFieldNames;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
public class ApprovedApplicationSummaryConverter {

    public ApprovedApplicationSummary fromDynamoDbAttributes(Map<String, AttributeValue> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return createDefaultSummary();
        }

        String id = extractId(attributes);
        Long count = extractCount(attributes);
        BigDecimal amount = extractAmount(attributes);
        Instant lastUpdated = extractLastUpdated(attributes);

        return new ApprovedApplicationSummary(id, count, amount, lastUpdated);
    }

    private ApprovedApplicationSummary createDefaultSummary() {
        return new ApprovedApplicationSummary(
                ApprovedApplicationSummaryFieldNames.SUMMARY,
                0L,
                BigDecimal.ZERO,
                null
        );
    }

    private String extractId(Map<String, AttributeValue> attributes) {
        return Optional.ofNullable(attributes.get(ApprovedApplicationSummaryFieldNames.ID))
                .map(AttributeValue::s)
                .orElse(ApprovedApplicationSummaryFieldNames.SUMMARY);
    }

    private Long extractCount(Map<String, AttributeValue> attributes) {
        return Optional.ofNullable(attributes.get(ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_COUNT))
                .map(AttributeValue::n)
                .map(this::parseToLong)
                .orElse(0L);
    }

    private BigDecimal extractAmount(Map<String, AttributeValue> attributes) {
        return Optional.ofNullable(attributes.get(ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_AMOUNT))
                .map(AttributeValue::n)
                .map(this::parseToBigDecimal)
                .orElse(BigDecimal.ZERO);
    }

    private Instant extractLastUpdated(Map<String, AttributeValue> attributes) {
        return Optional.ofNullable(attributes.get(ApprovedApplicationSummaryFieldNames.LAST_UPDATED))
                .map(AttributeValue::s)
                .map(this::parseToInstant)
                .orElse(null);
    }

    private Long parseToLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private BigDecimal parseToBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Instant parseToInstant(String value) {
        try {
            return Instant.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

}
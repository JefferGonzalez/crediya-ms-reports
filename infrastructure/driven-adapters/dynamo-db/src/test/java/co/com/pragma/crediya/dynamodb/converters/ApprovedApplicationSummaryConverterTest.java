package co.com.pragma.crediya.dynamodb.converters;

import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import co.com.pragma.crediya.model.loan.constants.ApprovedApplicationSummaryFieldNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApprovedApplicationSummaryConverterTest {

    private ApprovedApplicationSummaryConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ApprovedApplicationSummaryConverter();
    }

    @Test
    void fromDynamoDbAttributesWithValidAttributesShouldConvertCorrectly() {
        Map<String, AttributeValue> attributes = new HashMap<>();
        attributes.put(ApprovedApplicationSummaryFieldNames.ID,
                AttributeValue.builder().s(ApprovedApplicationSummaryFieldNames.SUMMARY).build());
        attributes.put(ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_COUNT,
                AttributeValue.builder().n("42").build());
        attributes.put(ApprovedApplicationSummaryFieldNames.LAST_UPDATED,
                AttributeValue.builder().s("2024-01-15T10:30:00Z").build());

        ApprovedApplicationSummary result = converter.fromDynamoDbAttributes(attributes);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(ApprovedApplicationSummaryFieldNames.SUMMARY);
        assertThat(result.approvedApplicationsCount()).isEqualTo(42L);
        assertThat(result.lastUpdated()).isEqualTo(Instant.parse("2024-01-15T10:30:00Z"));
    }

    @Test
    void fromDynamoDbAttributesWithNullAttributesShouldReturnDefault() {
        ApprovedApplicationSummary result = converter.fromDynamoDbAttributes(null);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(ApprovedApplicationSummaryFieldNames.SUMMARY);
        assertThat(result.approvedApplicationsCount()).isZero();
        assertThat(result.lastUpdated()).isNull();
    }

    @Test
    void fromDynamoDbAttributesWithEmptyAttributesShouldReturnDefault() {
        ApprovedApplicationSummary result = converter.fromDynamoDbAttributes(new HashMap<>());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(ApprovedApplicationSummaryFieldNames.SUMMARY);
        assertThat(result.approvedApplicationsCount()).isZero();
        assertThat(result.lastUpdated()).isNull();
    }

    @Test
    void fromDynamoDbAttributesWithMissingCountShouldDefaultToZero() {
        Map<String, AttributeValue> attributes = new HashMap<>();
        attributes.put(ApprovedApplicationSummaryFieldNames.ID,
                AttributeValue.builder().s(ApprovedApplicationSummaryFieldNames.SUMMARY).build());
        attributes.put(ApprovedApplicationSummaryFieldNames.LAST_UPDATED,
                AttributeValue.builder().s("2024-01-15T10:30:00Z").build());

        ApprovedApplicationSummary result = converter.fromDynamoDbAttributes(attributes);

        assertThat(result.approvedApplicationsCount()).isZero();
        assertThat(result.lastUpdated()).isEqualTo(Instant.parse("2024-01-15T10:30:00Z"));
    }

    @Test
    void fromDynamoDbAttributesWithInvalidNumberShouldDefaultToZero() {
        Map<String, AttributeValue> attributes = new HashMap<>();
        attributes.put(ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_COUNT,
                AttributeValue.builder().n("invalid-number").build());

        ApprovedApplicationSummary result = converter.fromDynamoDbAttributes(attributes);

        assertThat(result.approvedApplicationsCount()).isZero();
    }

    @Test
    void fromDynamoDbAttributesWithInvalidDateShouldSetLastUpdatedToNull() {
        Map<String, AttributeValue> attributes = new HashMap<>();
        attributes.put(ApprovedApplicationSummaryFieldNames.LAST_UPDATED,
                AttributeValue.builder().s("invalid-date").build());

        ApprovedApplicationSummary result = converter.fromDynamoDbAttributes(attributes);

        assertThat(result.lastUpdated()).isNull();
    }

    @Test
    void fromDynamoDbAttributesWithMissingLastUpdatedShouldSetToNull() {
        Map<String, AttributeValue> attributes = new HashMap<>();
        attributes.put(ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_COUNT,
                AttributeValue.builder().n("10").build());

        ApprovedApplicationSummary result = converter.fromDynamoDbAttributes(attributes);

        assertThat(result.lastUpdated()).isNull();
    }

}
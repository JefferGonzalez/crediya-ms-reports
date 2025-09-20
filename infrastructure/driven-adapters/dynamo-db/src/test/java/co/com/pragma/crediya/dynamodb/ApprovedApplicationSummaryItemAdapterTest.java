package co.com.pragma.crediya.dynamodb;

import co.com.pragma.crediya.dynamodb.converters.ApprovedApplicationSummaryConverter;
import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import co.com.pragma.crediya.model.loan.constants.ApprovedApplicationSummaryFieldNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovedApplicationSummaryItemAdapterTest {

    @Mock
    private DynamoDbAsyncClient lowLevelClient;

    @Mock
    private ApprovedApplicationSummaryConverter converter;

    private ApprovedApplicationSummaryItemAdapter adapter;

    private final long countToAdd = 1;

    private final BigDecimal amountToAdd = BigDecimal.valueOf(1500000);

    @BeforeEach
    void setUp() {
        adapter = new ApprovedApplicationSummaryItemAdapter(lowLevelClient, converter);
    }

    @Test
    void incrementSummaryWhenSuccessfulShouldReturnUpdatedSummary() {
        Map<String, AttributeValue> responseAttributes = createMockAttributes(10L, BigDecimal.valueOf(85000000), "2024-01-15T10:30:00Z");
        ApprovedApplicationSummary expectedSummary = new ApprovedApplicationSummary(
                ApprovedApplicationSummaryFieldNames.SUMMARY,
                10L,
                BigDecimal.valueOf(85000000),
                Instant.parse("2024-01-15T10:30:00Z")
        );

        UpdateItemResponse updateResponse = UpdateItemResponse.builder()
                .attributes(responseAttributes)
                .build();

        when(lowLevelClient.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(updateResponse));

        when(converter.fromDynamoDbAttributes(responseAttributes))
                .thenReturn(expectedSummary);

        StepVerifier.create(adapter.incrementSummary(countToAdd, amountToAdd))
                .expectNext(expectedSummary)
                .verifyComplete();

        ArgumentCaptor<UpdateItemRequest> requestCaptor = ArgumentCaptor.forClass(UpdateItemRequest.class);
        verify(lowLevelClient).updateItem(requestCaptor.capture());

        UpdateItemRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.tableName()).isEqualTo(ApprovedApplicationSummaryFieldNames.TABLE_NAME);
        assertThat(capturedRequest.returnValues()).isEqualTo(ReturnValue.ALL_NEW);
        assertThat(capturedRequest.updateExpression()).contains("ADD #count :countInc");
        assertThat(capturedRequest.updateExpression()).contains("SET #amount = if_not_exists(#amount, :zero) + :amountInc");
        assertThat(capturedRequest.updateExpression()).contains("#lastUpdated = :now");

        Map<String, AttributeValue> values = capturedRequest.expressionAttributeValues();
        assertThat(values.get(":countInc").n()).isEqualTo("1");
        assertThat(values.get(":amountInc").n()).isEqualTo("1500000");
        assertThat(values.get(":now").s()).isNotNull();
        assertThat(values.get(":zero").n()).isEqualTo("0");

        assertThat(capturedRequest.expressionAttributeNames())
                .containsEntry("#count", ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_COUNT)
                .containsEntry("#amount", ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_AMOUNT)
                .containsEntry("#lastUpdated", ApprovedApplicationSummaryFieldNames.LAST_UPDATED);

        verify(converter).fromDynamoDbAttributes(responseAttributes);
    }

    @Test
    void incrementSummaryWhenDynamoDbFailsShouldPropagateError() {
        RuntimeException dynamoException = new RuntimeException("DynamoDB error");

        when(lowLevelClient.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(dynamoException));

        StepVerifier.create(adapter.incrementSummary(countToAdd, amountToAdd))
                .expectError(RuntimeException.class)
                .verify();

        verify(converter, never()).fromDynamoDbAttributes(any());
    }

    @Test
    void getSummaryWhenItemExistsShouldReturnSummary() {
        Map<String, AttributeValue> responseAttributes = createMockAttributes(15L, BigDecimal.valueOf(95000000), "2024-01-15T11:00:00Z");
        ApprovedApplicationSummary expectedSummary = new ApprovedApplicationSummary(
                ApprovedApplicationSummaryFieldNames.SUMMARY,
                15L,
                BigDecimal.valueOf(95000000),
                Instant.parse("2024-01-15T11:00:00Z")
        );

        GetItemResponse getResponse = GetItemResponse.builder()
                .item(responseAttributes)
                .build();

        when(lowLevelClient.getItem(any(GetItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(getResponse));

        when(converter.fromDynamoDbAttributes(responseAttributes))
                .thenReturn(expectedSummary);

        StepVerifier.create(adapter.getSummary())
                .expectNext(expectedSummary)
                .verifyComplete();

        ArgumentCaptor<GetItemRequest> requestCaptor = ArgumentCaptor.forClass(GetItemRequest.class);
        verify(lowLevelClient).getItem(requestCaptor.capture());

        GetItemRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.tableName()).isEqualTo(ApprovedApplicationSummaryFieldNames.TABLE_NAME);

        Map<String, AttributeValue> key = capturedRequest.key();
        assertThat(key.get(ApprovedApplicationSummaryFieldNames.ID).s()).isEqualTo(ApprovedApplicationSummaryFieldNames.SUMMARY);

        verify(converter).fromDynamoDbAttributes(responseAttributes);
    }

    @Test
    void getSummaryWhenItemDoesNotExistShouldReturnDefault() {
        GetItemResponse emptyResponse = GetItemResponse.builder().build(); // No item
        ApprovedApplicationSummary defaultSummary = new ApprovedApplicationSummary(
                ApprovedApplicationSummaryFieldNames.SUMMARY,
                0L,
                BigDecimal.ZERO,
                null
        );

        when(lowLevelClient.getItem(any(GetItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(emptyResponse));

        when(converter.fromDynamoDbAttributes(null))
                .thenReturn(defaultSummary);

        StepVerifier.create(adapter.getSummary())
                .expectNext(defaultSummary)
                .verifyComplete();

        verify(converter).fromDynamoDbAttributes(null);
    }

    @Test
    void getSummaryWhenDynamoDbFailsShouldPropagateError() {
        RuntimeException dynamoException = new RuntimeException("DynamoDB connection error");

        when(lowLevelClient.getItem(any(GetItemRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(dynamoException));

        StepVerifier.create(adapter.getSummary())
                .expectError(RuntimeException.class)
                .verify();

        verify(converter, never()).fromDynamoDbAttributes(any());
    }

    @Test
    void getSummaryWhenItemNotFoundShouldReturnDefaultFromConverter() {
        GetItemResponse emptyResponse = GetItemResponse.builder()
                .build();

        ApprovedApplicationSummary defaultSummary = new ApprovedApplicationSummary(
                ApprovedApplicationSummaryFieldNames.SUMMARY, 0L, BigDecimal.ZERO, null);

        when(lowLevelClient.getItem(any(GetItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(emptyResponse));

        when(converter.fromDynamoDbAttributes(null))
                .thenReturn(defaultSummary);

        StepVerifier.create(adapter.getSummary())
                .expectNext(defaultSummary)
                .verifyComplete();
    }

    private Map<String, AttributeValue> createMockAttributes(Long count, BigDecimal amount, String lastUpdated) {
        Map<String, AttributeValue> attributes = new HashMap<>();
        attributes.put(ApprovedApplicationSummaryFieldNames.ID,
                AttributeValue.builder().s(ApprovedApplicationSummaryFieldNames.SUMMARY).build());
        attributes.put(ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_COUNT,
                AttributeValue.builder().n(count.toString()).build());
        attributes.put(ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_AMOUNT,
                AttributeValue.builder().n(amount.toPlainString()).build());

        if (lastUpdated != null) {
            attributes.put(ApprovedApplicationSummaryFieldNames.LAST_UPDATED,
                    AttributeValue.builder().s(lastUpdated).build());
        }

        return attributes;
    }

}
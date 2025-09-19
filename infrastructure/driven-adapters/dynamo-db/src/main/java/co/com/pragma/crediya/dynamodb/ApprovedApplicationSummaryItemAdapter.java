package co.com.pragma.crediya.dynamodb;

import co.com.pragma.crediya.dynamodb.converters.ApprovedApplicationSummaryConverter;
import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import co.com.pragma.crediya.model.loan.constants.ApprovedApplicationSummaryFieldNames;
import co.com.pragma.crediya.model.loan.gateways.ApprovedApplicationSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ApprovedApplicationSummaryItemAdapter implements ApprovedApplicationSummaryRepository {

    private final DynamoDbAsyncClient lowLevelClient;

    private final ApprovedApplicationSummaryConverter converter;

    @Override
    public Mono<ApprovedApplicationSummary> incrementSummary(long countToAdd) {
        Map<String, AttributeValue> key = buildKey();

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(ApprovedApplicationSummaryFieldNames.TABLE_NAME)
                .key(key)
                .updateExpression("ADD #count :countInc SET #lastUpdated = :now")
                .expressionAttributeNames(buildExpressionAttributeNames())
                .expressionAttributeValues(buildIncrementExpressionValues(countToAdd))
                .returnValues(ReturnValue.ALL_NEW)
                .build();

        return Mono.fromFuture(() -> lowLevelClient.updateItem(request))
                .map(UpdateItemResponse::attributes)
                .map(converter::fromDynamoDbAttributes);
    }

    @Override
    public Mono<ApprovedApplicationSummary> getSummary() {
        Map<String, AttributeValue> key = buildKey();

        GetItemRequest request = GetItemRequest.builder()
                .tableName(ApprovedApplicationSummaryFieldNames.TABLE_NAME)
                .key(key)
                .build();

        return Mono.fromFuture(() -> lowLevelClient.getItem(request))
                .filter(GetItemResponse::hasItem)
                .map(GetItemResponse::item)
                .map(converter::fromDynamoDbAttributes)
                .switchIfEmpty(Mono.fromSupplier(() -> converter.fromDynamoDbAttributes(null)));
    }

    private Map<String, AttributeValue> buildKey() {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(ApprovedApplicationSummaryFieldNames.ID, AttributeValue.builder().s(ApprovedApplicationSummaryFieldNames.SUMMARY).build());
        return key;
    }

    private Map<String, String> buildExpressionAttributeNames() {
        Map<String, String> names = new HashMap<>();
        names.put("#count", ApprovedApplicationSummaryFieldNames.APPROVED_APPLICATIONS_COUNT);
        names.put("#lastUpdated", ApprovedApplicationSummaryFieldNames.LAST_UPDATED);
        return names;
    }

    private Map<String, AttributeValue> buildIncrementExpressionValues(long countToAdd) {
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":countInc", AttributeValue.builder().n(Long.toString(countToAdd)).build());
        values.put(":now", AttributeValue.builder().s(Instant.now().toString()).build());
        return values;
    }

}

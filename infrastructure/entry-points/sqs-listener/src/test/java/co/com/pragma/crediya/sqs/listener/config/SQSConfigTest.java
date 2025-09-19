package co.com.pragma.crediya.sqs.listener.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.metrics.LoggingMetricPublisher;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

class SQSConfigTest {

    private static final String REGION = "us-east-1";

    private static final String QUEUE_URL = "http://queue-url/";

    private static final String LOAN_APPROVED_EVENTS_QUEUE_NAME = "LoanApprovedEvents";

    private SQSConfig sqsConfig;

    private SQSProperties sqsProperties;

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    @BeforeEach
    void init() {
        sqsProperties = new SQSProperties(REGION, QUEUE_URL, LOAN_APPROVED_EVENTS_QUEUE_NAME, 20, 10, 10, 1);
        sqsConfig = new SQSConfig();
    }

    @Test
    void configSQSListenerIsNotNull() {
        assertThat(sqsConfig.sqsListener(sqsAsyncClient, sqsProperties, message -> Mono.empty())).isNotNull();
    }

    @Test
    void configSqsIsNotNull() {
        var loggingMetricPublisher = LoggingMetricPublisher.create();
        assertThat(sqsConfig.configSqs(sqsProperties, loggingMetricPublisher)).isNotNull();
    }

}
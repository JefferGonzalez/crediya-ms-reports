package co.com.pragma.crediya.metrics.aws;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricRecord;
import software.amazon.awssdk.metrics.SdkMetric;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MicrometerMetricPublisherTest {

    @Mock
    private ExecutorService mockExecutor;

    @Mock
    private MetricCollection mockMetricCollection;

    @Mock
    private MetricRecord<Duration> apiCallDurationRecord;

    @Mock
    private MetricRecord<Integer> retryAttemptsRecord;

    @Mock
    private MetricRecord<String> serviceIdRecord;

    @Mock
    private MetricRecord<Boolean> successfulRecord;

    @Mock
    private SdkMetric<Duration> apiCallDurationMetric;

    @Mock
    private SdkMetric<Integer> retryAttemptsMetric;

    @Mock
    private SdkMetric<String> serviceIdMetric;

    @Mock
    private SdkMetric<Boolean> successfulMetric;

    private MeterRegistry meterRegistry;

    private MicrometerMetricPublisher publisher;

    public static final String API_CALL_DURATION = "ApiCallDuration";

    public static final String RETRY_COUNT = "RetryCount";

    public static final String SQS = "SQS";

    public static final String SERVICE_ID = "ServiceId";

    public static final String SUCCESSFUL = "Successful";

    public static final String TRUE = "true";

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        publisher = new MicrometerMetricPublisher(mockExecutor, meterRegistry);
    }

    @Test
    @DisplayName("Publish AWS SDK metrics to Micrometer registry with correct timers, counters and tags")
    void publish_shouldConvertAndRecordMetrics() {
        when(apiCallDurationRecord.value()).thenReturn(Duration.ofMillis(150));
        when(apiCallDurationRecord.metric()).thenReturn(apiCallDurationMetric);
        when(apiCallDurationMetric.name()).thenReturn(API_CALL_DURATION);

        when(retryAttemptsRecord.value()).thenReturn(2);
        when(retryAttemptsRecord.metric()).thenReturn(retryAttemptsMetric);
        when(retryAttemptsMetric.name()).thenReturn(RETRY_COUNT);

        when(serviceIdRecord.value()).thenReturn(SQS);
        when(serviceIdRecord.metric()).thenReturn(serviceIdMetric);
        when(serviceIdMetric.name()).thenReturn(SERVICE_ID);

        when(successfulRecord.value()).thenReturn(true);
        when(successfulRecord.metric()).thenReturn(successfulMetric);
        when(successfulMetric.name()).thenReturn(SUCCESSFUL);

        when(mockMetricCollection.stream()).thenAnswer(invocation -> Stream.of(apiCallDurationRecord, retryAttemptsRecord, serviceIdRecord, successfulRecord));

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        publisher.publish(mockMetricCollection);
        verify(mockExecutor).submit(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        Timer apiCallTimer = meterRegistry.find(API_CALL_DURATION).timer();
        assertThat(apiCallTimer).isNotNull();
        assertThat(apiCallTimer.count()).isEqualTo(1);
        assertThat(apiCallTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS)).isEqualTo(150);
        assertThat(apiCallTimer.getId().getTags()).containsExactlyInAnyOrder(
                Tag.of(SERVICE_ID, SQS),
                Tag.of(SUCCESSFUL, TRUE)
        );

        Counter retryCounter = meterRegistry.find(RETRY_COUNT).counter();
        assertThat(retryCounter).isNotNull();
        assertThat(retryCounter.count()).isEqualTo(2);
        assertThat(retryCounter.getId().getTags()).containsExactlyInAnyOrder(
                Tag.of(SERVICE_ID, SQS),
                Tag.of(SUCCESSFUL, TRUE)
        );
    }

}

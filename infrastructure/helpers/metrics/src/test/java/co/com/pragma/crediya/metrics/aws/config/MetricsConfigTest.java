package co.com.pragma.crediya.metrics.aws.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MetricsConfigTest {

    @Test
    void awsMetricsExecutorBean_shouldBeCreated() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MetricsConfig.class)) {
            ExecutorService executorService = (ExecutorService) context.getBean(MetricsConfig.AWS_METRICS_EXECUTOR);
            assertThat(executorService).isNotNull();
            assertThat(executorService.isShutdown()).isFalse();
        }
    }

}

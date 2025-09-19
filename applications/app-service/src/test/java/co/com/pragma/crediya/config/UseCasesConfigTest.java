package co.com.pragma.crediya.config;

import co.com.pragma.crediya.model.loan.gateways.ApprovedApplicationSummaryRepository;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public ApprovedApplicationSummaryRepository repository() {
            return Mockito.mock(ApprovedApplicationSummaryRepository.class);
        }

        @Bean
        public LoggerPort loggerPort() {
            return Mockito.mock(LoggerPort.class);
        }

    }

}
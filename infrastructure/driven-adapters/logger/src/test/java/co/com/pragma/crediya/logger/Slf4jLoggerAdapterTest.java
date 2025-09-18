package co.com.pragma.crediya.logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Slf4jLoggerAdapterTest {

    @Mock
    private Logger logger;

    private Slf4jLoggerAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new Slf4jLoggerAdapter(logger);
    }

    @Test
    void constructor_withClass_shouldCreateLogger() {
        Slf4jLoggerAdapter adapterWithClass = new Slf4jLoggerAdapter(Slf4jLoggerAdapterTest.class);

        assertNotNull(adapterWithClass.logger());
    }

    @Test
    void info_shouldDelegateToLogger() {
        adapter.info("info message {}", "arg");

        verify(logger).info(eq("info message {}"), AdditionalMatchers.aryEq(new Object[]{"arg"}));
    }

    @Test
    void warn_shouldDelegateToLogger() {
        adapter.warn("warn message {}", "arg");

        verify(logger).warn(eq("warn message {}"), AdditionalMatchers.aryEq(new Object[]{"arg"}));

    }

    @Test
    void error_shouldDelegateToLogger() {
        adapter.error("error message {}", "arg");

        verify(logger).error(eq("error message {}"), AdditionalMatchers.aryEq(new Object[]{"arg"}));
    }

}
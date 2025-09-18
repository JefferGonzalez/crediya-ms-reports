package co.com.pragma.crediya.logger;

import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Slf4jLoggerAdapter(Logger logger) implements LoggerPort {

    public Slf4jLoggerAdapter(Class<?> logger) {
        this(LoggerFactory.getLogger(logger));
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

}
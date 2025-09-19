package co.com.pragma.crediya.model.logs.gateways;

public interface LoggerPort {

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

}
package org.myshop.logger;

public interface Logger {
    void info(String message, Object...args);
    void error(String error, Object...args);
}

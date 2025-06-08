package org.myshop.logger;

public class SystemLogger implements Logger {
    @Override
    public void info(String message, Object... args) {
        System.out.printf(message + "%n", args);
    }

    @Override
    public void error(String error, Object... args) {
        System.err.printf(error + "%n", args);
    }
}

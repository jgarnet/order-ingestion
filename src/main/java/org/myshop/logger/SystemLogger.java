package org.myshop.logger;

public class SystemLogger implements Logger {
    @Override
    public void info(String message, Object... args) {
        System.out.printf(getPrefix() + message + "%n", args);
    }

    @Override
    public void error(String error, Object... args) {
        System.err.printf(getPrefix() + error + "%n", args);
    }

    private static String getPrefix() {
        return String.format("[%s]: ", Thread.currentThread().getName());
    }
}

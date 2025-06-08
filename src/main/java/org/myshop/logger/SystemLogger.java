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
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return String.format("[%s] %s: ", stackTrace[3].getClassName(), Thread.currentThread().getName());
    }
}

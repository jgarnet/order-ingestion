package org.myshop.exception;

public class ValidationException extends Exception {
    private String error;
    public ValidationException(String error) {
        super();
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

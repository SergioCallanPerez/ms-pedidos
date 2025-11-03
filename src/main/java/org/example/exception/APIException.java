package org.example.exception;

public class APIException extends Exception {
    private final int status;

    public APIException(String message) {
        super(message);
        this.status = 400;
    }

    public APIException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

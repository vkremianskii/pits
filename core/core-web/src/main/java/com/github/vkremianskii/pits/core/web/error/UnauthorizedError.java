package com.github.vkremianskii.pits.core.web.error;

public class UnauthorizedError extends RuntimeException {

    public UnauthorizedError() {
    }

    public UnauthorizedError(String message) {
        super(message);
    }

    public UnauthorizedError(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedError(Throwable cause) {
        super(cause);
    }

    public UnauthorizedError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

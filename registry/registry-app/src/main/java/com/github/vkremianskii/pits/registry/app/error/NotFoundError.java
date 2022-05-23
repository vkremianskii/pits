package com.github.vkremianskii.pits.registry.app.error;

public class NotFoundError extends RuntimeException {

    public NotFoundError() {
    }

    public NotFoundError(String message) {
        super(message);
    }

    public NotFoundError(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundError(Throwable cause) {
        super(cause);
    }

    public NotFoundError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

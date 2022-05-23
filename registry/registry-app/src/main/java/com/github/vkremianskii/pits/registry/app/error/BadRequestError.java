package com.github.vkremianskii.pits.registry.app.error;

public class BadRequestError extends RuntimeException {

    public BadRequestError() {
        super();
    }

    public BadRequestError(String message) {
        super(message);
    }

    public BadRequestError(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestError(Throwable cause) {
        super(cause);
    }

    protected BadRequestError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

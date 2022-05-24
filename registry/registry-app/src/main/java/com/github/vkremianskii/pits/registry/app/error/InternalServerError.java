package com.github.vkremianskii.pits.registry.app.error;

public class InternalServerError extends RuntimeException {

    public InternalServerError() {
        super();
    }

    public InternalServerError(String message) {
        super(message);
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerError(Throwable cause) {
        super(cause);
    }

    protected InternalServerError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

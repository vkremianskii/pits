package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.registry.app.error.BadRequestError;
import com.github.vkremianskii.pits.registry.app.error.InternalServerError;
import com.github.vkremianskii.pits.registry.app.error.NotFoundError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RegistryExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RegistryExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<Void> handleBadRequestError(BadRequestError e) {
        LOG.error("", e);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleNotFoundError(NotFoundError e) {
        LOG.error("", e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleInternalServerError(InternalServerError e) {
        LOG.error("", e);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleGenericException(Exception e) {
        LOG.error("", e);
        return ResponseEntity.internalServerError().build();
    }
}

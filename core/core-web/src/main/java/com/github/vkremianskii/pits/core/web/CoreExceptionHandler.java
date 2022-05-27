package com.github.vkremianskii.pits.core.web;

import com.github.vkremianskii.pits.core.web.error.BadRequestError;
import com.github.vkremianskii.pits.core.web.error.InternalServerError;
import com.github.vkremianskii.pits.core.web.error.NotFoundError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@ControllerAdvice
@Order(HIGHEST_PRECEDENCE)
public class CoreExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CoreExceptionHandler.class);

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

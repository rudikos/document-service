package com.workwolf.documentservice.endpoint;

import com.workwolf.documentservice.exception.DocumentInfectedException;
import com.workwolf.documentservice.exception.DocumentStorageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = "com.workwolf.documentservice.endpoint")
public class DocumentControllerAdvice extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(DocumentControllerAdvice.class);

    @ExceptionHandler({DocumentInfectedException.class})
    public ResponseEntity<Object> handleObjectNotFoundException(DocumentInfectedException ex) {
        LOGGER.info(ex::getMessage, ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(ex.getMessage());
    }

    @ExceptionHandler({DocumentStorageException.class})
    public ResponseEntity<Object> handleObjectNotFoundException(DocumentStorageException ex) {
        LOGGER.info(ex::getMessage, ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(ex.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Void> handleAll(Exception ex) {
        LOGGER.error(ex::getMessage, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

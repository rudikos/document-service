package com.workwolf.documentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DocumentInfectedException extends RuntimeException {

    public DocumentInfectedException(String message) {
        super(message);
    }

    public DocumentInfectedException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.kiennguyenfpt.datingapp.exceptions;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends RuntimeException {
    private HttpStatus httpStatus;

    public AccessDeniedException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

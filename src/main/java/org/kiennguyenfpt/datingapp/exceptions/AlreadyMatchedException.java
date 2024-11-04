package org.kiennguyenfpt.datingapp.exceptions;

public class AlreadyMatchedException extends RuntimeException {
    public AlreadyMatchedException(String message) {
        super(message);
    }
}

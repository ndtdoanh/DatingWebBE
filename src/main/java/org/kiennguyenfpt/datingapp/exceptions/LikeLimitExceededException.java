package org.kiennguyenfpt.datingapp.exceptions;

public class LikeLimitExceededException extends RuntimeException {
    public LikeLimitExceededException(String message) {
        super(message);
    }
}

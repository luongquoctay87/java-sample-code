package com.sample.exception;

/**
 * Handle object is not updated
 */
public class NotAcceptableException extends RuntimeException {
    public NotAcceptableException(String message) {
        super(message);
    }
}

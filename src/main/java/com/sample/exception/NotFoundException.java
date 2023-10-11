package com.sample.exception;

/**
 * Handle object not found
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

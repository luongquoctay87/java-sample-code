package com.sample.exception;

/**
 * Handle object is duplicated
 */
public class DuplicatedException extends RuntimeException {
    public DuplicatedException(String message) {
        super(message);
    }
}

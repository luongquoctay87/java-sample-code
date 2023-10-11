package com.sample.exception;

/**
 * Handle object authenticate fail
 */
public class ForbiddenException extends Exception {
    public ForbiddenException(String message) {
        super(message);
    }
}

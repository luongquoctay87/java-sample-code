package com.sample.exception;

/**
 * Handle object authorize fail
 */
public class UnAuthorizeException extends Exception {
    public UnAuthorizeException(String message) {
        super(message);
    }
}

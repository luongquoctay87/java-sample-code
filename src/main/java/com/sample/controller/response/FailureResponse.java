package com.sample.controller.response;

import org.springframework.http.HttpStatus;

/**
 * Failure response when it gets an exception error
 */
public class FailureResponse extends SuccessResponse {
    /**
     * Failure response for api
     *
     * @param status
     * @param message
     */
    public FailureResponse(HttpStatus status, String message) {
        super(status, message);
    }
}

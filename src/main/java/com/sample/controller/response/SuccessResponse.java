package com.sample.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SuccessResponse extends ResponseEntity<SuccessResponse.Payload> {

    /**
     * Create a new {@code ApiResponse} with the given code,message,http status.
     *
     * @param status  status code
     * @param message status code message
     */
    public SuccessResponse(HttpStatus status, String message) {
        super(new Payload(status.value(), message), HttpStatus.OK);
    }

    /**
     * Create a new {@code ApiResponse} with the given code,message,data,http status.
     *
     * @param status  status code
     * @param message status code message
     * @param data    data response
     */
    public SuccessResponse(HttpStatus status, String message, Object data) {
        super(new Payload(status.value(), message, data), HttpStatus.OK);
    }

    @Data
    @AllArgsConstructor
    public static class Payload {
        private Integer status;
        private String message;
        private Object data;

        public Payload(Integer status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
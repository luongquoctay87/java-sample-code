package com.sample.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.Date;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle request data
     *
     * @param e
     * @param request
     * @return error
     */
    @ExceptionHandler({InvalidDataException.class, ConstraintViolationException.class,
            MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "400 Response",
                                    summary = "Handle request data",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:26:34.388+00:00",
                                              "status": 400,
                                              "path": "/accounts/user",
                                              "error": "Invalid request",
                                              "messages": "Email is invalid"
                                            }"""
                            ))})
    })
    public Error handleValidationException(Exception e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setStatus(BAD_REQUEST.value());
        error.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[");
            int end = message.lastIndexOf("]");
            message = message.substring(start + 1, end - 1);
            error.setError("Body is invalid");
            error.setMessages(message);
        } else if (e instanceof MissingServletRequestParameterException) {
            error.setError("Param is invalid");
            error.setMessages(message);
        } else if (e instanceof ConstraintViolationException) {
            error.setError("Param is invalid");
            error.setMessages(message.substring(message.indexOf(" ") + 1));
        } else {
            error.setError("Data is invalid");
            error.setMessages(message);
        }

        return error;
    }


    /**
     * Handle exception when the request not found object
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "404 Response",
                                    summary = "Handle resource not found",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 404,
                                              "path": "/accounts/user/1000000",
                                              "error": "Not Found",
                                              "messages": "User not found"
                                            }"""
                            ))})
    })
    public Error handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(NOT_FOUND.value());
        error.setError(NOT_FOUND.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }

    /**
     * Handle conflict data exception
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(CONFLICT)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "409 Response",
                                    summary = "Handle conflict data",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:07:35.321+00:00",
                                              "status": 409,
                                              "path": "/accounts/user",
                                              "error": "Conflict",
                                              "messages": "Email has registered, Please try again!"
                                            }"""
                            ))})
    })
    public Error handleDuplicateKeyException(DuplicateKeyException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(CONFLICT.value());
        error.setError(CONFLICT.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }

    /**
     * Handle exception when internal server error
     *
     * @param e
     * @param request
     * @return error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "500 Response",
                                    summary = "Handle internal server error",
                                    value = """
                                            {
                                              "timestamp": "2023-10-19T06:35:52.333+00:00",
                                              "status": 500,
                                              "path": "/accounts/user",
                                              "error": "Internal Server Error",
                                              "messages": "Connection timeout, please try again"
                                            }"""
                            ))})
    })
    public Error handleException(Exception e, WebRequest request, HttpServletResponse response) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(INTERNAL_SERVER_ERROR.value());
        error.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
        error.setMessages(e.getMessage());
        response.setStatus(INTERNAL_SERVER_ERROR.value());

        return error;
    }

    @Data
    private static class Error {
        private Date timestamp;
        private int status;
        private String path;
        private String error;
        private String messages;
    }
}

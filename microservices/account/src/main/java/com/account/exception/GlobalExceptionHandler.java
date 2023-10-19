package com.account.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle request body validation
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "400 Response",
                                    summary = "Handle request body validation",
                                    value = "{\n" +
                                            "  \"timestamp\": \"2023-10-19T06:26:34.388+00:00\",\n" +
                                            "  \"status\": 400,\n" +
                                            "  \"path\": \"/accounts/user\",\n" +
                                            "  \"error\": \"Invalid request body\",\n" +
                                            "  \"messages\": \"email is invalid\"\n" +
                                            "}"
                            ))})
    })
    public Error handleValidationException(MethodArgumentNotValidException e, WebRequest request) {
        String eMessage = e.getMessage();
        int start = eMessage.lastIndexOf("[");
        int end = eMessage.lastIndexOf("]");
        eMessage = eMessage.substring(start + 1, end - 1);

        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(BAD_REQUEST.value());
        error.setError("Invalid request body");
        error.setMessages(eMessage);

        return error;
    }

    /**
     * Handle invalid data
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(BAD_REQUEST)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "400 Response",
                                    summary = "Handle request body validation",
                                    value = "{\n" +
                                            "  \"timestamp\": \"2023-10-19T06:26:34.388+00:00\",\n" +
                                            "  \"status\": 400,\n" +
                                            "  \"path\": \"/accounts/user\",\n" +
                                            "  \"error\": \"Invalid parameter\",\n" +
                                            "  \"messages\": \"Email is invalid\"\n" +
                                            "}"
                            ))})
    })
    public Error handleValidationException(InvalidDataException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(BAD_REQUEST.value());
        error.setError("Invalid parameter");
        error.setMessages(e.getMessage());

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
                                    summary = "Handle invalid request",
                                    value = "{\n" +
                                            "  \"timestamp\": \"2023-10-19T06:07:35.321+00:00\",\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"path\": \"/accounts/user/1000000\",\n" +
                                            "  \"error\": \"Not Found\",\n" +
                                            "  \"messages\": \"User not found\"\n" +
                                            "}"
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
                                    summary = "Handle conflict data exception ",
                                    value = "{\n" +
                                            "  \"timestamp\": \"2023-10-19T06:07:35.321+00:00\",\n" +
                                            "  \"status\": 409,\n" +
                                            "  \"path\": \"/accounts/user\",\n" +
                                            "  \"error\": \"Conflict\",\n" +
                                            "  \"messages\": \"Email has registered, Please try again!\"\n" +
                                            "}"
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
                                    summary = "Handle exception when internal server error",
                                    value = "{\n" +
                                            "  \"timestamp\": \"2023-10-19T06:35:52.333+00:00\",\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"path\": \"/accounts/user\",\n" +
                                            "  \"error\": \"Internal Server Error\",\n" +
                                            "  \"messages\": \"could not execute statement; SQL [n/a]; constraint [email\\\" of relation \\\"tbl_users]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement\"\n" +
                                            "}"
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

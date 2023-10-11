package com.sample.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Date;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 BAD_REQUEST: Handle exception when the request invalid parameter
     *
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public void constraintViolationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 401 UNAUTHORIZED: Handle exception when the request unauthenticated
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(UnAuthorizeException.class)
    public Error handleUnAuthorizeException(UnAuthorizeException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(UNAUTHORIZED.value());
        error.setError(UNAUTHORIZED.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }

    /**
     * 403 FORBIDDEN: Handle exception when the request incorrect level of authorization to use a specific resource.
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ForbiddenException.class)
    public Error handleAccessForbiddenException(ForbiddenException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(FORBIDDEN.value());
        error.setError(FORBIDDEN.getReasonPhrase());
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
    @ExceptionHandler(NotFoundException.class)
    public Error handleResourceNotFoundException(NotFoundException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(NOT_FOUND.value());
        error.setError(NOT_FOUND.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }

    /**
     * Handle exception when update object was failure
     *
     * @param e
     * @param request
     * @return error
     */
    @ExceptionHandler(NotAcceptableException.class)
    public Error handleNotAcceptableException(NotAcceptableException e, WebRequest request) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(NOT_ACCEPTABLE.value());
        error.setError(NOT_ACCEPTABLE.getReasonPhrase());
        error.setMessages(e.getMessage());

        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Error handleValidationException(MethodArgumentNotValidException e, WebRequest request) {
        String eMessage = e.getMessage();
        int start = eMessage.lastIndexOf("[");
        int end = eMessage.lastIndexOf("]");
        eMessage = eMessage.substring(start + 1, end - 1);

        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(NOT_ACCEPTABLE.value());
        error.setError("Invalid parameter");
        error.setMessages(eMessage);

        return error;
    }

    /**
     * Handle exception when object is duplicated
     *
     * @param e
     * @param request
     * @return error
     */
    @ExceptionHandler(DuplicatedException.class)
    public Error handleDuplicatedException(DuplicatedException e, WebRequest request) {
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
    public Error handleCommonException(Exception e, WebRequest request, HttpServletResponse response) {
        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));

        if (e instanceof NullPointerException) {
            error.setStatus(BAD_REQUEST.value());
            error.setError(BAD_REQUEST.getReasonPhrase());
            error.setMessages(e.getMessage());
            response.setStatus(BAD_REQUEST.value());
        } else if ("Bad credentials".equals(e.getMessage())) {
            error.setStatus(FORBIDDEN.value());
            error.setError(FORBIDDEN.getReasonPhrase());
            error.setMessages("Access denied");
            response.setStatus(FORBIDDEN.value());
        } else {
            error.setStatus(INTERNAL_SERVER_ERROR.value());
            error.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
            error.setMessages(e.getMessage());
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
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

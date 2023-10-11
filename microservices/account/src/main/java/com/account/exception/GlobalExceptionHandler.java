package com.account.exception;

import lombok.Data;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.springframework.http.HttpStatus.*;

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
    public Error handleValidationException(MethodArgumentNotValidException e, WebRequest request) {
        String eMessage = e.getMessage();
        int start = eMessage.lastIndexOf("[");
        int end = eMessage.lastIndexOf("]");
        eMessage = eMessage.substring(start + 1, end - 1);

        Error error = new Error();
        error.setTimestamp(new Date());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(BAD_REQUEST.value());
        error.setError("Invalid parameter");
        error.setMessages(eMessage);

        return error;
    }

    /**
     * Handle invalid data
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(BAD_REQUEST)
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
     * Handle exception when internal server error
     *
     * @param e
     * @param request
     * @return error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public Error handleCommonException(Exception e, WebRequest request, HttpServletResponse response) {
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

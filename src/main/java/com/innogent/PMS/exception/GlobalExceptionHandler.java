package com.innogent.PMS.exception;

import com.innogent.PMS.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GenericException.class)
    protected ResponseEntity<ErrorResponse> handleException(GenericException exception, WebRequest request, HttpServletRequest response) {
        log.error(exception.getMessage());
        HttpStatus httpStatus = exception.getHttpStatus();
        Integer errorCode = httpStatus.value();
        String errorMessage = exception.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage, httpStatus.name());
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception exception, WebRequest request, HttpServletRequest response) throws GenericException {
        log.error(exception.getMessage());
        return handleException(new GenericException(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR),request,response);
    }

    // progress tracking code
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}


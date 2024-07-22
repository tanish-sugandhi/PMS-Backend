package com.innogent.PMS.exception.customException;

import com.innogent.PMS.exception.GenericException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends GenericException {
    public ResourceNotFoundException(String message, HttpStatus httpStatus) {
        super(message,httpStatus);
    }
}

package com.innogent.PMS.exception.customException;

import com.innogent.PMS.exception.GenericException;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends GenericException {
    public EntityNotFoundException(String message, HttpStatus httpStatus) {
        super(message,httpStatus);
    }
}

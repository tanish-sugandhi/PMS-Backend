package com.innogent.PMS.exception.customException;

import com.innogent.PMS.exception.GenericException;
import org.springframework.http.HttpStatus;

public class NoSuchFeedbackExistsException extends GenericException {
    public NoSuchFeedbackExistsException(String message, HttpStatus httpStatus){
        super (message,httpStatus);
    }
}

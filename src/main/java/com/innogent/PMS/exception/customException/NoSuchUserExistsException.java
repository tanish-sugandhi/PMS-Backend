package com.innogent.PMS.exception.customException;

import com.innogent.PMS.exception.GenericException;
import org.springframework.http.HttpStatus;

public class NoSuchUserExistsException extends GenericException {
    public NoSuchUserExistsException(String message, HttpStatus httpStatus){
        super (message,httpStatus);}
}

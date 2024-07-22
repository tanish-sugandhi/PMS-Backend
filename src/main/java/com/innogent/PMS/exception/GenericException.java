package com.innogent.PMS.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class GenericException extends RuntimeException{
    final HttpStatus httpStatus;
    public GenericException(String message,HttpStatus httpStatus){
        super (message);
        this.httpStatus=httpStatus;
    }
    public GenericException(String message,Exception exception,HttpStatus httpStatus){
        super(message,exception);
        this.httpStatus=httpStatus;
    }
}
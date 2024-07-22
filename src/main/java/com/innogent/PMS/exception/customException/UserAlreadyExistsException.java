package com.innogent.PMS.exception.customException;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(){}
    public UserAlreadyExistsException(String message){ super(message);}
}

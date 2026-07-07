package com.example.ZenDesk.exception;

public class BusinessValidationException extends RuntimeException{
    public BusinessValidationException(String errorMsg)
    {
        super(errorMsg);
    }
    
}

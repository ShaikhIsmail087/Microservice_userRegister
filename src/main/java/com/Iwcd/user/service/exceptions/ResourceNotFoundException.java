package com.Iwcd.user.service.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    //extra property that u want to manage
    public ResourceNotFoundException()
    {
        super("Resource not found on server !!");
    }

    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}

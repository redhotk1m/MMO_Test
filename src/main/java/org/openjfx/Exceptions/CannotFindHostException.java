package org.openjfx.Exceptions;

public class CannotFindHostException extends Exception {
    public CannotFindHostException(String errorMessage, Throwable error){
        super(errorMessage);
    }
}

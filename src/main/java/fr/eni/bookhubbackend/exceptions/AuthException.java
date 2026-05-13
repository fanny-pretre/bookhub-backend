package fr.eni.bookhubbackend.exceptions;

public class AuthException extends RuntimeException{

    public AuthException(String message) {
        super(message);
    }
}

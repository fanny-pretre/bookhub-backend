package fr.eni.bookhubbackend.exceptions;

public class AuteurAlreadyExistsException extends RuntimeException{

    public AuteurAlreadyExistsException(String message) {
        super(message);
    }

}

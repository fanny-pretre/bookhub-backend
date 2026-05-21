package fr.eni.bookhubbackend.exceptions;

public class LateLoanException extends RuntimeException {
    public LateLoanException() {
        super("Emprunt impossible : retard en cours");
    }
}

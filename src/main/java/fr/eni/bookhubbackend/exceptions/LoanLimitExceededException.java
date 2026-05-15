package fr.eni.bookhubbackend.exceptions;

public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException() {
        super("Limite de 3 emprunts atteinte");
    }
}

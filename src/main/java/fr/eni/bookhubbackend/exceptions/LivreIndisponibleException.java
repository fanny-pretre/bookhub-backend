package fr.eni.bookhubbackend.exceptions;

public class LivreIndisponibleException extends RuntimeException {
    public LivreIndisponibleException() {
        super("Livre non disponible");
    }
}

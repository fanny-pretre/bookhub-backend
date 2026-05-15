package fr.eni.bookhubbackend.exceptions;

public class UtilisateurNotFoundException extends RuntimeException {

    public UtilisateurNotFoundException() {
        super("Il n'y a pas de compte à cette adresse email");
    }
}

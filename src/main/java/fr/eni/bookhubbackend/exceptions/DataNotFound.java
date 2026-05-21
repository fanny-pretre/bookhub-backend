package fr.eni.bookhubbackend.exceptions;

public class DataNotFound extends RuntimeException {

    public DataNotFound(String type, Object recherche) {
        super("L'element recherche n'a pas été trouvé - type " + type + " - critere de recherche :  " + recherche  );
    }
}

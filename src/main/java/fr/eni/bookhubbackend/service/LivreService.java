package fr.eni.bookhubbackend.service;


import fr.eni.bookhubbackend.dto.LivreDTO;

public interface LivreService {

    LivreDTO getByIsbn(String isbn);
}
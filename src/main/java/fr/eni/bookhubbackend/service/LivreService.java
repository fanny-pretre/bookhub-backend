package fr.eni.bookhubbackend.service;


import fr.eni.bookhubbackend.dto.LivreDTO;

import java.util.List;

public interface LivreService {

    List<LivreDTO> getAllLivres();

    LivreDTO getByIsbn(String isbn);
}
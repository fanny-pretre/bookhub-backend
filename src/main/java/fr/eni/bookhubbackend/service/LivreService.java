package fr.eni.bookhubbackend.service;


import fr.eni.bookhubbackend.dto.LivreDTO;

import java.util.List;

public interface LivreService {

    List<LivreDTO> getAllLivres();

    LivreDTO getByIsbn(String isbn);

    LivreDTO create(LivreDTO dto);

    LivreDTO update(String isbn, LivreDTO dto);

    void delete(String isbn);
}
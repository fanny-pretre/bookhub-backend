package fr.eni.bookhubbackend.service;


import fr.eni.bookhubbackend.dto.LivreDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LivreService {

    List<LivreDTO> getAllLivres();

    LivreDTO getByIsbn(String isbn);

    LivreDTO create(LivreDTO dto);

    LivreDTO update(String isbn, LivreDTO dto);

    void delete(String isbn);

    Page<LivreDTO> search(String search, String category, Boolean available, int page, String sort);
}
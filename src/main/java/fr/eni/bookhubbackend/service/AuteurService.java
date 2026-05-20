package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.AuteurDTO;

import java.util.List;

public interface AuteurService {

    AuteurDTO createAuteur (AuteurDTO auteurDTO);

    List<AuteurDTO> getAllAuteurs();

    void deleteAuteur(Long id);
}

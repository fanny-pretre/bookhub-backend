package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.dto.EmpruntDTO;

import java.util.List;

public interface EmpruntService {
    List<EmpruntResponseDTO> getAllEmprunts();

    EmpruntResponseDTO getById(Integer id);

    List<EmpruntResponseDTO> getLoansByUser(Integer userId);

    EmpruntResponseDTO createLoan(EmpruntDTO request);

    EmpruntResponseDTO returnLoan(Integer loanId);
}

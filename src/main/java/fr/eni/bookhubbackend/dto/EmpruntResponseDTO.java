package fr.eni.bookhubbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpruntResponseDTO {
    private Integer id;

    private String message;

    private String isbn;

    private Integer idUtilisateur;

    private LocalDate dateEmprunt;

    private LocalDate dateRetourPrevue;

    private Integer idStatut;
}

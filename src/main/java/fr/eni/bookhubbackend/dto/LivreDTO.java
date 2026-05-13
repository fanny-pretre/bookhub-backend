package fr.eni.bookhubbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivreDTO {

    private String isbn;
    private String titre;
    private String couverture;
    private String description;
    private Boolean disponibilite;
    private LocalDate dateAjout;

    private AuteurDTO auteur;

    private List<CategorieDTO> categories;
}
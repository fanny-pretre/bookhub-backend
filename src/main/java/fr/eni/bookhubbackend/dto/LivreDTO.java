package fr.eni.bookhubbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivreDTO {

    @NotBlank(message = "L'ISBN est obligatoire")
    @Size(min = 10, max = 13, message = "L'ISBN doit contenir entre 10 et 13 caractères")
    private String isbn;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    private String couverture;
    private String description;

    @NotNull(message = "La disponibilité est obligatoire")
    private Boolean disponibilite;

    @NotNull(message = "La date d'ajout est obligatoire")
    private LocalDate dateAjout;

    @NotNull(message = "L'auteur est obligatoire")
    private AuteurDTO auteur;

    private List<CategorieDTO> categories;
}
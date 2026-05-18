package fr.eni.bookhubbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuteurDTO {

    private Long id;

    @NotBlank(message = "Le nom de l'auteur est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom de l'auteur est obligatoire")
    private String prenom;
}
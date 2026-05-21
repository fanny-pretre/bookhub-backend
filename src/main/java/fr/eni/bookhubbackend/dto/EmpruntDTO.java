package fr.eni.bookhubbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpruntDTO {
    private String isbn;

    private Integer idUtilisateur;
}

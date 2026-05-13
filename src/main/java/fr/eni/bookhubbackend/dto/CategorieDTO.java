package fr.eni.bookhubbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorieDTO {

    private Long id;
    private String typeCategorie;
}
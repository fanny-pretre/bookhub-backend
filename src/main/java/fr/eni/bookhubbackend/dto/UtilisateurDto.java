package fr.eni.bookhubbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDto {

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private RoleDto role;

}

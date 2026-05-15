package fr.eni.bookhubbackend.dto.utilisateurDto;

import fr.eni.bookhubbackend.dto.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfilDto {

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

}

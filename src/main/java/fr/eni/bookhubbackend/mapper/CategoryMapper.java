package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.dto.CategorieDTO;
import fr.eni.bookhubbackend.entity.Categorie;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategorieDTO toDTO(Categorie categorie) {
        if (categorie == null) return null;

        CategorieDTO dto = new CategorieDTO();
        dto.setId(categorie.getId());
        dto.setTypeCategorie(categorie.getTypeCategorie());

        return dto;
    }

    public Categorie toEntity(CategorieDTO dto) {
        if (dto == null) return null;

        Categorie categorie = new Categorie();
        categorie.setId(dto.getId());
        categorie.setTypeCategorie(dto.getTypeCategorie());

        return categorie;
    }
}

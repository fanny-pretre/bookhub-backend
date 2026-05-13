package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.dto.AuteurDTO;
import fr.eni.bookhubbackend.entity.Auteur;
import org.springframework.stereotype.Component;

@Component
public class AuteurMapper {

    public AuteurDTO toDTO(Auteur auteur) {
        if (auteur == null) return null;

        AuteurDTO dto = new AuteurDTO();
        dto.setId(auteur.getId());
        dto.setNom(auteur.getNom());

        return dto;
    }

    public Auteur toEntity(AuteurDTO dto) {
        if (dto == null) return null;

        Auteur auteur = new Auteur();
        auteur.setId(dto.getId());
        auteur.setNom(dto.getNom());

        return auteur;
    }
}
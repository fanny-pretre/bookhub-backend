package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.entity.Emprunt;
import org.springframework.stereotype.Component;

@Component
public class EmpruntMapper {

    public EmpruntResponseDTO toDTO(Emprunt emprunt) {

        if (emprunt == null) return null;

        EmpruntResponseDTO dto = new EmpruntResponseDTO();

        dto.setId(emprunt.getId());

        dto.setIsbn(
                emprunt.getLivre() != null
                        ? emprunt.getLivre().getIsbn()
                        : null
        );

        dto.setIdUtilisateur(
                emprunt.getUtilisateur() != null
                        ? emprunt.getUtilisateur().getId()
                        : null
        );

        dto.setDateEmprunt(
                emprunt.getDateEmprunt()
        );

        dto.setDateRetourPrevue(
                emprunt.getDateRetourPrevue()
        );

        dto.setIdStatut(
                emprunt.getStatut() != null
                        ? emprunt.getStatut().getId()
                        : null
        );

        return dto;
    }

    public Emprunt toEntity(EmpruntResponseDTO dto) {

        if (dto == null) return null;

        Emprunt emprunt = new Emprunt();

        emprunt.setId(dto.getId());

        emprunt.setDateEmprunt(
                dto.getDateEmprunt()
        );

        emprunt.setDateRetourPrevue(
                dto.getDateRetourPrevue()
        );

        return emprunt;
    }
}
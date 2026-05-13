package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.dto.LivreDTO;
import fr.eni.bookhubbackend.entity.Livre;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class LivreMapper {

    private final AuteurMapper auteurMapper;
    private final CategoryMapper categorieMapper;

    public LivreMapper(AuteurMapper auteurMapper,
                       CategoryMapper categorieMapper) {
        this.auteurMapper = auteurMapper;
        this.categorieMapper = categorieMapper;
    }

    public LivreDTO toDTO(Livre livre) {
        if (livre == null) return null;

        LivreDTO dto = new LivreDTO();

        dto.setIsbn(livre.getIsbn());
        dto.setTitre(livre.getTitre());
        dto.setCouverture(livre.getCouverture());
        dto.setDescription(livre.getDescription());
        dto.setDisponibilite(livre.getDisponibilite());
        dto.setDateAjout(livre.getDateAjout());

        dto.setAuteur(auteurMapper.toDTO(livre.getAuteur()));

        dto.setCategories(
                livre.getCategories() == null
                        ? Collections.emptyList()
                        : livre.getCategories()
                          .stream()
                          .map(categorieMapper::toDTO)
                          .collect(Collectors.toList())
        );

        return dto;
    }

    public Livre toEntity(LivreDTO dto) {
        if (dto == null) return null;

        Livre livre = new Livre();

        livre.setIsbn(dto.getIsbn());
        livre.setTitre(dto.getTitre());
        livre.setCouverture(dto.getCouverture());
        livre.setDescription(dto.getDescription());
        livre.setDisponibilite(dto.getDisponibilite());
        livre.setDateAjout(dto.getDateAjout());

        livre.setAuteur(auteurMapper.toEntity(dto.getAuteur()));

        if (dto.getCategories() != null) {
            livre.setCategories(
                    dto.getCategories()
                            .stream()
                            .map(categorieMapper::toEntity)
                            .collect(Collectors.toList())
            );
        }

        return livre;
    }
}
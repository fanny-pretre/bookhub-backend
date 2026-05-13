package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.AuteurDTO;
import fr.eni.bookhubbackend.dto.CategorieDTO;
import fr.eni.bookhubbackend.dto.LivreDTO;
import fr.eni.bookhubbackend.entity.Livre;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.repository.LivreRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LivreServiceImpl implements LivreService {
    @NonNull
    private LivreRepository livreRepository;

    @Override
    public LivreDTO getByIsbn(String isbn) {

        Livre livre = livreRepository.findById(isbn)
                .orElseThrow(() -> new DataNotFound("Livre", isbn));

        // 👉 Auteur DTO
        AuteurDTO auteurDTO = new AuteurDTO(
                livre.getAuteur().getId(),
                livre.getAuteur().getNom(),
                livre.getAuteur().getPrenom()
        );

        // 👉 Livre DTO
        LivreDTO dto = new LivreDTO();
        dto.setIsbn(livre.getIsbn());
        dto.setTitre(livre.getTitre());
        dto.setCouverture(livre.getCouverture());
        dto.setDescription(livre.getDescription());
        dto.setDisponibilite(livre.getDisponibilite());
        dto.setDateAjout(livre.getDateAjout());
        dto.setAuteur(auteurDTO);

        // 👉 Catégories (ManyToMany)
        dto.setCategories(
                livre.getCategories()
                        .stream()
                        .map(c -> new CategorieDTO(c.getId(), c.getTypeCategorie()))
                        .toList()
        );

        return dto;
    }
}

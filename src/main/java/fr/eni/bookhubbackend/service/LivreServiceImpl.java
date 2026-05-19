package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.LivreDTO;
import fr.eni.bookhubbackend.entity.Auteur;
import fr.eni.bookhubbackend.entity.Categorie;
import fr.eni.bookhubbackend.entity.Livre;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.mapper.LivreMapper;
import fr.eni.bookhubbackend.repository.AuteurRepository;
import fr.eni.bookhubbackend.repository.CategorieRepository;
import fr.eni.bookhubbackend.repository.EmpruntRepository;
import fr.eni.bookhubbackend.repository.LivreRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LivreServiceImpl implements LivreService {
    @NonNull
    private LivreRepository livreRepository;
    private LivreMapper livreMapper;
    private EmpruntRepository empruntRepository;
    private AuteurRepository auteurRepository;
    private CategorieRepository categorieRepository;

    private final Integer STATUT_RETOURNE = 1;

    @Override
    public List<LivreDTO> getAllLivres() {
        return livreRepository.findAll()
                .stream()
                .map(livreMapper::toDTO)
                .toList();
    }

    @Override
    public LivreDTO getByIsbn(String isbn) {

        Livre livre = livreRepository.findById(isbn)
                .orElseThrow(() -> new DataNotFound("Livre", isbn));

        return livreMapper.toDTO(livre);
    }

    @Override
    public LivreDTO create(LivreDTO dto) {
        if (livreRepository.existsByIsbn(dto.getIsbn())) {
            throw new RuntimeException("Un livre avec l'ISBN " + dto.getIsbn() + " existe déjà.");
        }

        // 1. Convertir le DTO en entité (l'auteur et les catégories n'ont pas d'ID ici)
        Livre livre = livreMapper.toEntity(dto);

        // 2. RÉCUPÉRER L'AUTEUR RÉEL DEPUIS LA BDD
        Auteur existingAuteur = auteurRepository.findByNomAndPrenom(
                livre.getAuteur().getNom(),
                livre.getAuteur().getPrenom()
        ).orElseThrow(() -> new DataNotFound("Auteur non trouvé en base", null));

        // On remplace l'auteur "temporaire" par celui de la BDD qui possède un ID
        livre.setAuteur(existingAuteur);

        // 3. RÉCUPÉRER LES CATÉGORIES RÉELLES DEPUIS LA BDD
        if (livre.getCategories() != null && !livre.getCategories().isEmpty()) {
            List<Categorie> categoriesPersistantes = livre.getCategories().stream()
                    .map(cat -> categorieRepository.findByTypeCategorie(cat.getTypeCategorie())
                            .orElseThrow(() -> new DataNotFound("Catégorie non trouvée : " + cat.getTypeCategorie(), null)))
                    .toList();

            // On remplace la liste par les entités persistantes (avec ID)
            livre.setCategories(categoriesPersistantes);
        }

        // 4. SAUVEGARDER
        // Maintenant Hibernate est content car l'auteur et les catégories ont des IDs
        Livre saved = livreRepository.save(livre);

        return livreMapper.toDTO(saved);
    }

    @Override
    public LivreDTO update(String isbn, LivreDTO dto) {
        Livre existingLivre = livreRepository.findById(isbn)
                .orElseThrow(() ->
                        new DataNotFound("Livre", isbn)
                );

        Livre updatedLivre = livreMapper.toEntity(dto);

        updatedLivre.setIsbn(existingLivre.getIsbn());

        Livre savedLivre = livreRepository.save(updatedLivre);

        return livreMapper.toDTO(savedLivre);
    }

    @Override
    public void delete(String isbn) {

            Livre livre = livreRepository.findById(isbn)
                    .orElseThrow(() ->
                            new DataNotFound("Livre", isbn)
                    );

            boolean hasEmpruntsActifs = empruntRepository.existsByLivre_IsbnAndStatut_StatutNot(isbn, STATUT_RETOURNE);

            if(hasEmpruntsActifs) {
                throw new IllegalStateException("Impossible de supprimer un livre avec des emprunts en cours");
            }

            livreRepository.delete(livre);
        }

    @Override
    public Page<LivreDTO> search(String search, String category, Boolean available, int page, String sort) {
        Sort sortConfig = parseSort(sort);

        Pageable pageable = PageRequest.of(page, 20, sortConfig);

        return livreRepository.searchBooks(search, category, available, pageable)
                .map(livreMapper::toDTO);
    }

    private Sort parseSort(String sort) {

        if (sort == null) return Sort.by("titre").ascending();

        return switch (sort) {

            case "title,desc" -> Sort.by("titre").descending();
            case "title,asc" -> Sort.by("titre").ascending();

            case "date,desc" -> Sort.by("dateAjout").descending();
            case "date,asc" -> Sort.by("dateAjout").ascending();

            default -> Sort.by("titre").ascending();
        };
    }
}

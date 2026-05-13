package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.LivreDTO;
import fr.eni.bookhubbackend.entity.Livre;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.mapper.LivreMapper;
import fr.eni.bookhubbackend.repository.LivreRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LivreServiceImpl implements LivreService {
    @NonNull
    private LivreRepository livreRepository;
    private LivreMapper livreMapper;

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
        Livre livre = livreMapper.toEntity(dto);

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

            /**
            Potentiellement voir pour ajouter une vérification sur les emprunts
             */

            livreRepository.delete(livre);
        }
    }

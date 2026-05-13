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
}

package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.AuteurDTO;
import fr.eni.bookhubbackend.entity.Auteur;
import fr.eni.bookhubbackend.exceptions.AuteurAlreadyExistsException;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.repository.AuteurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuteurServiceImpl implements AuteurService {

    private final AuteurRepository auteurRepository;


    public AuteurServiceImpl(AuteurRepository auteurRepository) {
        this.auteurRepository = auteurRepository;
    }

    @Override
    public AuteurDTO createAuteur(AuteurDTO auteurDTO) {
        auteurRepository.findByNomAndPrenom(auteurDTO.getNom(), auteurDTO.getPrenom())
                .ifPresent(auteur -> {
                    throw new AuteurAlreadyExistsException("Cet auteur existe déjà");
                });

        Auteur auteur = new Auteur();
        auteur.setNom(auteurDTO.getNom());
        auteur.setPrenom(auteurDTO.getPrenom());

        Auteur auteurSauvegarde = auteurRepository.save(auteur);

        return new AuteurDTO(
                auteurSauvegarde.getId(),
                auteurSauvegarde.getNom(),
                auteurSauvegarde.getPrenom()
        );
    }


    @Override
    public List<AuteurDTO> getAllAuteurs() {

        List<Auteur> auteurs = auteurRepository.findAll();

        return auteurs.stream()
                .map(auteur -> new AuteurDTO(
                        auteur.getId(),
                        auteur.getNom(),
                        auteur.getPrenom()
                ))
                .toList();
    }

    @Override
    public void deleteAuteur(Long id) {

        Auteur auteur = auteurRepository.findById(id)
                .orElseThrow(() ->
                        new DataNotFound("Auteur", id));

        auteurRepository.delete(auteur);
    }
}

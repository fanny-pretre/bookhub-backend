package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.dto.EmpruntDTO;
import fr.eni.bookhubbackend.entity.Emprunt;
import fr.eni.bookhubbackend.entity.Livre;
import fr.eni.bookhubbackend.entity.Statut;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.exceptions.LateLoanException;
import fr.eni.bookhubbackend.exceptions.LivreIndisponibleException;
import fr.eni.bookhubbackend.exceptions.LoanLimitExceededException;
import fr.eni.bookhubbackend.mapper.EmpruntMapper;
import fr.eni.bookhubbackend.repository.EmpruntRepository;
import fr.eni.bookhubbackend.repository.LivreRepository;
import fr.eni.bookhubbackend.repository.StatutRepository;
import fr.eni.bookhubbackend.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
    public class EmpruntServiceImpl implements EmpruntService {

        private final EmpruntRepository empruntRepository;
        private final LivreRepository livreRepository;
        private final UtilisateurRepository utilisateurRepository;
        private final StatutRepository statutRepository;
        private final EmpruntMapper empruntMapper;

        private final Integer STATUT_RETOURNE = 1;
        private final Integer STATUT_ENCOURS = 2;


    @Override
    public List<EmpruntResponseDTO> getAllEmprunts() {
        return empruntRepository.findAll()
                .stream()
                .map(empruntMapper::toDTO)
                .toList();
    }

    @Override
    public EmpruntResponseDTO getById(Integer id) {
        Emprunt emprunt = empruntRepository.findById(id)
                .orElseThrow(() -> new DataNotFound("Emprunt", id));

        return empruntMapper.toDTO(emprunt);
    }

    @Override
    public List<EmpruntResponseDTO> getLoansByUser(Integer userId) {

        return empruntRepository.findByUtilisateurId(userId)
                .stream()
                .map(empruntMapper::toDTO)
                .toList();
    }

    @Override
    public EmpruntResponseDTO createLoan(EmpruntDTO request) {
        Livre livre = livreRepository.findById(request.getIsbn())
                .orElseThrow(() -> new RuntimeException("Livre introuvable"));

        if (!livre.getDisponibilite()) {
            throw new LivreIndisponibleException();
        }

        Utilisateur utilisateur = utilisateurRepository
                .findById(request.getIdUtilisateur())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        long activeLoans =
                empruntRepository
                        .countByUtilisateurIdAndDateRetourEffectiveIsNull(
                                utilisateur.getId()
                        );

        if (activeLoans >= 3) {
            throw new LoanLimitExceededException();
        }

        boolean hasLateLoan =
                empruntRepository
                        .existsByUtilisateurIdAndDateRetourPrevueBeforeAndDateRetourEffectiveIsNull(
                                utilisateur.getId(),
                                LocalDate.now()
                        );

        if (hasLateLoan) {
            throw new LateLoanException();
        }

        Statut statut = statutRepository.findById(STATUT_ENCOURS)
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));

        Emprunt emprunt = new Emprunt();

        emprunt.setLivre(livre);
        emprunt.setUtilisateur(utilisateur);
        emprunt.setStatut(statut);

        emprunt.setDateEmprunt(LocalDate.now());

        emprunt.setDateRetourPrevue(
                LocalDate.now().plusDays(14)
        );

        livre.setDisponibilite(false);

        Emprunt saved = empruntRepository.save(emprunt);

        return empruntMapper.toDTO(saved);
    }

    @Override
    public EmpruntResponseDTO returnLoan(Integer loanId) {
        Emprunt emprunt = empruntRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Emprunt introuvable"));

        if (emprunt.getDateRetourEffective() != null) {
            throw new RuntimeException("Emprunt déjà retourné");
        }

        emprunt.setDateRetourEffective(LocalDate.now());

        Statut statutRetourne = statutRepository.findById(STATUT_RETOURNE)
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        emprunt.setStatut(statutRetourne);

        Livre livre = emprunt.getLivre();
        livre.setDisponibilite(true);

        empruntRepository.save(emprunt);

        return empruntMapper.toDTO(emprunt);
    }
}

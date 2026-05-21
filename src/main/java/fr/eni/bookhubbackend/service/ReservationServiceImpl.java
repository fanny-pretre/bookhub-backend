package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.dto.ReservationDTO;
import fr.eni.bookhubbackend.dto.ReservationResponseDTO;
import fr.eni.bookhubbackend.entity.*;
import fr.eni.bookhubbackend.mapper.EmpruntMapper;
import fr.eni.bookhubbackend.mapper.ReservationMapper;
import fr.eni.bookhubbackend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final LivreRepository livreRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ReservationMapper reservationMapper;
    private final EmpruntRepository empruntRepository;
    private final EmpruntMapper empruntMapper;
    private final StatutRepository statutRepository;

    private final Integer STATUT_TERMINE = 1;
    private final Integer STATUT_ENCOURS = 2;
    private final Integer STATUT_ATTENTE = 4 ;



    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDTO)
                .toList();
    }

    @Override
    public ReservationResponseDTO getById(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        return reservationMapper.toDTO(reservation);
    }

    @Override
    public ReservationResponseDTO createReservation(ReservationDTO request, String email) {

        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Livre livre = livreRepository.findById(request.getIsbn())
                .orElseThrow(() -> new RuntimeException("Livre introuvable"));

        List<Integer> activeStatuses = List.of(
                STATUT_ATTENTE, STATUT_ENCOURS
        );

        long activeReservations =
                reservationRepository.countByUtilisateurIdAndStatut_IdIn(
                        user.getId(),
                        activeStatuses

                );

        if (activeReservations >= 5) {
            throw new RuntimeException("Limite de 5 réservations atteinte");
        }

        boolean alreadyExists =
                reservationRepository.existsByUtilisateurIdAndLivreIsbnAndStatut_IdIn(
                        user.getId(),
                        request.getIsbn(),
                        activeStatuses
                );

        if (alreadyExists) {
            throw new RuntimeException("Déjà réservé");
        }

        Reservation reservation = new Reservation();
        reservation.setUtilisateur(user);
        reservation.setLivre(livre);
        reservation.setDateReservation(LocalDate.now());

        if (livre.getDisponibilite()) {
            Statut statutEncours = statutRepository.findById(STATUT_ENCOURS)
                    .orElseThrow(() -> new RuntimeException("Statut introuvable"));
            reservation.setStatut(statutEncours);
            reservation.setRang(0);
        } else {
            long position = reservationRepository
                    .countByLivreIsbnAndStatut_Id(livre.getIsbn(), STATUT_ATTENTE) + 1;

            Statut statutAttente = statutRepository.findById(STATUT_ATTENTE)
                    .orElseThrow(() -> new RuntimeException("Statut introuvable"));
            reservation.setStatut(statutAttente);
            reservation.setRang((int) position);
        }

        Reservation saved = reservationRepository.save(reservation);

        ReservationResponseDTO dto = reservationMapper.toDTO(saved);
        dto.setMessage("Réservation créée");

        return dto;
    }

    @Override
    public List<ReservationResponseDTO> getUserReservations(String email) {

        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return reservationRepository.findByUtilisateur_Id(user.getId())
                .stream()
                .map(reservationMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteReservation(Integer id, String email) {

        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (!reservation.getUtilisateur().getId().equals(user.getId())) {
            throw new RuntimeException("Accès interdit");
        }

        String isbn = reservation.getLivre().getIsbn();
        Integer statutId = reservation.getStatut().getId();

        reservationRepository.delete(reservation);

        if (statutId.equals(STATUT_ATTENTE)) {
            List<Reservation> queue = reservationRepository
                    .findByLivreIsbnAndStatut_IdOrderByRangAsc(isbn, STATUT_ATTENTE);

            int rank = 1;
            for (Reservation r : queue) {
                r.setRang(rank++);
            }
            reservationRepository.saveAll(queue);
        }

    }

    @Override
    public EmpruntResponseDTO validateReservation(Integer reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        // Vérifie que la réservation est bien EN_COURS
        if (!reservation.getStatut().getId().equals(STATUT_ENCOURS)) {
            throw new RuntimeException("Seules les réservations EN_COURS peuvent être validées");
        }


        // Création de l'emprunt
        Emprunt emprunt = new Emprunt();

        emprunt.setUtilisateur(reservation.getUtilisateur());
        emprunt.setLivre(reservation.getLivre());

        emprunt.setDateEmprunt(LocalDate.now());
        emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14));

        Statut statutEncours = statutRepository.findById(STATUT_ENCOURS)
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        emprunt.setStatut(statutEncours);

        // Sauvegarde emprunt
        Emprunt savedLoan = empruntRepository.save(emprunt);

        // Passage réservation en TERMINE
        Statut statutTermine = statutRepository.findById(STATUT_TERMINE)
                .orElseThrow(() -> new RuntimeException("Statut introuvable"));
        reservation.setStatut(statutTermine);
        reservationRepository.save(reservation);

        // Livre indisponible
        Livre livre = reservation.getLivre();
        livre.setDisponibilite(false);

        livreRepository.save(livre);

        return empruntMapper.toDTO(savedLoan);
    }
}
package fr.eni.bookhubbackend.mapper;

import fr.eni.bookhubbackend.dto.ReservationResponseDTO;
import fr.eni.bookhubbackend.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponseDTO toDTO(Reservation reservation) {

        if (reservation == null) return null;

        ReservationResponseDTO dto = new ReservationResponseDTO();

        dto.setId(reservation.getId());
        dto.setIsbn(reservation.getLivre().getIsbn());
        dto.setBookTitle(reservation.getLivre().getTitre());
        dto.setStatus(reservation.getStatut().getStatut());
        dto.setQueuePosition(reservation.getRang());
        dto.setReservationDate(reservation.getDateReservation());
        dto.setMessage("Réservation confirmée");

        return dto;
    }
}
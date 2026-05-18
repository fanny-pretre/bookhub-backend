package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.dto.ReservationDTO;
import fr.eni.bookhubbackend.dto.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {
    List<ReservationResponseDTO> getAllReservations();

    ReservationResponseDTO getById(Integer id);

    ReservationResponseDTO createReservation(ReservationDTO request, String email);

    List<ReservationResponseDTO> getUserReservations(String email);

    void deleteReservation(Integer id, String email);

    EmpruntResponseDTO validateReservation(Integer reservationId);
}

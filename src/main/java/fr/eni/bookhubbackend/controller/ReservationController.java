package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.dto.ReservationDTO;
import fr.eni.bookhubbackend.dto.ReservationResponseDTO;
import fr.eni.bookhubbackend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getAllReservations() {

        List<ReservationResponseDTO> reservations =
                reservationService.getAllReservations();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Réservations récupérées", reservations)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> getById(@PathVariable Integer id) {

        try {
            ReservationResponseDTO reservation = reservationService.getById(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Réservation trouvée", reservation)
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getMyReservations(
            Authentication authentication
    ) {

        String email = authentication.getName();

        List<ReservationResponseDTO> reservations =
                reservationService.getUserReservations(email);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Mes réservations", reservations)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> createReservation(
            @Valid @RequestBody ReservationDTO request,
            Authentication authentication
    ) {

        try {
            String email = authentication.getName();

            ReservationResponseDTO response =
                    reservationService.createReservation(request, email);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Réservation créée", response));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        try {
            String email = authentication.getName();

            reservationService.deleteReservation(id, email);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Réservation supprimée", null)
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<ApiResponse<EmpruntResponseDTO>> validateReservation(
            @PathVariable Integer id) {
        try {
            EmpruntResponseDTO emprunt = reservationService.validateReservation(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Réservation validée, emprunt créé", emprunt));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

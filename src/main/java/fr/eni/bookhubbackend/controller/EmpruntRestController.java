package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.dto.EmpruntDTO;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.service.EmpruntService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class EmpruntRestController {
    private final EmpruntService empruntService;

    public EmpruntRestController(EmpruntService empruntService) {
        this.empruntService = empruntService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpruntResponseDTO>>> findAllEmprunts() {

        List<EmpruntResponseDTO> emprunts = empruntService.getAllEmprunts();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "ok", emprunts)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpruntResponseDTO>> findEmpruntById(@PathVariable Integer id) {

        try {
            EmpruntResponseDTO emprunt = empruntService.getById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Emprunt récupéré avec succès", emprunt)
            );

        } catch (DataNotFound e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Emprunt non trouvé pour l'id : " + id, null));
        }
    }

    @GetMapping("/my/{userId}")
    public ResponseEntity<ApiResponse<List<EmpruntResponseDTO>>> getLoansByUser(@PathVariable Integer userId) {

        try {
            List<EmpruntResponseDTO> loans = empruntService.getLoansByUser(userId);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Emprunts utilisateur récupérés",
                            loans
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur lors de la récupération des emprunts",
                            null
                    ));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmpruntResponseDTO>> createLoan(@RequestBody EmpruntDTO request) {

        try {
            EmpruntResponseDTO response = empruntService.createLoan(request);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Livre emprunté avec succès",
                            response
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<EmpruntResponseDTO>> returnLoan(@PathVariable Integer id) {

        try {
            EmpruntResponseDTO result = empruntService.returnLoan(id);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Livre retourné avec succès",
                            result
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null
                    ));
        }
    }
}

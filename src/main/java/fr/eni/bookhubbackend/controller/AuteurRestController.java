package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.AuteurDTO;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.service.AuteurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auteurs")
public class AuteurRestController {

    private final AuteurService auteurService;


    public AuteurRestController(AuteurService auteurService) {
        this.auteurService = auteurService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuteurDTO>> createAuteur(@Valid @RequestBody AuteurDTO auteurDTO) {
        try {
            AuteurDTO auteurCree = auteurService.createAuteur(auteurDTO);

            ApiResponse<AuteurDTO> apiResponse = new ApiResponse<>(true, "Auteur créé avec succès", auteurCree);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (RuntimeException e) {
            ApiResponse<AuteurDTO> apiResponse = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuteurDTO>>> getAllAuteurs() {

        List<AuteurDTO> auteurs = auteurService.getAllAuteurs();

        ApiResponse<List<AuteurDTO>> apiResponse =
                new ApiResponse<>(true, "Liste des auteurs récupérée", auteurs);

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAuteur(@PathVariable Long id) {

        try {
            auteurService.deleteAuteur(id);

            ApiResponse<Void> apiResponse =
                    new ApiResponse<>(true, "Auteur supprimé avec succès", null);

            return ResponseEntity.ok(apiResponse);

        } catch (DataNotFound e) {
            ApiResponse<Void> apiResponse =
                    new ApiResponse<>(false, e.getMessage(), null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }
}

package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.RegisterDto;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exception.EmailUtilisateurAlreadyExistsException;
import fr.eni.bookhubbackend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;


    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> creerUtilisateur (@Valid @RequestBody RegisterDto registerDto) {

        try {
            utilisateurService.creerUtilisateur(registerDto);
            ApiResponse<Void> apiResponse = new ApiResponse(true, "nouvel utilisateur créé", null);

            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

        } catch (EmailUtilisateurAlreadyExistsException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, "erreur de validation : email existe déjà", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

    }
}

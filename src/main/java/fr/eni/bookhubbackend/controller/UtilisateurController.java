package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.LoginDto;
import fr.eni.bookhubbackend.dto.LoginResponseDto;
import fr.eni.bookhubbackend.dto.RegisterDto;
import fr.eni.bookhubbackend.exceptions.AuthException;
import fr.eni.bookhubbackend.exceptions.EmailUtilisateurAlreadyExistsException;
import fr.eni.bookhubbackend.service.AuthentificationService;
import fr.eni.bookhubbackend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final AuthentificationService authentificationService;


    public UtilisateurController(UtilisateurService utilisateurService, AuthentificationService authentificationService) {
        this.utilisateurService = utilisateurService;
        this.authentificationService = authentificationService;
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login (@Valid @RequestBody LoginDto loginDto) {
        try {
            authentificationService.login(loginDto);
            ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>(true, "Connexion réussie, bravo BG", null);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (AuthException e) {
            ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>(false, "L'email et le mot de passe ne correspondent pas", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        }

    }


}

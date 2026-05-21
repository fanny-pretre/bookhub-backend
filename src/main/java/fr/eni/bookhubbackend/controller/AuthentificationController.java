package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.authentificationDto.LoginDto;
import fr.eni.bookhubbackend.dto.authentificationDto.LoginResponseDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.RegisterDto;
import fr.eni.bookhubbackend.exceptions.AuthException;
import fr.eni.bookhubbackend.exceptions.EmailUtilisateurAlreadyExistsException;
import fr.eni.bookhubbackend.exceptions.UtilisateurNotFoundException;
import fr.eni.bookhubbackend.security.TokenBlacklistService;
import fr.eni.bookhubbackend.service.AuthentificationService;
import fr.eni.bookhubbackend.service.UtilisateurService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthentificationController {

    private final UtilisateurService utilisateurService;
    private final AuthentificationService authentificationService;
    private final TokenBlacklistService tokenBlacklistService;


    public AuthentificationController(UtilisateurService utilisateurService, AuthentificationService authentificationService, TokenBlacklistService tokenBlacklistService) {
        this.utilisateurService = utilisateurService;
        this.authentificationService = authentificationService;
        this.tokenBlacklistService = tokenBlacklistService;
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

            LoginResponseDto loginResponseDto = authentificationService.login(loginDto);
            ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>(true, "Connexion réussie, bravo BG", loginResponseDto);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

        } catch (AuthException | UtilisateurNotFoundException e) {
            ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>(false, "L'email ou mot de passe incorrect", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Aucun token fourni", null));
        }

        String token = authHeader.substring(7);
        tokenBlacklistService.blacklist(token);

        return ResponseEntity.ok(new ApiResponse<>(true, "Déconnexion réussie", null));
    }


}

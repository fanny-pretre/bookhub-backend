package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.utilisateurDto.ProfilDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.UpdateMdpDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.UpdateProfilDto;
import fr.eni.bookhubbackend.exceptions.AuthException;
import fr.eni.bookhubbackend.exceptions.EmailUtilisateurAlreadyExistsException;
import fr.eni.bookhubbackend.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfilDto>> getProfil(@PathVariable Integer id, Authentication authentication) {

        try {
            utilisateurService.verifierAccesUtilisateur(id, authentication);
            ProfilDto profil = utilisateurService.getProfilById(id);
            ApiResponse<ProfilDto> apiResponse = new ApiResponse<>(true, "Profil récupéré avec succès", profil);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (AccessDeniedException e) {
            ApiResponse<ProfilDto> apiResponse = new ApiResponse<>(false, "Accès interdit à ce profil", null);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateProfil (@PathVariable Integer id,
                                                             @Valid @RequestBody UpdateProfilDto updateProfilDto,
                                                             Authentication authentication) {
        try {
            utilisateurService.verifierAccesUtilisateur(id, authentication);
            utilisateurService.modifierProfil(id, updateProfilDto);

            ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Profil mis à jour avec succès", null);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (AccessDeniedException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Accès interdit à ce profil", null);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);

        } catch (EmailUtilisateurAlreadyExistsException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Erreur de validation : email déjà existant", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updateMotDePasse (@PathVariable Integer id,
                                                               @Valid @RequestBody UpdateMdpDto updateMdpDto,
                                                               Authentication authentication) {
        try {
            utilisateurService.verifierAccesUtilisateur(id, authentication);
            utilisateurService.updateMotDePasse(id, updateMdpDto);

            ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Mot de passe mis à jour avec succès", null);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (AccessDeniedException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Accès interdit à ce profil", null);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);

        } catch (AuthException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, e.getMessage(), null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompte (@PathVariable Integer id,
                                                           Authentication authentication) {
        try {
            utilisateurService.verifierAccesUtilisateur(id, authentication);
            utilisateurService.deleteCompte(id);

            ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Compte supprimé avec succès", null);

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (AccessDeniedException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Accès interdit à ce profil", null);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
        } catch (AuthException e) {
            ApiResponse<Void> apiResponse = new ApiResponse<>(false, e.getMessage(), null
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }
}

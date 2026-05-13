package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.LoginDto;
import fr.eni.bookhubbackend.dto.LoginResponseDto;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exceptions.AuthException;
import fr.eni.bookhubbackend.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthentificationServiceImpl implements AuthentificationService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthentificationServiceImpl(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        boolean mdpOk = passwordEncoder.matches(
                loginDto.getMdp(),
                utilisateur.getMdp()
        );

        if (!mdpOk) {
            throw new AuthException("Email ou mot de passe incorrect");
        }

        return new LoginResponseDto(utilisateur.getRole().getTypeRole(), "Connexion réussie");
    }
}

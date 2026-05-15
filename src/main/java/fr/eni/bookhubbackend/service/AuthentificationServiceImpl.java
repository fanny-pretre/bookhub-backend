package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.LoginDto;
import fr.eni.bookhubbackend.dto.LoginResponseDto;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exceptions.AuthException;
import fr.eni.bookhubbackend.repository.UtilisateurRepository;
import fr.eni.bookhubbackend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthentificationServiceImpl implements AuthentificationService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthentificationServiceImpl(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new AuthException("Email ou mot de passe incorrect"));

        boolean mdpOk = passwordEncoder.matches(
                loginDto.getMdp(),
                utilisateur.getMdp()
        );

        if (!mdpOk) {
            throw new AuthException("Email ou mot de passe incorrect");
        }

        String role = utilisateur.getRole().getTypeRole();

        String token = jwtService.generateToken(utilisateur.getEmail(), role);

        return new LoginResponseDto(token, role, "Connexion réussie");
    }
}

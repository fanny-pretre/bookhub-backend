package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.utilisateurDto.ProfilDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.RegisterDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.UpdateMdpDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.UpdateProfilDto;
import fr.eni.bookhubbackend.entity.Role;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exceptions.AuthException;
import fr.eni.bookhubbackend.exceptions.EmailUtilisateurAlreadyExistsException;
import fr.eni.bookhubbackend.repository.RoleRepository;
import fr.eni.bookhubbackend.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;


@Service
public class UtilisateurServiceImpl implements UtilisateurService  {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Integer ID_ROLE_USER = 1;
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Utilisateur creerUtilisateur(RegisterDto registerDto) {

        if (utilisateurRepository.existsByEmail(registerDto.getEmail())) {
            throw new EmailUtilisateurAlreadyExistsException();
        }

        Utilisateur utilisateur = new Utilisateur();
        BeanUtils.copyProperties(registerDto, utilisateur);

        utilisateur.setMdp(passwordEncoder.encode(registerDto.getMdp()));

        Role roleUser = roleRepository.findById(ID_ROLE_USER)
                .orElseThrow();

        utilisateur.setRole(roleUser);

        try {
            return utilisateurRepository.save(utilisateur);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailUtilisateurAlreadyExistsException();
        }
    }

    @Override
    public void verifierAccesUtilisateur(Integer id, Authentication authentication) {
        String email = authentication.getName();

        Utilisateur utilisateurConnecte = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Aucun utilisateur trouvé"));

        if (!utilisateurConnecte.getId().equals(id)) {
            throw new AccessDeniedException("Accès interdit à ce profil");
        }
    }

    @Override
    public ProfilDto getProfilById(Integer id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        return new ProfilDto(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getTelephone()
        );
    }

    @Override
    public void modifierProfil(Integer id, UpdateProfilDto updateProfilDto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        boolean emailModifie = !utilisateur.getEmail().equals(updateProfilDto.getEmail());

        if (emailModifie && utilisateurRepository.existsByEmail(updateProfilDto.getEmail())) {
            throw new EmailUtilisateurAlreadyExistsException();
        }

        utilisateur.setNom(updateProfilDto.getNom());
        utilisateur.setPrenom(updateProfilDto.getPrenom());
        utilisateur.setTelephone(updateProfilDto.getTelephone());
        utilisateur.setEmail(updateProfilDto.getEmail());

        try {
            utilisateurRepository.save(utilisateur);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailUtilisateurAlreadyExistsException();
        }
    }

    @Override
    public void updateMotDePasse(Integer id, UpdateMdpDto updateMdpDto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(updateMdpDto.getOldPassword(), utilisateur.getMdp())) {
            throw new AuthException("Ancien mot de passe incorrect");
        }

        if (!updateMdpDto.getNewPassword().matches(PASSWORD_REGEX)) {
            throw new AuthException("Le mot de passe ne respecte pas la politique de sécurité");
        }

        if (!updateMdpDto.getNewPassword().equals(updateMdpDto.getConfirmPassword())) {
            throw new AuthException("Les mots de passe ne correspondent pas");
        }

        utilisateur.setMdp(
                passwordEncoder.encode(updateMdpDto.getNewPassword())
        );

        utilisateurRepository.save(utilisateur);

    }

    @Override
    public void deleteCompte(Integer id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        utilisateurRepository.delete(utilisateur);

    }


}

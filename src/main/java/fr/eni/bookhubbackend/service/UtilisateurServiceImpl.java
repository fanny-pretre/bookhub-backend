package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.RegisterDto;
import fr.eni.bookhubbackend.entity.Role;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exceptions.EmailUtilisateurAlreadyExistsException;
import fr.eni.bookhubbackend.repository.RoleRepository;
import fr.eni.bookhubbackend.repository.UtilisateurRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UtilisateurServiceImpl implements UtilisateurService  {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Integer ID_ROLE_USER = 1;

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
}

package fr.eni.bookhubbackend.security;

import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookhubUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public BookhubUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utilisateur non trouvé"));

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMdp())
                .roles(utilisateur.getRole().getTypeRole())
                .build();
    }
}

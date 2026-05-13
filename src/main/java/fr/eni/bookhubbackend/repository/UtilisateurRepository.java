package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    boolean existsByEmail(String email);

    Optional<Utilisateur> findByEmail (String email);
}

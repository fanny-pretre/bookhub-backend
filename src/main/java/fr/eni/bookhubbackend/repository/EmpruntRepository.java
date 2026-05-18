package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.Emprunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Integer> {
    List<Emprunt> findByUtilisateurId(Integer idUtilisateur);

    long countByUtilisateurIdAndDateRetourEffectiveIsNull(Integer utilisateurId);

    boolean existsByLivre_IsbnAndStatut_StatutNot(String isbn, Integer idStatut);

    boolean existsByUtilisateurIdAndDateRetourPrevueBeforeAndDateRetourEffectiveIsNull(
            Integer utilisateurId,
            LocalDate date
    );
}

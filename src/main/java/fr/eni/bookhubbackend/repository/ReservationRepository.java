package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository
        extends JpaRepository<Reservation, Integer> {

    long countByUtilisateurIdAndStatut_IdIn(Integer userId, List<Integer> statutIds);

    boolean existsByUtilisateurIdAndLivreIsbnAndStatut_IdIn(Integer userId, String isbn, List<Integer> statutIds);

    long countByLivreIsbnAndStatut_Id(String isbn, Integer idStatut);

    List<Reservation> findByUtilisateur_Id(Integer userId);

    List<Reservation> findByLivreIsbnAndStatut_IdOrderByRangAsc(String isbn, Integer idStatut);
}
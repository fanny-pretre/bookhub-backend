package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutRepository  extends JpaRepository<Statut, Integer> {
}

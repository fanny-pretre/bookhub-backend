package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivreRepository extends JpaRepository<Livre, String> {

}
package fr.eni.bookhubbackend.repository;


import fr.eni.bookhubbackend.entity.Auteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuteurRepository extends JpaRepository<Auteur, Long> {

}

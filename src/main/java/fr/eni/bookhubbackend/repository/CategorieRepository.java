package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Optional<Categorie> findByTypeCategorie(String typeCategorie);
}


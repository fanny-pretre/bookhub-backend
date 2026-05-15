package fr.eni.bookhubbackend.repository;

import fr.eni.bookhubbackend.entity.Livre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LivreRepository extends JpaRepository<Livre, String> {
    boolean existsByIsbn(String isbn);

    @Query("""
SELECT DISTINCT l FROM Livre l
LEFT JOIN l.auteur a
WHERE
(:search IS NULL OR
 LOWER(l.titre) LIKE LOWER(CONCAT('%', :search, '%')) OR
 LOWER(l.isbn) LIKE LOWER(CONCAT('%', :search, '%')) OR
 LOWER(a.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR
 LOWER(a.prenom) LIKE LOWER(CONCAT('%', :search, '%'))
)
AND (:available IS NULL OR l.disponibilite = :available)
AND (:category IS NULL OR EXISTS (
    SELECT 1 FROM Livre l2
    JOIN l2.categories c
    WHERE l2 = l
    AND LOWER(c.typeCategorie) = LOWER(:category)
))
""")
    Page<Livre> searchBooks(
            @Param("search") String search,
            @Param("category") String category,
            @Param("available") Boolean available,
            Pageable pageable
    );
}
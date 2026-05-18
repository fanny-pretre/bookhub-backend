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

    @Query(value = """
        SELECT DISTINCT l.isbn, l.id_auteur, l.couverture, l.date_ajout,
                        l.description, l.disponibilite, l.titre
        FROM livre l
        LEFT JOIN auteur a ON a.id = l.id_auteur
        WHERE
            (CAST(:search AS varchar) IS NULL OR
             LOWER(l.titre)    LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR
             LOWER(l.isbn)     LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR
             LOWER(a.nom)      LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR
             LOWER(a.prenom)   LIKE LOWER('%' || CAST(:search AS varchar) || '%')
            )
        AND (CAST(:available AS boolean) IS NULL OR l.disponibilite = CAST(:available AS boolean))
        AND (CAST(:category AS varchar) IS NULL OR EXISTS (
            SELECT 1 FROM disposer d
            JOIN categorie c ON c.id = d.id_categorie
            WHERE d.isbn = l.isbn
            AND LOWER(c.type_categorie) = LOWER(CAST(:category AS varchar))
        ))
        """,
            countQuery = """
        SELECT COUNT(DISTINCT l.isbn)
        FROM livre l
        LEFT JOIN auteur a ON a.id = l.id_auteur
        WHERE
            (CAST(:search AS varchar) IS NULL OR
             LOWER(l.titre)    LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR
             LOWER(l.isbn)     LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR
             LOWER(a.nom)      LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR
             LOWER(a.prenom)   LIKE LOWER('%' || CAST(:search AS varchar) || '%')
            )
        AND (CAST(:available AS boolean) IS NULL OR l.disponibilite = CAST(:available AS boolean))
        AND (CAST(:category AS varchar) IS NULL OR EXISTS (
            SELECT 1 FROM disposer d
            JOIN categorie c ON c.id = d.id_categorie
            WHERE d.isbn = l.isbn
            AND LOWER(c.type_categorie) = LOWER(CAST(:category AS varchar))
        ))
        """,
            nativeQuery = true)
    Page<Livre> searchBooks(
            @Param("search") String search,
            @Param("category") String category,
            @Param("available") Boolean available,
            Pageable pageable
    );
}
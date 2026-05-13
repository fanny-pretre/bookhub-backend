package fr.eni.bookhubbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LIVRE")
public class Livre {

    @Id
    @Column(length = 13, nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false, length = 255)
    private String titre;

    @Column(length = 255)
    private String couverture;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean disponibilite;

    @Column(nullable = false)
    private LocalDate dateAjout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_auteur", nullable = false)
    private Auteur auteur;

    @ManyToMany
    @JoinTable(
            name = "DISPOSER",
            joinColumns = @JoinColumn(name = "isbn"),
            inverseJoinColumns = @JoinColumn(name = "id_categorie")
    )
    private List<Categorie> categories;
}

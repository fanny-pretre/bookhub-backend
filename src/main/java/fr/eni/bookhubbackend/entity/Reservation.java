package fr.eni.bookhubbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RESERVATION")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_reservation")
    private LocalDate dateReservation;

    private Integer rang;

    @ManyToOne
    @JoinColumn(name = "id_statut")
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "isbn")
    private Livre livre;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;
}

package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.dto.ReservationDTO;
import fr.eni.bookhubbackend.dto.ReservationResponseDTO;
import fr.eni.bookhubbackend.entity.*;
import fr.eni.bookhubbackend.mapper.EmpruntMapper;
import fr.eni.bookhubbackend.mapper.ReservationMapper;
import fr.eni.bookhubbackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private LivreRepository livreRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private ReservationMapper reservationMapper;
    @Mock private EmpruntRepository empruntRepository;
    @Mock private EmpruntMapper empruntMapper;
    @Mock private StatutRepository statutRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Utilisateur user;
    private Livre livre;
    private Reservation reservation;
    private ReservationDTO reservationDTO;
    private ReservationResponseDTO reservationResponseDTO;

    @BeforeEach
    void setUp() {
        user = new Utilisateur();
        user.setId(1);
        user.setEmail("test@test.com");

        livre = new Livre();
        livre.setIsbn("1234567890");
        livre.setDisponibilite(false);

        Statut statutAttente = new Statut();
        statutAttente.setId(4);

        reservation = new Reservation();
        reservation.setId(10);
        reservation.setUtilisateur(user);
        reservation.setLivre(livre);
        reservation.setStatut(statutAttente);
        reservation.setRang(1);

        reservationDTO = new ReservationDTO();
        reservationDTO.setIsbn("1234567890");

        reservationResponseDTO = new ReservationResponseDTO();
    }

    @Test
    void getAllReservations_shouldReturnListOfReservationResponseDTO() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));
        when(reservationMapper.toDTO(reservation)).thenReturn(reservationResponseDTO);

        List<ReservationResponseDTO> result = reservationService.getAllReservations();

        assertEquals(1, result.size());

        verify(reservationRepository).findAll();
        verify(reservationMapper).toDTO(reservation);
    }

    @Test
    void getById_shouldReturnReservation_whenReservationExists() {
        when(reservationRepository.findById(10)).thenReturn(Optional.of(reservation));
        when(reservationMapper.toDTO(reservation)).thenReturn(reservationResponseDTO);

        ReservationResponseDTO result = reservationService.getById(10);

        assertNotNull(result);

        verify(reservationRepository).findById(10);
        verify(reservationMapper).toDTO(reservation);
    }

    @Test
    void getById_shouldThrowRuntimeException_whenReservationDoesNotExist() {
        when(reservationRepository.findById(10)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.getById(10));

        assertEquals("Réservation introuvable", exception.getMessage());

        verify(reservationRepository).findById(10);
        verifyNoInteractions(reservationMapper);
    }

    @Test
    void createReservation_shouldCreateWaitingReservation_whenLivreUnavailable() {
        Statut statutAttente = new Statut();
        statutAttente.setId(4);

        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(reservationRepository.countByUtilisateurIdAndStatut_IdIn(eq(1), anyList()))
                .thenReturn(0L);
        when(reservationRepository.existsByUtilisateurIdAndLivreIsbnAndStatut_IdIn(eq(1), eq("1234567890"), anyList()))
                .thenReturn(false);
        when(reservationRepository.countByLivreIsbnAndStatut_Id("1234567890", 4))
                .thenReturn(2L);
        when(statutRepository.findById(4)).thenReturn(Optional.of(statutAttente));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationResponseDTO);

        ReservationResponseDTO result = reservationService.createReservation(reservationDTO, "test@test.com");

        assertNotNull(result);
        assertEquals("Réservation créée", result.getMessage());

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());

        Reservation savedReservation = captor.getValue();

        assertEquals(user, savedReservation.getUtilisateur());
        assertEquals(livre, savedReservation.getLivre());
        assertEquals(4, savedReservation.getStatut().getId());
        assertEquals(3, savedReservation.getRang());
    }

    @Test
    void createReservation_shouldCreateOngoingReservation_whenLivreAvailable() {
        livre.setDisponibilite(true);

        Statut statutEncours = new Statut();
        statutEncours.setId(2);

        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(reservationRepository.countByUtilisateurIdAndStatut_IdIn(eq(1), anyList()))
                .thenReturn(0L);
        when(reservationRepository.existsByUtilisateurIdAndLivreIsbnAndStatut_IdIn(eq(1), eq("1234567890"), anyList()))
                .thenReturn(false);
        when(statutRepository.findById(2)).thenReturn(Optional.of(statutEncours));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationMapper.toDTO(any(Reservation.class))).thenReturn(reservationResponseDTO);

        ReservationResponseDTO result = reservationService.createReservation(reservationDTO, "test@test.com");

        assertNotNull(result);
        assertEquals("Réservation créée", result.getMessage());

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());

        Reservation savedReservation = captor.getValue();

        assertEquals(2, savedReservation.getStatut().getId());
        assertEquals(0, savedReservation.getRang());
    }

    @Test
    void createReservation_shouldThrowException_whenUserHasAlreadyFiveReservations() {
        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(reservationRepository.countByUtilisateurIdAndStatut_IdIn(eq(1), anyList()))
                .thenReturn(5L);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(reservationDTO, "test@test.com"));

        assertEquals("Limite de 5 réservations atteinte", exception.getMessage());

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_shouldThrowException_whenReservationAlreadyExists() {
        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(reservationRepository.countByUtilisateurIdAndStatut_IdIn(eq(1), anyList()))
                .thenReturn(1L);
        when(reservationRepository.existsByUtilisateurIdAndLivreIsbnAndStatut_IdIn(eq(1), eq("1234567890"), anyList()))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(reservationDTO, "test@test.com"));

        assertEquals("Déjà réservé", exception.getMessage());

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void getUserReservations_shouldReturnReservationsForUser() {
        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(reservationRepository.findByUtilisateur_Id(1)).thenReturn(List.of(reservation));
        when(reservationMapper.toDTO(reservation)).thenReturn(reservationResponseDTO);

        List<ReservationResponseDTO> result = reservationService.getUserReservations("test@test.com");

        assertEquals(1, result.size());

        verify(utilisateurRepository).findByEmail("test@test.com");
        verify(reservationRepository).findByUtilisateur_Id(1);
    }

    @Test
    void deleteReservation_shouldDeleteReservationAndRecalculateRanks_whenReservationIsWaiting() {
        Reservation reservation2 = new Reservation();
        reservation2.setRang(3);

        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(reservationRepository.findById(10)).thenReturn(Optional.of(reservation));
        when(reservationRepository.findByLivreIsbnAndStatut_IdOrderByRangAsc("1234567890", 4))
                .thenReturn(List.of(reservation2));

        reservationService.deleteReservation(10, "test@test.com");

        assertEquals(1, reservation2.getRang());

        verify(reservationRepository).delete(reservation);
        verify(reservationRepository).saveAll(List.of(reservation2));
    }

    @Test
    void deleteReservation_shouldThrowException_whenUserIsNotOwner() {
        Utilisateur anotherUser = new Utilisateur();
        anotherUser.setId(99);

        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(anotherUser));
        when(reservationRepository.findById(10)).thenReturn(Optional.of(reservation));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.deleteReservation(10, "test@test.com"));

        assertEquals("Accès interdit", exception.getMessage());

        verify(reservationRepository, never()).delete(any());
    }

    @Test
    void validateReservation_shouldCreateLoanAndFinishReservation() {
        Statut statutEncours = new Statut();
        statutEncours.setId(2);

        Statut statutTermine = new Statut();
        statutTermine.setId(1);

        reservation.setStatut(statutEncours);

        Emprunt emprunt = new Emprunt();
        EmpruntResponseDTO empruntResponseDTO = new EmpruntResponseDTO();

        when(reservationRepository.findById(10)).thenReturn(Optional.of(reservation));
        when(statutRepository.findById(2)).thenReturn(Optional.of(statutEncours));
        when(statutRepository.findById(1)).thenReturn(Optional.of(statutTermine));
        when(empruntRepository.save(any(Emprunt.class))).thenReturn(emprunt);
        when(empruntMapper.toDTO(emprunt)).thenReturn(empruntResponseDTO);

        EmpruntResponseDTO result = reservationService.validateReservation(10);

        assertNotNull(result);
        assertEquals(1, reservation.getStatut().getId());
        assertFalse(livre.getDisponibilite());

        verify(empruntRepository).save(any(Emprunt.class));
        verify(reservationRepository).save(reservation);
        verify(livreRepository).save(livre);
    }

    @Test
    void validateReservation_shouldThrowException_whenReservationIsNotOngoing() {
        Statut statutAttente = new Statut();
        statutAttente.setId(4);
        reservation.setStatut(statutAttente);

        when(reservationRepository.findById(10)).thenReturn(Optional.of(reservation));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reservationService.validateReservation(10));

        assertEquals("Seules les réservations EN_COURS peuvent être validées", exception.getMessage());

        verify(empruntRepository, never()).save(any());
        verify(livreRepository, never()).save(any());
    }
}
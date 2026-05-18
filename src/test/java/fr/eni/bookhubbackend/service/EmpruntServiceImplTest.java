package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.EmpruntDTO;
import fr.eni.bookhubbackend.dto.EmpruntResponseDTO;
import fr.eni.bookhubbackend.entity.Emprunt;
import fr.eni.bookhubbackend.entity.Livre;
import fr.eni.bookhubbackend.entity.Statut;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.exceptions.LateLoanException;
import fr.eni.bookhubbackend.exceptions.LivreIndisponibleException;
import fr.eni.bookhubbackend.exceptions.LoanLimitExceededException;
import fr.eni.bookhubbackend.mapper.EmpruntMapper;
import fr.eni.bookhubbackend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpruntServiceImplTest {

    @Mock private EmpruntRepository empruntRepository;
    @Mock private LivreRepository livreRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private StatutRepository statutRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private EmpruntMapper empruntMapper;

    @InjectMocks
    private EmpruntServiceImpl empruntService;

    private Livre livre;
    private Utilisateur utilisateur;
    private Statut statutEncours;
    private Statut statutRetourne;
    private Emprunt emprunt;
    private EmpruntDTO empruntDTO;
    private EmpruntResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        livre = new Livre();
        livre.setIsbn("1234567890");
        livre.setDisponibilite(true);

        utilisateur = new Utilisateur();
        utilisateur.setId(1);

        statutEncours = new Statut();
        statutEncours.setId(2);

        statutRetourne = new Statut();
        statutRetourne.setId(1);

        emprunt = new Emprunt();
        emprunt.setId(10);
        emprunt.setLivre(livre);
        emprunt.setUtilisateur(utilisateur);
        emprunt.setStatut(statutEncours);

        empruntDTO = new EmpruntDTO();
        empruntDTO.setIsbn("1234567890");
        empruntDTO.setIdUtilisateur(1);

        responseDTO = new EmpruntResponseDTO();
    }

    @Test
    void getAllEmprunts_shouldReturnListOfEmpruntResponseDTO() {
        when(empruntRepository.findAll()).thenReturn(List.of(emprunt));
        when(empruntMapper.toDTO(emprunt)).thenReturn(responseDTO);

        List<EmpruntResponseDTO> result = empruntService.getAllEmprunts();

        assertEquals(1, result.size());

        verify(empruntRepository).findAll();
        verify(empruntMapper).toDTO(emprunt);
    }

    @Test
    void getById_shouldReturnEmprunt_whenEmpruntExists() {
        when(empruntRepository.findById(10)).thenReturn(Optional.of(emprunt));
        when(empruntMapper.toDTO(emprunt)).thenReturn(responseDTO);

        EmpruntResponseDTO result = empruntService.getById(10);

        assertNotNull(result);

        verify(empruntRepository).findById(10);
        verify(empruntMapper).toDTO(emprunt);
    }

    @Test
    void getById_shouldThrowDataNotFound_whenEmpruntDoesNotExist() {
        when(empruntRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(DataNotFound.class, () -> empruntService.getById(10));

        verify(empruntRepository).findById(10);
        verifyNoInteractions(empruntMapper);
    }

    @Test
    void getLoansByUser_shouldReturnUserLoans() {
        when(empruntRepository.findByUtilisateurId(1)).thenReturn(List.of(emprunt));
        when(empruntMapper.toDTO(emprunt)).thenReturn(responseDTO);

        List<EmpruntResponseDTO> result = empruntService.getLoansByUser(1);

        assertEquals(1, result.size());

        verify(empruntRepository).findByUtilisateurId(1);
        verify(empruntMapper).toDTO(emprunt);
    }

    @Test
    void createLoan_shouldCreateLoan_whenAllRulesAreValid() {
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(empruntRepository.countByUtilisateurIdAndDateRetourEffectiveIsNull(1))
                .thenReturn(0L);
        when(empruntRepository.existsByUtilisateurIdAndDateRetourPrevueBeforeAndDateRetourEffectiveIsNull(
                eq(1),
                any(LocalDate.class)
        )).thenReturn(false);
        when(statutRepository.findById(2)).thenReturn(Optional.of(statutEncours));
        when(empruntRepository.save(any(Emprunt.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(empruntMapper.toDTO(any(Emprunt.class))).thenReturn(responseDTO);

        EmpruntResponseDTO result = empruntService.createLoan(empruntDTO);

        assertNotNull(result);
        assertFalse(livre.getDisponibilite());

        ArgumentCaptor<Emprunt> captor = ArgumentCaptor.forClass(Emprunt.class);
        verify(empruntRepository).save(captor.capture());

        Emprunt savedEmprunt = captor.getValue();

        assertEquals(livre, savedEmprunt.getLivre());
        assertEquals(utilisateur, savedEmprunt.getUtilisateur());
        assertEquals(statutEncours, savedEmprunt.getStatut());
        assertEquals(LocalDate.now(), savedEmprunt.getDateEmprunt());
        assertEquals(LocalDate.now().plusDays(14), savedEmprunt.getDateRetourPrevue());
    }

    @Test
    void createLoan_shouldThrowRuntimeException_whenLivreNotFound() {
        when(livreRepository.findById("1234567890")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empruntService.createLoan(empruntDTO));

        assertEquals("Livre introuvable", exception.getMessage());

        verify(empruntRepository, never()).save(any());
    }

    @Test
    void createLoan_shouldThrowLivreIndisponibleException_whenLivreUnavailable() {
        livre.setDisponibilite(false);

        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));

        assertThrows(LivreIndisponibleException.class,
                () -> empruntService.createLoan(empruntDTO));

        verify(utilisateurRepository, never()).findById(anyInt());
        verify(empruntRepository, never()).save(any());
    }

    @Test
    void createLoan_shouldThrowRuntimeException_whenUtilisateurNotFound() {
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(utilisateurRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empruntService.createLoan(empruntDTO));

        assertEquals("Utilisateur introuvable", exception.getMessage());

        verify(empruntRepository, never()).save(any());
    }

    @Test
    void createLoan_shouldThrowLoanLimitExceededException_whenUserHasThreeActiveLoans() {
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(empruntRepository.countByUtilisateurIdAndDateRetourEffectiveIsNull(1))
                .thenReturn(3L);

        assertThrows(LoanLimitExceededException.class,
                () -> empruntService.createLoan(empruntDTO));

        verify(empruntRepository, never()).save(any());
    }

    @Test
    void createLoan_shouldThrowLateLoanException_whenUserHasLateLoan() {
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(empruntRepository.countByUtilisateurIdAndDateRetourEffectiveIsNull(1))
                .thenReturn(1L);
        when(empruntRepository.existsByUtilisateurIdAndDateRetourPrevueBeforeAndDateRetourEffectiveIsNull(
                eq(1),
                any(LocalDate.class)
        )).thenReturn(true);

        assertThrows(LateLoanException.class,
                () -> empruntService.createLoan(empruntDTO));

        verify(empruntRepository, never()).save(any());
    }

    @Test
    void createLoan_shouldThrowRuntimeException_whenStatutNotFound() {
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(empruntRepository.countByUtilisateurIdAndDateRetourEffectiveIsNull(1))
                .thenReturn(0L);
        when(empruntRepository.existsByUtilisateurIdAndDateRetourPrevueBeforeAndDateRetourEffectiveIsNull(
                eq(1),
                any(LocalDate.class)
        )).thenReturn(false);
        when(statutRepository.findById(2)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empruntService.createLoan(empruntDTO));

        assertEquals("Statut introuvable", exception.getMessage());

        verify(empruntRepository, never()).save(any());
    }

    @Test
    void returnLoan_shouldReturnLoan_whenLoanExistsAndIsNotAlreadyReturned() {
        when(empruntRepository.findById(10)).thenReturn(Optional.of(emprunt));
        when(statutRepository.findById(1)).thenReturn(Optional.of(statutRetourne));
        when(empruntRepository.save(emprunt)).thenReturn(emprunt);
        when(empruntMapper.toDTO(emprunt)).thenReturn(responseDTO);

        EmpruntResponseDTO result = empruntService.returnLoan(10);

        assertNotNull(result);
        assertEquals(LocalDate.now(), emprunt.getDateRetourEffective());
        assertEquals(statutRetourne, emprunt.getStatut());
        assertTrue(livre.getDisponibilite());

        verify(empruntRepository).save(emprunt);
        verify(empruntMapper).toDTO(emprunt);
    }

    @Test
    void returnLoan_shouldThrowRuntimeException_whenLoanNotFound() {
        when(empruntRepository.findById(10)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empruntService.returnLoan(10));

        assertEquals("Emprunt introuvable", exception.getMessage());

        verify(empruntRepository, never()).save(any());
    }

    @Test
    void returnLoan_shouldThrowRuntimeException_whenLoanAlreadyReturned() {
        emprunt.setDateRetourEffective(LocalDate.now());

        when(empruntRepository.findById(10)).thenReturn(Optional.of(emprunt));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empruntService.returnLoan(10));

        assertEquals("Emprunt déjà retourné", exception.getMessage());

        verify(statutRepository, never()).findById(anyInt());
        verify(empruntRepository, never()).save(any());
    }

    @Test
    void returnLoan_shouldThrowRuntimeException_whenStatutNotFound() {
        when(empruntRepository.findById(10)).thenReturn(Optional.of(emprunt));
        when(statutRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> empruntService.returnLoan(10));

        assertEquals("Statut introuvable", exception.getMessage());

        verify(empruntRepository, never()).save(any());
    }
}
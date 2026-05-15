package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.utilisateurDto.RegisterDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.UpdateMdpDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.UpdateProfilDto;
import fr.eni.bookhubbackend.entity.Role;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exceptions.AuthException;
import fr.eni.bookhubbackend.exceptions.EmailUtilisateurAlreadyExistsException;
import fr.eni.bookhubbackend.repository.RoleRepository;
import fr.eni.bookhubbackend.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import fr.eni.bookhubbackend.dto.utilisateurDto.ProfilDto;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceImplTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UtilisateurServiceImpl utilisateurService;

    private RegisterDto registerDto;

    @BeforeEach
    void setUp() {

        registerDto = new RegisterDto();
        registerDto.setNom("Dupont");
        registerDto.setPrenom("Emma");
        registerDto.setEmail("emma@test.fr");
        registerDto.setMdp("MotDePasse123!");
    }

    @Test
    void creerUtilisateur_shouldCreateUser() {

        Role role = new Role();
        role.setId(1);

        when(utilisateurRepository.existsByEmail(registerDto.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(registerDto.getMdp()))
                .thenReturn("hashedPassword");

        when(roleRepository.findById(1))
                .thenReturn(Optional.of(role));

        when(utilisateurRepository.save(any(Utilisateur.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Utilisateur result = utilisateurService.creerUtilisateur(registerDto);

        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
        assertEquals("Emma", result.getPrenom());
        assertEquals("emma@test.fr", result.getEmail());
        assertEquals("hashedPassword", result.getMdp());
        assertEquals(role, result.getRole());

        verify(utilisateurRepository).save(any(Utilisateur.class));
    }

    @Test
    void creerUtilisateur_shouldThrowExceptionWhenEmailAlreadyExists() {

        when(utilisateurRepository.existsByEmail(registerDto.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailUtilisateurAlreadyExistsException.class,
                () -> utilisateurService.creerUtilisateur(registerDto)
        );

        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void verifierAccesUtilisateur_shouldNotThrowWhenUserIsOwner() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setEmail("emma@test.fr");

        when(authentication.getName()).thenReturn("emma@test.fr");
        when(utilisateurRepository.findByEmail("emma@test.fr"))
                .thenReturn(Optional.of(utilisateur));

        assertDoesNotThrow(() -> utilisateurService.verifierAccesUtilisateur(1, authentication));
    }

    @Test
    void verifierAccesUtilisateur_shouldThrowWhenUserIsNotOwner() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setEmail("emma@test.fr");

        when(authentication.getName()).thenReturn("emma@test.fr");
        when(utilisateurRepository.findByEmail("emma@test.fr"))
                .thenReturn(Optional.of(utilisateur));

        assertThrows(
                AccessDeniedException.class,
                () -> utilisateurService.verifierAccesUtilisateur(2, authentication)
        );
    }

    @Test
    void getProfilById_shouldReturnProfilDto() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Emma");
        utilisateur.setEmail("emma@test.fr");
        utilisateur.setTelephone("0611223344");

        when(utilisateurRepository.findById(1))
                .thenReturn(Optional.of(utilisateur));

        ProfilDto result = utilisateurService.getProfilById(1);

        assertEquals(1, result.getId());
        assertEquals("Dupont", result.getNom());
        assertEquals("Emma", result.getPrenom());
        assertEquals("emma@test.fr", result.getEmail());
        assertEquals("0611223344", result.getTelephone());
    }

    @Test
    void modifierProfil_shouldUpdateProfil() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Emma");
        utilisateur.setEmail("emma@test.fr");
        utilisateur.setTelephone("0611223344");

        UpdateProfilDto updateProfilDto = new UpdateProfilDto();
        updateProfilDto.setNom("Martin");
        updateProfilDto.setPrenom("Emma");
        updateProfilDto.setEmail("emma.new@test.fr");
        updateProfilDto.setTelephone("0699887766");

        when(utilisateurRepository.findById(1))
                .thenReturn(Optional.of(utilisateur));

        when(utilisateurRepository.existsByEmail("emma.new@test.fr"))
                .thenReturn(false);

        utilisateurService.modifierProfil(1, updateProfilDto);

        assertEquals("Martin", utilisateur.getNom());
        assertEquals("Emma", utilisateur.getPrenom());
        assertEquals("emma.new@test.fr", utilisateur.getEmail());
        assertEquals("0699887766", utilisateur.getTelephone());

        verify(utilisateurRepository).save(utilisateur);
    }

    @Test
    void modifierProfil_shouldThrowExceptionWhenEmailAlreadyExists() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setEmail("emma@test.fr");

        UpdateProfilDto updateProfilDto = new UpdateProfilDto();
        updateProfilDto.setNom("Martin");
        updateProfilDto.setPrenom("Emma");
        updateProfilDto.setEmail("deja@test.fr");
        updateProfilDto.setTelephone("0699887766");

        when(utilisateurRepository.findById(1))
                .thenReturn(Optional.of(utilisateur));

        when(utilisateurRepository.existsByEmail("deja@test.fr"))
                .thenReturn(true);

        assertThrows(
                EmailUtilisateurAlreadyExistsException.class,
                () -> utilisateurService.modifierProfil(1, updateProfilDto)
        );

        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void updateMotDePasse_shouldUpdatePassword() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setMdp("oldHashedPassword");

        UpdateMdpDto updateMdpDto = new UpdateMdpDto();
        updateMdpDto.setOldPassword("AncienMotDePasse123!");
        updateMdpDto.setNewPassword("NouveauMotDePasse123!");
        updateMdpDto.setConfirmPassword("NouveauMotDePasse123!");

        when(utilisateurRepository.findById(1))
                .thenReturn(Optional.of(utilisateur));

        when(passwordEncoder.matches("AncienMotDePasse123!", "oldHashedPassword"))
                .thenReturn(true);

        when(passwordEncoder.encode("NouveauMotDePasse123!"))
                .thenReturn("newHashedPassword");

        utilisateurService.updateMotDePasse(1, updateMdpDto);

        assertEquals("newHashedPassword", utilisateur.getMdp());

        verify(utilisateurRepository).save(utilisateur);
    }

    @Test
    void updateMotDePasse_shouldThrowExceptionWhenOldPasswordIsWrong() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setMdp("oldHashedPassword");

        UpdateMdpDto updateMdpDto = new UpdateMdpDto();
        updateMdpDto.setOldPassword("MauvaisMotDePasse123!");
        updateMdpDto.setNewPassword("NouveauMotDePasse123!");
        updateMdpDto.setConfirmPassword("NouveauMotDePasse123!");

        when(utilisateurRepository.findById(1))
                .thenReturn(Optional.of(utilisateur));

        when(passwordEncoder.matches("MauvaisMotDePasse123!", "oldHashedPassword"))
                .thenReturn(false);

        assertThrows(
                AuthException.class,
                () -> utilisateurService.updateMotDePasse(1, updateMdpDto)
        );

        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void updateMotDePasse_shouldThrowExceptionWhenConfirmPasswordIsDifferent() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setMdp("oldHashedPassword");

        UpdateMdpDto updateMdpDto = new UpdateMdpDto();
        updateMdpDto.setOldPassword("AncienMotDePasse123!");
        updateMdpDto.setNewPassword("NouveauMotDePasse123!");
        updateMdpDto.setConfirmPassword("DifferentMotDePasse123!");

        when(utilisateurRepository.findById(1))
                .thenReturn(Optional.of(utilisateur));

        when(passwordEncoder.matches("AncienMotDePasse123!", "oldHashedPassword"))
                .thenReturn(true);

        assertThrows(
                AuthException.class,
                () -> utilisateurService.updateMotDePasse(1, updateMdpDto)
        );

        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    void deleteCompte_shouldDeleteUser() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1);

        when(utilisateurRepository.findById(1))
                .thenReturn(Optional.of(utilisateur));

        utilisateurService.deleteCompte(1);

        verify(utilisateurRepository).delete(utilisateur);
    }
}

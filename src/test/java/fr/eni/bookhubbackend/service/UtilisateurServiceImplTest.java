package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.RegisterDto;
import fr.eni.bookhubbackend.entity.Role;
import fr.eni.bookhubbackend.entity.Utilisateur;
import fr.eni.bookhubbackend.exceptions.EmailUtilisateurAlreadyExistsException;
import fr.eni.bookhubbackend.repository.RoleRepository;
import fr.eni.bookhubbackend.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}

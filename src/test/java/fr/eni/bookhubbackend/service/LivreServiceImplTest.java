package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.LivreDTO;
import fr.eni.bookhubbackend.entity.Livre;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.mapper.LivreMapper;
import fr.eni.bookhubbackend.repository.EmpruntRepository;
import fr.eni.bookhubbackend.repository.LivreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivreServiceImplTest {

    @Mock
    private LivreRepository livreRepository;

    @Mock
    private LivreMapper livreMapper;

    @Mock
    private EmpruntRepository empruntRepository;

    @InjectMocks
    private LivreServiceImpl livreService;

    private Livre livre;
    private LivreDTO livreDTO;

    @BeforeEach
    void setUp() {
        livre = new Livre();
        livre.setIsbn("1234567890");

        livreDTO = new LivreDTO();
        livreDTO.setIsbn("1234567890");
    }

    @Test
    void getAllLivres_shouldReturnListOfLivreDTO() {
        // Arrange
        when(livreRepository.findAll()).thenReturn(List.of(livre));
        when(livreMapper.toDTO(livre)).thenReturn(livreDTO);

        // Act
        List<LivreDTO> result = livreService.getAllLivres();

        // Assert
        assertEquals(1, result.size());
        assertEquals("1234567890", result.get(0).getIsbn());

        verify(livreRepository).findAll();
        verify(livreMapper).toDTO(livre);
    }

    @Test
    void getByIsbn_shouldReturnLivreDTO_whenLivreExists() {
        // Arrange
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(livreMapper.toDTO(livre)).thenReturn(livreDTO);

        // Act
        LivreDTO result = livreService.getByIsbn("1234567890");

        // Assert
        assertNotNull(result);
        assertEquals("1234567890", result.getIsbn());

        verify(livreRepository).findById("1234567890");
        verify(livreMapper).toDTO(livre);
    }

    @Test
    void getByIsbn_shouldThrowDataNotFound_whenLivreDoesNotExist() {
        // Arrange
        when(livreRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class, () -> livreService.getByIsbn("unknown"));

        verify(livreRepository).findById("unknown");
        verifyNoInteractions(livreMapper);
    }

    @Test
    void create_shouldSaveAndReturnLivreDTO_whenIsbnDoesNotExist() {
        // Arrange
        when(livreRepository.existsByIsbn(livreDTO.getIsbn())).thenReturn(false);
        when(livreMapper.toEntity(livreDTO)).thenReturn(livre);
        when(livreRepository.save(livre)).thenReturn(livre);
        when(livreMapper.toDTO(livre)).thenReturn(livreDTO);

        // Act
        LivreDTO result = livreService.create(livreDTO);

        // Assert
        assertNotNull(result);
        assertEquals("1234567890", result.getIsbn());

        verify(livreRepository).existsByIsbn("1234567890");
        verify(livreRepository).save(livre);
    }

    @Test
    void create_shouldThrowRuntimeException_whenIsbnAlreadyExists() {
        // Arrange
        when(livreRepository.existsByIsbn(livreDTO.getIsbn())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> livreService.create(livreDTO));

        assertEquals("Un livre avec l'ISBN 1234567890 existe déjà.", exception.getMessage());

        verify(livreRepository).existsByIsbn("1234567890");
        verify(livreRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateLivreAndKeepExistingIsbn_whenLivreExists() {
        // Arrange
        LivreDTO updateDTO = new LivreDTO();
        updateDTO.setIsbn("9999999999");

        Livre updatedLivre = new Livre();
        updatedLivre.setIsbn("9999999999");

        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(livreMapper.toEntity(updateDTO)).thenReturn(updatedLivre);
        when(livreRepository.save(updatedLivre)).thenReturn(updatedLivre);
        when(livreMapper.toDTO(updatedLivre)).thenReturn(livreDTO);

        // Act
        LivreDTO result = livreService.update("1234567890", updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("1234567890", updatedLivre.getIsbn());

        verify(livreRepository).findById("1234567890");
        verify(livreRepository).save(updatedLivre);
    }

    @Test
    void update_shouldThrowDataNotFound_whenLivreDoesNotExist() {
        // Arrange
        when(livreRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class,
                () -> livreService.update("unknown", livreDTO));

        verify(livreRepository).findById("unknown");
        verify(livreRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteLivre_whenNoActiveEmprunts() {
        // Arrange
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(empruntRepository.existsByLivre_IsbnAndStatut_StatutNot("1234567890", 1))
                .thenReturn(false);

        // Act
        livreService.delete("1234567890");

        // Assert
        verify(livreRepository).delete(livre);
    }

    @Test
    void delete_shouldThrowIllegalStateException_whenLivreHasActiveEmprunts() {
        // Arrange
        when(livreRepository.findById("1234567890")).thenReturn(Optional.of(livre));
        when(empruntRepository.existsByLivre_IsbnAndStatut_StatutNot("1234567890", 1))
                .thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> livreService.delete("1234567890"));

        assertEquals("Impossible de supprimer un livre avec des emprunts en cours", exception.getMessage());

        verify(livreRepository, never()).delete(any());
    }

    @Test
    void delete_shouldThrowDataNotFound_whenLivreDoesNotExist() {
        // Arrange
        when(livreRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFound.class,
                () -> livreService.delete("unknown"));

        verify(empruntRepository, never()).existsByLivre_IsbnAndStatut_StatutNot(anyString(), anyInt());
        verify(livreRepository, never()).delete(any());
    }

    @Test
    void search_shouldReturnPageOfLivreDTO_withDefaultSort() {
        // Arrange
        Page<Livre> livrePage = new PageImpl<>(List.of(livre));

        when(livreRepository.searchBooks(
                eq("harry"),
                eq("fantasy"),
                eq(true),
                any(Pageable.class)
        )).thenReturn(livrePage);

        when(livreMapper.toDTO(livre)).thenReturn(livreDTO);

        // Act
        Page<LivreDTO> result = livreService.search("harry", "fantasy", true, 0, null);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("1234567890", result.getContent().get(0).getIsbn());

        verify(livreRepository).searchBooks(
                eq("harry"),
                eq("fantasy"),
                eq(true),
                argThat(pageable ->
                        pageable.getPageNumber() == 0
                                && pageable.getPageSize() == 20
                                && pageable.getSort().getOrderFor("titre").isAscending()
                )
        );
    }

    @Test
    void search_shouldUseTitleDescendingSort() {
        // Arrange
        Page<Livre> livrePage = new PageImpl<>(List.of(livre));

        when(livreRepository.searchBooks(any(), any(), any(), any(Pageable.class)))
                .thenReturn(livrePage);

        when(livreMapper.toDTO(livre)).thenReturn(livreDTO);

        // Act
        livreService.search(null, null, null, 0, "title,desc");

        // Assert
        verify(livreRepository).searchBooks(
                isNull(),
                isNull(),
                isNull(),
                argThat(pageable ->
                        pageable.getSort().getOrderFor("titre").isDescending()
                )
        );
    }

    @Test
    void search_shouldUseDateAscendingSort() {
        // Arrange
        Page<Livre> livrePage = new PageImpl<>(List.of(livre));

        when(livreRepository.searchBooks(any(), any(), any(), any(Pageable.class)))
                .thenReturn(livrePage);

        when(livreMapper.toDTO(livre)).thenReturn(livreDTO);

        // Act
        livreService.search(null, null, null, 0, "date,asc");

        // Assert
        verify(livreRepository).searchBooks(
                isNull(),
                isNull(),
                isNull(),
                argThat(pageable ->
                        pageable.getSort().getOrderFor("dateAjout").isAscending()
                )
        );
    }
}
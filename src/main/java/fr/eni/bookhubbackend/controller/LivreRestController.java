package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.LivreDTO;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.service.LivreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class LivreRestController {

    private final LivreService livreService;

    public LivreRestController(LivreService livreService) {
        this.livreService = livreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LivreDTO>>> findAllLivres() {

        List<LivreDTO> livres = livreService.getAllLivres();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "ok", livres)
        );
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<ApiResponse<LivreDTO>> findLivreById(@PathVariable String isbn) {

        try {
            LivreDTO livre = livreService.getByIsbn(isbn);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "ok", livre)
            );

        } catch (DataNotFound notFound) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "Livre : " + isbn + " non trouvé", null)
            );
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LivreDTO>> create(@RequestBody LivreDTO dto) {

        try {
            LivreDTO createdLivre = livreService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Livre créé avec succès", createdLivre));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur lors de la création du livre", null));
        }
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<ApiResponse<LivreDTO>> update(@PathVariable String isbn, @RequestBody LivreDTO dto) {

        try {
            LivreDTO updatedLivre = livreService.update(isbn, dto);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Livre mis à jour avec succès", updatedLivre)
            );

        } catch (DataNotFound e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Livre : " + isbn + " non trouvé", null));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour", null));
        }
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String isbn) {

        try {
            livreService.delete(isbn);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Livre supprimé avec succès", null));

        } catch (DataNotFound e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Livre : " + isbn + " non trouvé", null));

        } catch (IllegalStateException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


}
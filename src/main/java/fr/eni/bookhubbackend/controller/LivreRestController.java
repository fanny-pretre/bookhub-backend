package fr.eni.bookhubbackend.controller;

import fr.eni.bookhubbackend.dto.LivreDTO;
import fr.eni.bookhubbackend.exceptions.DataNotFound;
import fr.eni.bookhubbackend.service.LivreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
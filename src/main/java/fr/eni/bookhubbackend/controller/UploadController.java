package fr.eni.bookhubbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            String fileName = UUID.randomUUID()
                    + "_"
                    + file.getOriginalFilename();

            Path path = Paths.get(UPLOAD_DIR + fileName);

            Files.createDirectories(path.getParent());

            Files.copy(file.getInputStream(), path);

            return ResponseEntity.ok("/uploads/" + fileName);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(e.getMessage());
        }
    }
}
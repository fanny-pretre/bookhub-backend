package fr.eni.bookhubbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {

    private Integer id;

    private String message;

    private String isbn;

    private Integer queuePosition;

    private String status;

    private String bookTitle;

    private LocalDate reservationDate;
}

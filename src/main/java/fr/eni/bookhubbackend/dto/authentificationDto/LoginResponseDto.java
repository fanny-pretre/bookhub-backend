package fr.eni.bookhubbackend.dto.authentificationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String token;
    private String role;
    private String message;
    private Integer id;

}

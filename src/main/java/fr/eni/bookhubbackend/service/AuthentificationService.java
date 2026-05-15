package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.authentificationDto.LoginDto;
import fr.eni.bookhubbackend.dto.authentificationDto.LoginResponseDto;


public interface AuthentificationService {

    LoginResponseDto login(LoginDto loginDto);
}

package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.LoginDto;
import fr.eni.bookhubbackend.dto.LoginResponseDto;


public interface AuthentificationService {

    LoginResponseDto login(LoginDto loginDto);
}

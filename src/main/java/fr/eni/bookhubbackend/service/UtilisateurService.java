package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.RegisterDto;
import fr.eni.bookhubbackend.entity.Utilisateur;

public interface UtilisateurService {

    public Utilisateur creerUtilisateur(RegisterDto registerDto);


}

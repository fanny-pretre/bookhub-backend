package fr.eni.bookhubbackend.service;

import fr.eni.bookhubbackend.dto.utilisateurDto.ProfilDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.RegisterDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.UpdateMdpDto;
import fr.eni.bookhubbackend.dto.utilisateurDto.UpdateProfilDto;
import fr.eni.bookhubbackend.entity.Utilisateur;
import org.springframework.security.core.Authentication;

public interface UtilisateurService {

    Utilisateur creerUtilisateur(RegisterDto registerDto);

    void verifierAccesUtilisateur(Integer id, Authentication authentication);

    ProfilDto getProfilById (Integer id);

    void modifierProfil (Integer id, UpdateProfilDto updateProfilDto);

    void updateMotDePasse (Integer id, UpdateMdpDto updateMdpDto);

    void deleteCompte (Integer id);

}

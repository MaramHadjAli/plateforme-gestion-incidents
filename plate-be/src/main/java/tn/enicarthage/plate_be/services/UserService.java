package tn.enicarthage.plate_be.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.enicarthage.plate_be.dtos.user.ChangePasswordDTO;
import tn.enicarthage.plate_be.dtos.user.UpdateProfileDTO;
import tn.enicarthage.plate_be.dtos.user.UserResponseDTO;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtenir l'utilisateur actuel depuis le contexte de sécurité
     */
    private Utilisateur getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur) {
            return (Utilisateur) auth.getPrincipal();
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    /**
     * Récupérer les infos de l'utilisateur actuel
     */
    public UserResponseDTO getCurrentUserInfo() {
        Utilisateur user = getCurrentUser();
        return UserResponseDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    /**
     * Mettre à jour le profil utilisateur
     */
    public UserResponseDTO updateProfile(UpdateProfileDTO updateDTO) {
        Utilisateur user = getCurrentUser();
        
        if (updateDTO.getNom() != null && !updateDTO.getNom().isBlank()) {
            user.setNom(updateDTO.getNom());
        }
        
        if (updateDTO.getTelephone() != null && !updateDTO.getTelephone().isBlank()) {
            user.setTelephone(updateDTO.getTelephone());
        }
        
        Utilisateur updated = userRepository.save(user);
        
        return UserResponseDTO.builder()
                .id(updated.getId())
                .nom(updated.getNom())
                .email(updated.getEmail())
                .telephone(updated.getTelephone())
                .role(updated.getRole().name())
                .avatarUrl(updated.getAvatarUrl())
                .build();
    }

    /**
     * Changer le mot de passe
     */
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Utilisateur user = getCurrentUser();
        
        // Vérifier que les deux nouveaux mots de passe correspondent
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new RuntimeException("Les nouveaux mots de passe ne correspondent pas");
        }
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }
        
        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Récupérer un utilisateur par email
     */
    public UserResponseDTO getUserByEmail(String email) {
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return UserResponseDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    /**
     * Supprimer le compte utilisateur
     */
    public void deleteAccount() {
        Utilisateur user = getCurrentUser();
        userRepository.delete(user);
    }

    /**
     * Mettre à jour l'URL de l'avatar
     */
    public UserResponseDTO updateAvatar(String avatarUrl) {
        Utilisateur user = getCurrentUser();
        user.setAvatarUrl(avatarUrl);
        Utilisateur updated = userRepository.save(user);
        
        return UserResponseDTO.builder()
                .id(updated.getId())
                .nom(updated.getNom())
                .email(updated.getEmail())
                .telephone(updated.getTelephone())
                .role(updated.getRole().name())
                .avatarUrl(updated.getAvatarUrl())
                .build();
    }

    /**
     * Supprimer l'avatar
     */
    public UserResponseDTO deleteAvatarUrl() {
        Utilisateur user = getCurrentUser();
        user.setAvatarUrl(null);
        Utilisateur updated = userRepository.save(user);
        
        return UserResponseDTO.builder()
                .id(updated.getId())
                .nom(updated.getNom())
                .email(updated.getEmail())
                .telephone(updated.getTelephone())
                .role(updated.getRole().name())
                .avatarUrl(updated.getAvatarUrl())
                .build();
    }
}

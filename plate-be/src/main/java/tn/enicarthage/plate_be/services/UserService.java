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
        return mapToResponse(user);
    }

    private UserResponseDTO mapToResponse(Utilisateur user) {
        UserResponseDTO.UserResponseDTOBuilder builder = UserResponseDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .twoFactorEnabled(user.isTwoFactorEnabled());

        if (user instanceof tn.enicarthage.plate_be.entities.Technicien tech) {
            builder.ticketsResolus(tech.getTicketsResolus());
            builder.noteMoyenne(tech.getNoteMoyenne());
            builder.totalPoints(tech.getTotalPoints());
        } else if (user instanceof tn.enicarthage.plate_be.entities.Demandeur dem) {
            builder.noteMoyenne((double) dem.getNoteMoyenne());
            builder.totalPoints(dem.getTotalPoint());
        }

        return builder.build();
    }

    /**
     * Mettre à jour le profil utilisateur
     */
    public UserResponseDTO updateProfile(UpdateProfileDTO updateDTO) {
        Utilisateur user = getCurrentUser();
        
        if (updateDTO.getNom() != null && !updateDTO.getNom().isBlank()) {
            user.setNom(updateDTO.getNom());
        }

        if (updateDTO.getPrenom() != null && !updateDTO.getPrenom().isBlank()) {
            user.setPrenom(updateDTO.getPrenom());
        }
        
        if (updateDTO.getTelephone() != null && !updateDTO.getTelephone().isBlank()) {
            user.setTelephone(updateDTO.getTelephone());
        }
        
        Utilisateur updated = userRepository.save(user);
        return mapToResponse(updated);
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
        user.setMotPassee(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Récupérer un utilisateur par email
     */
    public UserResponseDTO getUserByEmail(String email) {
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return mapToResponse(user);
    }

    /**
     * Supprimer le compte utilisateur
     */
    public void deleteAccount() {
        Utilisateur user = getCurrentUser();
        userRepository.delete(user);
    }

    public UserResponseDTO uploadAvatar(org.springframework.web.multipart.MultipartFile file) {
        Utilisateur user = getCurrentUser();
        try {
            String fileName = "avatar_" + user.getId() + "_" + System.currentTimeMillis() + ".png";
            java.nio.file.Path rootPath = java.nio.file.Paths.get("").toAbsolutePath();
            java.nio.file.Path uploadPath = rootPath.resolve("uploads/avatars");
            
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }
            
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            user.setAvatarUrl("/api/users/avatar/view/" + fileName);
            Utilisateur updated = userRepository.save(user);
            return mapToResponse(updated);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de l'avatar : " + e.getMessage(), e);
        }
    }

    public UserResponseDTO deleteAvatar() {
        Utilisateur user = getCurrentUser();
        user.setAvatarUrl(null);
        Utilisateur updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    public UserResponseDTO toggle2FA() {
        Utilisateur user = getCurrentUser();
        user.setTwoFactorEnabled(!user.isTwoFactorEnabled());
        Utilisateur updated = userRepository.save(user);
        return mapToResponse(updated);
    }
}

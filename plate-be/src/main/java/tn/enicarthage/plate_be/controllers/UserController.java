package tn.enicarthage.plate_be.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.enicarthage.plate_be.dtos.user.ChangePasswordDTO;
import tn.enicarthage.plate_be.dtos.user.UpdateProfileDTO;
import tn.enicarthage.plate_be.dtos.user.UserResponseDTO;
import tn.enicarthage.plate_be.services.UserService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final String UPLOAD_DIR = "uploads/avatars/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Récupérer les infos de l'utilisateur actuel
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserInfo());
    }

    /**
     * Mettre à jour le profil
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody UpdateProfileDTO updateDTO) {
        return ResponseEntity.ok(userService.updateProfile(updateDTO));
    }

    /**
     * Changer le mot de passe
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(changePasswordDTO);
        return ResponseEntity.ok("Mot de passe changé avec succès");
    }

    /**
     * Upload l'avatar de l'utilisateur
     */
    @PostMapping("/avatar/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // Vérifier la taille du fichier
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fichier trop volumineux (max 5MB)"));
            }

            // Vérifier le type de fichier
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier doit être une image"));
            }

            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Sauvegarder le fichier
            Files.write(filePath, file.getBytes());

            // Construire l'URL de l'avatar
            String avatarUrl = "/uploads/avatars/" + fileName;

            // Mettre à jour l'avatar dans la base de données
            UserResponseDTO updatedUser = userService.updateAvatar(avatarUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Avatar uploadé avec succès");
            response.put("avatarUrl", avatarUrl);
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de l'upload du fichier"));
        }
    }

    /**
     * Supprimer l'avatar de l'utilisateur
     */
    @DeleteMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAvatar() {
        try {
            UserResponseDTO updatedUser = userService.deleteAvatarUrl();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Avatar supprimé avec succès");
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de la suppression de l'avatar"));
        }
    }

    /**
     * Récupérer les infos d'un utilisateur par email
     */
    @GetMapping("/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Supprimer le compte utilisateur
     */
    @DeleteMapping("/delete-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteAccount() {
        userService.deleteAccount();
        return ResponseEntity.ok("Compte supprimé avec succès");
    }
}




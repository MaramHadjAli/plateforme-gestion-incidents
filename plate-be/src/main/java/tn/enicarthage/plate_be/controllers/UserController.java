package tn.enicarthage.plate_be.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.plate_be.dtos.user.ChangePasswordDTO;
import tn.enicarthage.plate_be.dtos.user.UpdateProfileDTO;
import tn.enicarthage.plate_be.dtos.user.UserResponseDTO;
import tn.enicarthage.plate_be.services.UserService;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

    @PostMapping("/avatar/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> uploadAvatar(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(userService.uploadAvatar(file));
    }

    @DeleteMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> deleteAvatar() {
        return ResponseEntity.ok(userService.deleteAvatar());
    }

    @PostMapping("/toggle-2fa")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> toggle2FA() {
        return ResponseEntity.ok(userService.toggle2FA());
    }

    @GetMapping("/avatar/view/{fileName}")
    public ResponseEntity<org.springframework.core.io.Resource> viewAvatar(@PathVariable String fileName) {
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get("").toAbsolutePath();
            java.nio.file.Path filePath = rootPath.resolve("uploads/avatars").resolve(fileName);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (java.net.MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

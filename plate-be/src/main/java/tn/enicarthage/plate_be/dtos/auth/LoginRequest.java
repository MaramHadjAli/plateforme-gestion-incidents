
package tn.enicarthage.plate_be.dtos.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    
    @NotBlank(message = "L'email est obligatoire")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    // Constructeur par défaut (Indispensable pour Jackson)
    public LoginRequest() {}

    // Getters et Setters manuels (Plus sûr que Lombok dans certains environnements)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

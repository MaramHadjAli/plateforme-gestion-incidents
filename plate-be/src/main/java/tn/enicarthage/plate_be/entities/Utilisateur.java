package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
public class Utilisateur implements UserDetails {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String nom;

    @Getter
    @Setter
    @Column(unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    private boolean enabled = true;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private ROLE role;

    public Utilisateur() {}

    @Override
    public String getPassword() {
        return password;
    }

    public static UtilisateurBuilder builder() {
        return new UtilisateurBuilder();
    }

    public static class UtilisateurBuilder {
        private String nom;
        private String email;
        private String password;
        private ROLE role;
        private boolean enabled = true;

        public UtilisateurBuilder nom(String nom) { this.nom = nom; return this; }
        public UtilisateurBuilder email(String email) { this.email = email; return this; }
        public UtilisateurBuilder password(String password) { this.password = password; return this; }
        public UtilisateurBuilder role(ROLE role) { this.role = role; return this; }
        public UtilisateurBuilder enabled(boolean enabled) { this.enabled = enabled; return this; }
        
        public Utilisateur build() {
            Utilisateur user = new Utilisateur();
            user.setNom(nom);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            user.setEnabled(enabled);
            return user;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }


    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
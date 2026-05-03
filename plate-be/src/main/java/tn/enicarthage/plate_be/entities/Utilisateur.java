package tn.enicarthage.plate_be.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@Table(name = "utilisateurs")
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "mot_passee", nullable = false)
    private String motPassee;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateInscription;

    private boolean isActive;

    @Temporal(TemporalType.DATE)
    private Date desactivationDate;

    @Column(length = 500)
    private String desactivationReason;

    @Column(length = 20)
    private String telephone;

    @Enumerated(EnumType.STRING)
    private ROLE role;

    @Builder.Default
    private boolean enabled = true;

    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String motPassee,
                       Date dateInscription, boolean isActive, ROLE role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motPassee = motPassee;
        this.dateInscription = dateInscription;
        this.isActive = isActive;
        this.role = role;
        this.enabled = true;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.enabled = active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return motPassee;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
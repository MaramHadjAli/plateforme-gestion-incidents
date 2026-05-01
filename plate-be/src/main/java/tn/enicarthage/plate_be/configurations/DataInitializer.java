package tn.enicarthage.plate_be.configurations;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.entities.Adminstrateur;
import tn.enicarthage.plate_be.entities.Demandeur;
import tn.enicarthage.plate_be.entities.Technicien;
import tn.enicarthage.plate_be.repositories.*;

import java.util.Objects;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            DemandeurRepository demandeurRepository,
            TechnicienRepository technicienRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Seed Admin
            if (userRepository.findByEmail("admin@enicarthage.tn").isEmpty()) {
                Adminstrateur admin = new Adminstrateur();
                admin.setNom("Admin ENICarthage");
                admin.setEmail("admin@enicarthage.tn");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(ROLE.ADMIN);
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("[DataInitializer] Compte admin créé: admin@enicarthage.tn / Admin@123");
            }

            // Seed Technician
            if (userRepository.findByEmail("tech@enicarthage.tn").isEmpty()) {
                Technicien tech = new Technicien();
                tech.setNom("Technicien Pro");
                tech.setEmail("tech@enicarthage.tn");
                tech.setPassword(passwordEncoder.encode("Tech@123"));
                tech.setRole(ROLE.TECHNICIEN);
                tech.setEnabled(true);
                tech.setSpecialite("Informatique");
                technicienRepository.save(tech);
                System.out.println("[DataInitializer] Compte technicien créé: tech@enicarthage.tn / Tech@123");
            }

            // Seed User (Demandeur)
            if (userRepository.findByEmail("user@enicarthage.tn").isEmpty()) {
                Demandeur user = new Demandeur();
                user.setNom("Jean Dupont");
                user.setEmail("user@enicarthage.tn");
                user.setPassword(passwordEncoder.encode("User@123"));
                user.setRole(ROLE.DEMANDEUR);
                user.setEnabled(true);
                demandeurRepository.save(user);
                System.out.println("[DataInitializer] Compte utilisateur créé: user@enicarthage.tn / User@123");
            }
        };
    }
}

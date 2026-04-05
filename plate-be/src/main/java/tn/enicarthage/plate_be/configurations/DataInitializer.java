package tn.enicarthage.plate_be.configurations;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.repositories.UserRepository;

import java.util.Objects;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@enicarthage.tn").isEmpty()) {
                Utilisateur admin = Utilisateur.builder()
                        .nom("Admin ENICarthage")
                        .email("admin@enicarthage.tn")
                        .password(Objects.requireNonNull(passwordEncoder.encode("Admin@123")))
                        .role(ROLE.ADMIN)
                        .enabled(true)
                        .build();
                userRepository.save(admin);
                System.out.println("[DataInitializer] Compte admin créé: admin@enicarthage.tn / Admin@123");
            }
        };
    }
}

package tn.enicarthage.plate_be.configurations;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicarthage.plate_be.entities.ROLE;
import tn.enicarthage.plate_be.entities.Utilisateur;
import tn.enicarthage.plate_be.entities.*;
import tn.enicarthage.plate_be.repositories.*;
import tn.enicarthage.plate_be.services.BadgeService;

import java.util.Date;
import java.util.Objects;

@Configuration
public class DataInitializer {

    private final UserRepository userRepository;
    private final SalleRepository salleRepository;
    private final EquipementRepository equipementRepository;
    private final PasswordEncoder passwordEncoder;
    private final BadgeService badgeService;
    private final TicketRepository ticketRepository;
    private final HistoriqueMaintenanceRepository historiqueMaintenanceRepository;
    private final MaintenancePreventiveRepository maintenancePreventiveRepository;
    private final NotificationRepository notificationRepository;

    public DataInitializer(UserRepository userRepository,
                           SalleRepository salleRepository,
                           EquipementRepository equipementRepository,
                           PasswordEncoder passwordEncoder,
                           BadgeService badgeService,
                           TicketRepository ticketRepository,
                           HistoriqueMaintenanceRepository historiqueMaintenanceRepository,
                           MaintenancePreventiveRepository maintenancePreventiveRepository,
                           NotificationRepository notificationRepository
                          ) {
        this.userRepository = userRepository;
        this.salleRepository = salleRepository;
        this.equipementRepository = equipementRepository;
        this.passwordEncoder = passwordEncoder;
        this.badgeService = badgeService;
        this.ticketRepository = ticketRepository;
        this.historiqueMaintenanceRepository = historiqueMaintenanceRepository;
        this.maintenancePreventiveRepository = maintenancePreventiveRepository;
        this.notificationRepository = notificationRepository;
    }

    @Bean
    CommandLineRunner initData() {
        return args -> {
            System.out.println("[DataInitializer] Nettoyage des anciennes données terminé");

            historiqueMaintenanceRepository.deleteAll();
            maintenancePreventiveRepository.deleteAll();
            notificationRepository.deleteAll();
            ticketRepository.deleteAll();
            equipementRepository.deleteAll();
            salleRepository.deleteAll();

            System.out.println("[DataInitializer] Nettoyage terminé");

            badgeService.initializeBadges();
            System.out.println("[DataInitializer] Badges initialisés");

            userRepository.findByEmail("admin@enicarthage.tn").ifPresentOrElse(
                    admin -> {},
                    () -> {
                        Utilisateur admin = Utilisateur.builder()
                                .nom("Admin")
                                .prenom("Principal")
                                .email("admin@enicarthage.tn")
                                .motPassee(passwordEncoder.encode("Admin123!"))
                                .role(ROLE.ADMIN)
                                .enabled(true)
                                .isActive(true)
                                .dateInscription(new Date())
                                .build();
                        userRepository.save(admin);
                    });

            createSalle(salleRepository, "ANN-CONF", "Salle des conférences", "Annexe", "0");
            createSalle(salleRepository, "ANN-LECT", "Salle de lecture", "Annexe", "0");
            createSalle(salleRepository, "ANN-SPORT", "Salle de sport", "Annexe", "0");
            createSalle(salleRepository, "SL12", "Salle SL12", "Annexe", "1");
            for (int i = 20; i <= 36; i++) {
                createSalle(salleRepository, "S" + i, "Salle S" + i, "Annexe", "1");
            }

            // LOCAUX : PRINCIPALE
            createSalle(salleRepository, "ADMIN", "Administration", "Principale", "0");
            createSalle(salleRepository, "DEPT", "Départements", "Principale", "1");
            createSalle(salleRepository, "SCOL", "Scolarité", "Principale", "0");
            createSalle(salleRepository, "BIBLIO", "Bibliothèque", "Principale", "0");
            createSalle(salleRepository, "BORDRE", "Bureau d'ordre", "Principale", "0");
            createSalle(salleRepository, "STAGE", "Service de stage", "Principale", "0");
            createSalle(salleRepository, "INFO", "Service informatique", "Principale", "1");

            // Salles Spécifiques Principale
            String[] specific = {"SI", "S2", "S3", "LI", "L2", "L3"};
            for (String s : specific) {
                createSalle(salleRepository, s, "Salle " + s, "Principale", "0");
            }

            // Blocs 100, 200, 300
            for (int i = 101; i <= 111; i++) createSalle(salleRepository, String.valueOf(i), "Salle " + i, "Principale", "1");
            for (int i = 201; i <= 211; i++) createSalle(salleRepository, String.valueOf(i), "Salle " + i, "Principale", "2");
            for (int i = 301; i <= 311; i++) createSalle(salleRepository, String.valueOf(i), "Salle " + i, "Principale", "3");

            System.out.println("[DataInitializer] Salles de l'université créées (Annexe & Principale)");

            // --- EQUIPEMENTS ---
            // Informatique de Base (Administration & Salles Info)
            addEquip(equipementRepository, salleRepository, "PC-ADM-01", "Ordinateur de bureau HP", "Ordinateur de bureau", "ADMIN");
            addEquip(equipementRepository, salleRepository, "PC-ADM-02", "Ordinateur Portable Dell (Admin)", "Ordinateur portable", "ADMIN");
            addEquip(equipementRepository, salleRepository, "PRT-ADM-01", "Imprimante Laser Multifonction", "Imprimante", "ADMIN");
            addEquip(equipementRepository, salleRepository, "PHO-ADM-01", "Photocopieur Ricoh", "Photocopieur", "ADMIN");

            // Salle 101 (Pour vos tests rapides)
            addEquip(equipementRepository, salleRepository, "PC-101-01", "Unité centrale (Poste 01)", "Unité centrale", "101");
            addEquip(equipementRepository, salleRepository, "PC-101-02", "Unité centrale (Poste 02)", "Unité centrale", "101");
            addEquip(equipementRepository, salleRepository, "PROJ-101-01", "Vidéoprojecteur plafond", "Vidéoprojecteur", "101");
            addEquip(equipementRepository, salleRepository, "HP-101-01", "Haut-parleurs muraux", "Haut-parleurs", "101");

            // Salle des conférences (Annexe)
            addEquip(equipementRepository, salleRepository, "PROJ-CONF-01", "Vidéoprojecteur Epson High-Res", "Vidéoprojecteur", "ANN-CONF");
            addEquip(equipementRepository, salleRepository, "MIC-CONF-01", "Microphone Shure", "Microphone", "ANN-CONF");
            addEquip(equipementRepository, salleRepository, "WEB-CONF-01", "Webcam Logitech 4K", "Webcam", "ANN-CONF");

            // Robotique & Spécialisé
            addEquip(equipementRepository, salleRepository, "API-DEPT-01", "Automate Schneider Electric", "API", "DEPT");
            addEquip(equipementRepository, salleRepository, "ROB-DEPT-01", "Robot mobile TurtleBot", "Robotique", "DEPT");
            addEquip(equipementRepository, salleRepository, "BRS-DEPT-01", "Bras robotique UR5", "Robotique", "DEPT");

            System.out.println("[DataInitializer] Catalogue d'équipements enrichi déployé");
            System.out.println("[DataInitializer] Initialisation terminée");
        };
    }

    private void createSalle(SalleRepository repo, String id, String nom, String bat, String etage) {
        Salle s = new Salle();
        s.setIdSalle(id);
        s.setNomSalle(nom);
        s.setBatiment(bat);
        s.setEtage(etage);
        repo.save(s);
    }

    private void addEquip(EquipementRepository repo, SalleRepository sRepo, String id, String nom, String type, String idSalle) {
        sRepo.findById(idSalle).ifPresent(salle -> {
            Equipement e = new Equipement();
            e.setIdEquipement(id);
            e.setNomEquipement(nom);
            e.setType(type);
            e.setEtat(ETAT_EQUIPEMENT.FONCTIONNELLE);
            e.setSalle(salle);
            repo.save(e);
        });
    }
}
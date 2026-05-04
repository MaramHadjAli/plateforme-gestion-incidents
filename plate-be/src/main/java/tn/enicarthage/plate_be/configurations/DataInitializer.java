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

@Configuration
public class DataInitializer {

    private final UserRepository userRepository;
    private final SalleRepository salleRepository;
    private final EquipementRepository equipementRepository;
    private final PasswordEncoder passwordEncoder;
    private final BadgeService badgeService;
    private final TicketRepository ticketRepository;
    private final NotificationRepository notificationRepository;
    private final tn.enicarthage.plate_be.websocket.NotificationService notificationService;

    public DataInitializer(UserRepository userRepository,
                           SalleRepository salleRepository,
                           EquipementRepository equipementRepository,
                           PasswordEncoder passwordEncoder,
                           BadgeService badgeService,
                           TicketRepository ticketRepository,
                           NotificationRepository notificationRepository,
                           tn.enicarthage.plate_be.websocket.NotificationService notificationService
                          ) {
        this.userRepository = userRepository;
        this.salleRepository = salleRepository;
        this.equipementRepository = equipementRepository;
        this.passwordEncoder = passwordEncoder;
        this.badgeService = badgeService;
        this.ticketRepository = ticketRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @Bean
    CommandLineRunner initData() {
        return args -> {
            System.out.println("[DataInitializer] Nettoyage des anciennes données terminé");

            // historiqueMaintenanceRepository.deleteAll();
            // maintenancePreventiveRepository.deleteAll();
            // notificationRepository.deleteAll();
            // ticketRepository.deleteAll();
            // equipementRepository.deleteAll();
            // salleRepository.deleteAll();

            System.out.println("[DataInitializer] Nettoyage terminé");

            badgeService.initializeBadges();
            System.out.println("[DataInitializer] Badges initialisés");

            userRepository.findByEmail("admin@enicarthage.tn").ifPresentOrElse(
                    admin -> {
                        admin.setNom("Administrateur");
                        admin.setPrenom("Enicarthage");
                        userRepository.save(admin);
                    },
                    () -> {
                        Utilisateur admin = Utilisateur.builder()
                                .email("admin@enicarthage.tn")
                                .nom("Administrateur")
                                .prenom("Enicarthage")
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

            // --- TICKETS FICTIFS ---
            seedFictitiousTickets();

            System.out.println("[DataInitializer] Initialisation terminée");
        };
    }

    private void seedFictitiousTickets() {

        // 1. S'assurer qu'un Demandeur existe avec le bon type
        Utilisateur existingUser = userRepository.findByEmail("user@enicarthage.tn").orElse(null);
        if (existingUser != null && !(existingUser instanceof Demandeur)) {
            userRepository.delete(existingUser);
            userRepository.flush();
            existingUser = null;
        }

        Demandeur demandeur;
        if (existingUser == null) {
            demandeur = new Demandeur();
            demandeur.setNom("Hadj Ali");
            demandeur.setPrenom("Kenza");
            demandeur.setEmail("user@enicarthage.tn");
            demandeur.setMotPassee(passwordEncoder.encode("User123!"));
            demandeur.setRole(ROLE.DEMANDEUR);
            demandeur.setActive(true);
            demandeur.setEnabled(true);
            demandeur.setDateInscription(new Date());
            demandeur.setNoteMoyenne(0.0);
            demandeur.setTotalPoint(0);
            demandeur = userRepository.save(demandeur);
        } else {
            demandeur = (Demandeur) existingUser;
        }

        // 2. S'assurer qu'un Technicien existe avec le bon type
        Utilisateur existingTech = userRepository.findByEmail("tech@enicarthage.tn").orElse(null);
        if (existingTech != null && !(existingTech instanceof Technicien)) {
            userRepository.delete(existingTech);
            userRepository.flush();
            existingTech = null;
        }

        Technicien technicien;
        if (existingTech == null) {
            technicien = new Technicien();
            technicien.setNom("Gharbi");
            technicien.setPrenom("Mohammed");
            technicien.setEmail("tech@enicarthage.tn");
            technicien.setMotPassee(passwordEncoder.encode("Tech123!"));
            technicien.setRole(ROLE.TECHNICIEN);
            technicien.setActive(true);
            technicien.setEnabled(true);
            technicien.setDateInscription(new Date());
            technicien.setSpecialite("Maintenance Matériel");
            technicien.setTicketsResolus(0);
            technicien.setTotalPoints(0);
            technicien.setNoteMoyenne(0.0);
            technicien = userRepository.save(technicien);
        } else {
            technicien = (Technicien) existingTech;
        }

        // 3. Créer des tickets (6 au total avec les statuts demandés)
        createTicket("T-2026-001", "Panne Projecteur", "Le projecteur ne s'allume pas dans la salle ANN-CONF.", 
                     PRIORITE_TICKET.HAUTE, STATUS_TICKET.OUVERT, (Demandeur) demandeur, null, "ANN-CONF", "PROJ-CONF-01");
        
        createTicket("T-2026-002", "Écran Noir", "L'écran reste noir après l'allumage dans la salle S36.", 
                     PRIORITE_TICKET.HAUTE, STATUS_TICKET.OUVERT, (Demandeur) demandeur, null, "S36", "PC-101-02");

        createTicket("T-2026-003", "Clavier défectueux", "Plusieurs touches ne fonctionnent pas sur le PC de l'admin.", 
                     PRIORITE_TICKET.FAIBLE, STATUS_TICKET.RESOLU, (Demandeur) demandeur, technicien, "ADMIN", "PC-ADM-01");

        createTicket("T-2026-004", "Problème Réseau", "Pas de connexion internet sur le poste PC-101-01.", 
                     PRIORITE_TICKET.NORMALE, STATUS_TICKET.EN_COURS, (Demandeur) demandeur, technicien, "101", "PC-101-01");

        createTicket("T-2026-005", "Climatisation Défectueuse", "La climatisation fait un bruit anormal.", 
                     PRIORITE_TICKET.CRITIQUE, STATUS_TICKET.OUVERT, (Demandeur) demandeur, null, "ANN-CONF", "PROJ-CONF-01");

        createTicket("T-2026-006", "Souris Bloquée", "La souris ne répond plus sur le poste PC-101-02.", 
                     PRIORITE_TICKET.FAIBLE, STATUS_TICKET.OUVERT, (Demandeur) demandeur, null, "101", "PC-101-02");

        // 4. Générer des notifications pour le technicien
        if (notificationRepository.count() == 0) {
            notificationService.notifyTicketAssigned(technicien, "T-2026-002", "Problème Réseau");
            notificationService.notifyTicketAssigned(technicien, "T-2026-003", "Clavier défectueux");
        }
    }

    private void createTicket(String id, String titre, String desc, PRIORITE_TICKET priorite, 
                              STATUS_TICKET status, Demandeur creator, Utilisateur assigned, 
                              String idSalle, String idEquip) {
        Ticket t = Ticket.builder()
                .idTicket(id)
                .titre(titre)
                .description(desc)
                .priorite(priorite)
                .status(status)
                .createdBy(creator)
                .assignedTo(assigned)
                .dateCreation(new Date())
                .build();
        
        salleRepository.findById(idSalle).ifPresent(t::setSalle);
        equipementRepository.findById(idEquip).ifPresent(t::setEquipement);
        
        ticketRepository.save(t);
    }

    private void createSalle(SalleRepository repo, String id, String nom, String bat, String etage) {
        if (repo.existsById(id)) return;
        Salle s = new Salle();
        s.setIdSalle(id);
        s.setNomSalle(nom);
        s.setBatiment(bat);
        s.setEtage(etage);
        repo.save(s);
    }

    private void addEquip(EquipementRepository repo, SalleRepository sRepo, String id, String nom, String type, String idSalle) {
        if (repo.existsById(id)) return;
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
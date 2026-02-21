# plateforme-gestion-incidents

Plateforme complète de gestion des incidents techniques et de maintenance préventive pour l'ENICarthage. Solution digitale visant à remplacer la gestion manuelle actuelle (fiches papier, emails non structurés) par un système centralisé, traçable et en temps réel.

---

## Table des matières

1. [Contexte du projet](#contexte-du-projet)
2. [Objectifs](#objectifs)
3. [Fonctionnalités principales](#fonctionnalités-principales)
4. [Architecture technique](#architecture-technique)
5. [Modèle de données](#modèle-de-données)
6. [Installation et déploiement](#installation-et-déploiement)
7. [Utilisation](#utilisation)
8. [Structure du projet](#structure-du-projet)
9. [Planning de développement](#planning-de-développement)
10. [Tests et assurance qualité](#tests-et-assurance-qualité)
11. [Contributeurs](#contributeurs)
12. [Licence](#licence)

---

## Contexte du projet

L'ENICarthage gère actuellement ses incidents techniques et ses opérations de maintenance via des méthodes traditionnelles :

- Fiches papier manuscrites
- Emails non structurés
- Appels téléphoniques

Cette organisation entraîne plusieurs problématiques :

- Temps de traitement élevé des incidents
- Manque de traçabilité des interventions
- Absence de statistiques exploitables
- Impossible de suivre l'état des équipements en temps réel

La plateforme de gestion des incidents et maintenance a été conçue pour répondre à ces défis en digitalisant l'ensemble du processus.

---

## Objectifs

- Réduire le temps moyen de résolution des incidents
- Assurer une traçabilité complète des interventions
- Générer des statistiques pour améliorer la qualité de service
- Créer une base de connaissances réutilisable
- Automatiser les alertes et notifications selon les SLA
- Motiver les techniciens via un système de récompenses

---

## Fonctionnalités principales

### Module d'authentification et sécurité

- Inscription avec validation d'email
- Authentification par tokens JWT (validité 2 heures)
- Contrôle d'accès basé sur les rôles (RBAC)
- Hachage des mots de passe avec BCrypt
- Protection contre les attaques CSRF, XSS et injections SQL
- Journalisation des actions sensibles
- Réinitialisation de mot de passe par email

### Gestion des tickets d'incident

- Création de tickets avec titre, description, salle, équipement
- Pièces jointes (photos) aux formats autorisés
- Priorisation automatique selon le type de panne
- Workflow complet : Ouvert → Assigné → En cours → Résolu → Fermé
- Calcul automatique des délais SLA
- Réouverture possible des tickets

### Gestion des équipements et salles

- CRUD complet pour les salles
- CRUD complet pour les équipements
- Suivi de l'état des équipements (fonctionnel, en panne, en maintenance)
- Historique des maintenances par équipement
- Alertes automatiques pour les garanties expirées
- Import/export des données au format Excel

### Dashboard administrateur

- Vue en temps réel des tickets par statut
- Évolution des tickets sur 30 jours
- Performance individuelle des techniciens
- Feedbacks utilisateurs
- Carte thermique des salles les plus problématiques

### Notifications en temps réel

- Alertes pour les nouveaux tickets assignés
- Changements de statut des tickets
- Rappels de maintenance préventive
- Dépassement des délais SLA
- Attribution de badges
- Canaux : WebSocket (temps réel), Email (résumé quotidien), SMS (tickets critiques)

### Système de récompenses et gamification

- Attribution de points selon les actions :
  - Résolution de ticket : 10 à 50 points selon priorité
  - Feedback positif : 5 points par étoile
  - Résolution rapide : bonus de 20%
  - Ticket réouvert : pénalité de 15 points

- Badges pour les techniciens performants :
  - Expert : plus de 50 tickets résolus
  - Rapide : temps moyen < 4 heures
  - Excellent : satisfaction ≥ 4,5/5
  - Précis : 20 tickets sans réouverture

- Classement mensuel des 3 meilleurs techniciens

### Feedback utilisateurs

- Notation des interventions sur 5 étoiles
- Commentaires optionnels
- Photos complémentaires
- Analyse automatique des retours
- Alertes en cas de feedback négatif

### Maintenance préventive

- Calendrier interactif des maintenances
- Récurrence configurable (hebdomadaire, mensuelle, annuelle)
- Rappels automatiques pour les techniciens
- Suivi des garanties
- Coûts estimatifs des interventions
- Génération de rapports de maintenance

---

## Architecture technique

### Stack technique

- **Frontend** : React.js / Angular
- **Backend** : Spring Boot / Node.js
- **Base de données** : PostgreSQL / MySQL
- **Authentification** : JWT avec refresh token
- **Notifications temps réel** : WebSocket
- **Documentation API** : Swagger
- **Tests** : JUnit, Selenium

### Exigences non fonctionnelles

**Performance :**

- Temps de réponse < 2 secondes pour 95% des requêtes
- Support de 100+ utilisateurs simultanés
- Chargement initial < 5 secondes

**Sécurité :**

- Communications chiffrées (HTTPS)
- Protection CSRF, XSS, injections SQL
- Hachage BCrypt pour les mots de passe

**Accessibilité :**

- Standard WCAG 2.1 niveau AA
- Contraste minimum 4,5:1
- Navigation complète au clavier
- Design responsive (mobile, tablette, desktop)

**Maintenabilité :**

- Code commenté (standards Javadoc)
- Couverture de tests ≥ 70%
- Architecture modulaire

---

## Modèle de données

### Entités principales

#### Utilisateur (abstraite)

- idUser: String
- nom: String
- prenom: String
- email: String
- motPasse: String
- dateInscription: Date
- isActive: Boolean
- role: Enum {ADMIN, TECHNICIEN, DEMANDEUR}
- derniereConnexionReussie: Date
- derniereTentativeEchec: Date
- dernierChangementMdp: Date
- derniereIPconnue: String

#### Demandeur (hérite de Utilisateur)

- typeDemandeur: Enum {ETUDIANT, ENSEIGNANT, PERSONNEL}
- departement: String
- numEtudiant: String
- anneeEtude: String
- bureau: String

#### Technicien (hérite de Utilisateur)

- specialite: String
- ticketsResolus: int
- noteMoyenne: float
- totalPoints: int

#### Administrateur (hérite de Utilisateur)

#### Salle

- idSalle: String
- nomSalle: String
- etage: String
- batiment: String

#### Equipement

- idEquipement: String
- nomEquipement: String
- type: String
- modele: String
- numSerie: String
- etat: Enum {FONCTIONNEL, EN_PANNE, EN_MAINTENANCE}
- dateAchat: Date
- dateFinGarantie: Date

#### Ticket

- idTicket: String
- titre: String
- description: Text
- dateCreation: Date
- dateCloture: Date
- priorite: Enum {CRITIQUE, HAUTE, NORMALE, FAIBLE}
- statut: Enum {OUVERT, ASSIGNE, EN_COURS, RESOLU, FERME, REOUVERT}
- dateLimiteSLA: Date
- photo: String (URL)
- assignationDate: Date
- tempsResolution: Long (heures)

#### Feedback

- idFeedback: String
- note: int (1-5)
- commentaire: Text
- dateEvaluation: Date
- photoFeedback: String (URL)

#### MaintenancePreventive

- idMaintenance: String
- dateProgrammee: Date
- frequence: String
- description: Text
- statut: String

#### HistoriqueMaintenance

- idHistorique: String
- dateExecution: Date
- type: Enum {PREVENTIVE, CURATIVE}
- description: Text
- coutMaintenance: Float
- nomTechnicienResponsable: String

#### Notification

- idNotification: String
- type: String
- message: Text
- dateEnvoi: Date
- isReaded: Boolean
- canal: Enum {EMAIL, IN_APP, SMS}

#### Badge

- idBadge: String
- nomBadge: String
- description: Text
- critereObtention: String
- icon: String (URL)

#### PointTransaction

- idPoint: String
- quantite: int
- raison: String
- dateAttribution: Date

#### TraceLogin

- idTrace: String
- dateTentativeConnexion: Date
- adresseIP: String
- statut: Enum {SUCCES, ECHEC}
- userAgent: String

### Relations principales

- Un Administrateur gère plusieurs Salles/Equipements
- Une Salle contient plusieurs Equipements
- Un Equipement appartient à une Salle
- Un Equipement possède plusieurs HistoriqueMaintenance
- Un Equipement peut avoir plusieurs MaintenancePreventive
- Un Technicien réalise plusieurs MaintenancePreventive
- Un Technicien résout plusieurs Tickets
- Un Demandeur crée plusieurs Tickets
- Un Ticket est lié à une Salle
- Un Ticket peut concerner un Equipement
- Un Ticket reçoit un Feedback
- Un Technicien peut obtenir plusieurs Badges
- Un Technicien accumule plusieurs PointTransaction
- Un Utilisateur reçoit plusieurs Notifications
- Un Utilisateur a plusieurs TraceLogin

---

## Installation et déploiement

### Prérequis

- Node.js (v16 ou supérieur)
- Java JDK 17+ (pour Spring Boot)
- MySQL (WampServer)
- Maven
- Angular (standalone architecture)

### Installation

1. Cloner le dépôt

```bash
git clone https://github.com/MaramHadjAli/plateforme-gestion-incidents.git
cd plateforme-gestion-incidents

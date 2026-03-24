# Guide d'Utilisation du Backend - Plateforme de Gestion d'Incidents

## 1. Module d'Authentification et Sécurité

### Endpoints d'Authentification

#### Inscription
```bash
POST /api/auth/register
Content-Type: application/json

{
  "nom": "Jérémy Dupont",
  "email": "jeremy@example.com",
  "password": "SecurePassword123!",
  "role": "DEMANDEUR"
}

# Réponse
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Connexion
```bash
POST /api/auth/authenticate
Content-Type: application/json

{
  "email": "jeremy@example.com",
  "password": "SecurePassword123!"
}

# Réponse
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Utilisation du Token JWT
Tous les endpoints sécurisés requirent le header `Authorization`:
```bash
Authorization: Bearer <your_token_here>
```

---

## 2. Dashboard Administrateur

### Accès aux Statistiques
```bash
GET /api/admin/dashboard/stats
Authorization: Bearer <admin_token>

# Réponse
{
  "totalTickets": 45,
  "totalUsers": 25,
  "totalTechnicians": 8,
  "ticketsByStatus": {
    "OUVERT": 10,
    "ASSIGNE": 15,
    "EN_COURS": 12,
    "RESOLU": 6,
    "FERME": 2
  }
}
```

⚠️ **Cet endpoint nécessite le rôle ADMIN**

---

## 3. Notifications Temps Réel (WebSocket)

### Configuration WebSocket
Le serveur expose un endpoint WebSocket sur: `ws://localhost:8080/ws-notifications`

### Connexion
```javascript
const socket = new WebSocket('ws://localhost:8080/ws-notifications');
socket.onopen = function(event) {
  console.log('WebSocket connecté');
};
```

### Types de Notifications

#### 1. Notification Générale (Broadcast)
```javascript
// Envoyer une notification à tous les utilisateurs connectés
socket.send(JSON.stringify({
  destination: '/app/sendNotification',
  payload: {
    type: 'TICKET_CREATED',
    title: 'Nouveau Ticket',
    message: 'Un nouveau ticket a été créé',
    ticketId: 'TICKET-001',
    severity: 'HIGH'
  }
}));

// Reçevoir sur /topic/notifications
socket.onmessage = function(event) {
  const notification = JSON.parse(event.data);
  console.log('Notification reçue:', notification);
};
```

#### 2. Notification Administrateur
```javascript
// Envoyer une notification à l'administrateur
socket.send(JSON.stringify({
  destination: '/app/notifyAdmin',
  payload: {
    type: 'ADMIN_ALERT',
    title: 'Alerte Administrateur',
    message: 'Un incident critique a été signalé',
    severity: 'CRITICAL'
  }
}));

// Reçevoir sur /topic/admin
socket.subscribe('/topic/admin', function(message) {
  console.log('Notification admin:', message);
});
```

#### 3. Notification Utilisateur Spécifique
```javascript
// Envoyer une notification à un utilisateur spécifique
socket.send(JSON.stringify({
  destination: '/app/notifyTechnician',
  payload: {
    type: 'TICKET_ASSIGNED',
    title: 'Ticket Assigné',
    message: 'Un ticket vous a été assigné',
    ticketId: 'TICKET-005',
    severity: 'MEDIUM'
  }
}));

// Reçevoir sur /queue/notifications (personnel)
socket.subscribe('/user/queue/notifications', function(message) {
  console.log('Ma notification personnelle:', message);
});
```

### Endpoints REST pour les Notifications

#### Envoyer Notification Administrateur
```bash
POST /api/notifications/send-to-admin
Content-Type: application/json
Authorization: Bearer <token>

{
  "type": "SYSTEM_ALERT",
  "title": "Alerte Système",
  "message": "Le serveur approche de la limite de capacité",
  "severity": "WARNING"
}

# Réponse
{
  "message": "Notification envoyée aux administrateurs"
}
```

#### Broadcast Global
```bash
POST /api/notifications/broadcast
Content-Type: application/json
Authorization: Bearer <token>

{
  "type": "MAINTENANCE",
  "title": "Maintenance Programmée",
  "message": "Maintenance prévue ce soir de 22h à 23h",
  "severity": "INFO"
}

# Réponse
{
  "message": "Notification envoyée à tous"
}
```

---

## 4. Structure des Rôles

| Rôle | Permissions |
|------|-------------|
| **ADMIN** | Accès Dashboard, Gestion Système, Notifications Globales |
| **TECHNICIEN** | Assignation Tickets, Notifications Personnelles |
| **DEMANDEUR** | Création Tickets, Suivi Tickets |

---

## 5. Types de Notifications Supportées

- `TICKET_CREATED` - Un ticket a été créé
- `TICKET_ASSIGNED` - Un ticket a été assigné
- `TICKET_RESOLVED` - Un ticket a été résolu
- `TICKET_CLOSED` - Un ticket a été fermé
- `ADMIN_ALERT` - Alerte administrateur
- `SYSTEM_ALERT` - Alerte système
- `USER_DISCONNECTED` - Un utilisateur s'est déconnecté
- `MAINTENANCE` - Message de maintenance

---

## 6. Sécurité

### JWT Token
- **Durée de validité**: 24 heures
- **Algorithme**: HS256
- **Signature**: Base64 encodée

### CORS
Le WebSocket accepte les connexions depuis:
- `http://localhost:4200` (Angular)
- `http://localhost:3000` (React)

---

## 7. Exemple Complet Frontend (Angular)

```typescript
import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private socket$: WebSocketSubject<any>;

  constructor() {
    this.socket$ = webSocket({
      url: 'ws://localhost:8080/ws-notifications',
      protocol: ['STOMP'],
      openObserver: {
        next: () => console.log('WebSocket connecté')
      },
      closeObserver: {
        next: () => console.log('WebSocket fermé')
      }
    });
  }

  subscribeToAdminNotifications() {
    return this.socket$.subscribe(
      (message: any) => console.log('Notification reçue:', message),
      (error) => console.error('Erreur WebSocket:', error)
    );
  }

  sendNotification(notification: any) {
    this.socket$.next(notification);
  }
}
```

---

## 8. Vérification du Statut du Serveur

```bash
# Vérifier que le serveur est actif
curl http://localhost:8080/actuator/health

# Vérification de la compilation
mvn clean compile
mvn spring-boot:run
```

---

**Dernière mise à jour**: 24 Mars 2026

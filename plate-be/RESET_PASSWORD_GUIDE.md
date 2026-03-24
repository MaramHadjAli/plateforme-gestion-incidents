# 🔑 Guide d'Utilisation - Réinitialisation de Mot de Passe

## Endpoints Disponibles

### 1️⃣ POST /api/auth/forgot-password

#### Description
Initie le processus de réinitialisation de mot de passe en envoyant un email avec un lien.

#### Requête
```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'
```

#### Réponse (Succès)
```json
{
  "success": true,
  "message": "Un email de réinitialisation a été envoyé à votre adresse email"
}
```

#### Réponse (Erreur - Email non trouvé)
```json
{
  "success": false,
  "message": "Utilisateur non trouvé avec l'email: user@example.com"
}
```

#### Points Important
- ✅ Le token générée expire dans **15 minutes**
- ✅ Un UUID unique est généré pour chaque demande
- ✅ L'email envoyé contient le lien: `http://localhost:4200/reset-password?token=UUID`
- ✅ Même si l'utilisateur existe pas, un message générique est retourné (sécurité)

---

### 2️⃣ POST /api/auth/reset-password

#### Description
Réinitialise le mot de passe avec un token valide.

#### Requête
```bash
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "newPassword": "NewPassword123",
    "confirmPassword": "NewPassword123"
  }'
```

#### Réponse (Succès)
```json
{
  "success": true,
  "message": "Votre mot de passe a été réinitialisé avec succès"
}
```

#### Réponse (Erreur - Mots de passe non concordants)
```json
{
  "success": false,
  "message": "Les mots de passe ne correspondent pas"
}
```

#### Réponse (Erreur - Mot de passe trop court)
```json
{
  "success": false,
  "message": "Le mot de passe doit contenir au moins 6 caractères"
}
```

#### Réponse (Erreur - Token expiré)
```json
{
  "success": false,
  "message": "Le token de réinitialisation a expiré"
}
```

#### Réponse (Erreur - Token déjà utilisé)
```json
{
  "success": false,
  "message": "Le token de réinitialisation a déjà été utilisé"
}
```

#### Validations
- ✅ Token doit être valide et non expiré
- ✅ Token ne peut être utilisé qu'une seule fois
- ✅ Les mots de passe doivent correspondre
- ✅ Longueur minimale: 6 caractères
- ✅ Le mot de passe sera hachée avec BCrypt avant stockage

---

## 🔧 Configuration

### application.properties
```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=noreply@plate-incidents.com
```

### Pour Gmail
1. Activer l'authentification 2FA
2. Générer une **App Password**
3. Utiliser cette app password dans `spring.mail.password`

---

## 🧪 Scénarios de Test

### ✅ Test 1: Demande de réinitialisation valide
```bash
# Étape 1: Demander la réinitialisation
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com"}'

# Résultat attendu: Email envoyé, vous recevez un lien avec token
```

### ✅ Test 2: Réinitialisation réussie
```bash
# Étape 1: Utiliser le token reçu par email pour réinitialiser
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_TOKEN_HERE",
    "newPassword": "NewSecurePass123",
    "confirmPassword": "NewSecurePass123"
  }'

# Résultat attendu: Message succès

# Étape 2: Se connecter avec le nouveau mot de passe
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "NewSecurePass123"
  }'

# Résultat attendu: JWT token et refresh token reçus
```

### ❌ Test 3: Erreur - Token expiré
```bash
# Attendre 15+ minutes puis essayer de réinitialiser
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_OLD_TOKEN_HERE",
    "newPassword": "NewSecurePass123",
    "confirmPassword": "NewSecurePass123"
  }'

# Résultat attendu: Message d'erreur "Le token a expiré"
```

### ❌ Test 4: Erreur - Token double utilisation
```bash
# Réinitialiser une première fois (succès)
# Essayer à nouveau avec le même token
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_TOKEN_HERE",
    "newPassword": "AnotherPassword123",
    "confirmPassword": "AnotherPassword123"
  }'

# Résultat attendu: Message d'erreur "Token déjà utilisé"
```

---

## 📊 Flux Utilisateur (Frontend)

### Écran "Mot de passe oublié"
1. User clique sur "Mot de passe oublié?"
2. Affiche formulaire avec input email
3. User entre son email et clique "Envoyer"
4. Frontend appelle `POST /api/auth/forgot-password`
5. Affiche message: "Vérifiez votre email"

### Écran "Réinitialiser le mot de passe"
1. User reçoit email avec lien `http://localhost:4200/reset-password?token=UUID`
2. User clique sur le lien
3. Frontend extrait le token de l'URL
4. Affiche formulaire: nouveau mot de passe + confirmation
5. User entre les mots de passe et clique "Réinitialiser"
6. Frontend appelle `POST /api/auth/reset-password`
7. Si succès: redirection vers login avec message "Mot de passe réinitialisé"
8. Si erreur: affiche le message d'erreur

---

## 🔐 Sécurité

### ✅ Mesures implémentées
- [x] Tokens uniques (UUID)
- [x] Expiration 15 minutes
- [x] One-time use (token marqué comme utilisé)
- [x] Hachage BCrypt des mots de passe
- [x] Validation des emails
- [x] Validation des mots de passe (longueur, concordance)
- [x] Pas de révélation d'informations sensibles
- [x] Logging des actions (recommandé)

### 🛡️ Recommandations supplémentaires
- [ ] Rate limiting sur les endpoints (anti-spam)
- [ ] Captcha sur le formulaire "Mot de passe oublié"
- [ ] Notification email si mot de passe réinitialisé
- [ ] Logs d'audit pour les réinitialisations
- [ ] Limite de nombre de demandes par email (ex: 3 par heure)

---

## 🐛 Dépannage

### Problème: Email non reçu
**Solutions**:
1. Vérifier la configuration SMTP dans `application.properties`
2. Vérifier le dossier spam
3. Vérifier les logs de l'application
4. Vérifier que l'email est correct dans la requête

### Problème: Token expiré immédiatement
**Solutions**:
1. Vérifier que le serveur est bien synchronisé avec l'heure système
2. Vérifier la configuration `app.jwt.expirationMs` dans `application.properties`

### Problème: Erreur "Utilisateur non trouvé"
**Solutions**:
1. Vérifier que l'utilisateur existe réellement
2. Vérifier que l'email est exactement correcte (case-sensitive)

---

## 📚 Références

- Spring Security: https://spring.io/projects/spring-security
- JavaMailSender: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSender.html
- JWT: https://jwt.io/


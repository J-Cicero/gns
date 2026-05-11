# Email Module Configuration

## 📧 Configuration des variables d'environnement

### Fichier `.env`

Le fichier `.env` contient les variables d'environnement pour la configuration du email. **Ne pas le commiter** (il est dans `.gitignore`).

Copie `.env.example` en `.env` et configure avec tes vraies valeurs :

```bash
cp .env.example .env
```

### Variables requises

```properties
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### Pour Gmail

1. Active les "App Passwords" : https://myaccount.google.com/apppasswords
2. Génère un mot de passe d'application (16 caractères)
3. Mets-le dans `MAIL_PASSWORD` dans le fichier `.env`

### Options de chargement des variables

#### Option 1 : Variables d'environnement système (Production)
```bash
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
java -jar application.jar
```

#### Option 2 : Fichier `.env` (Développement)
Avec une dépendance comme `dotenv`:
```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>3.0.0</version>
</dependency>
```

#### Option 3 : `application-local.properties` (Développement)
Crée `src/main/resources/application-local.properties` (dans `.gitignore`):
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

Lance avec : `spring.profiles.active=local`

## 📁 Structure du module

```
src/main/java/com/backend/gns/Shared/email/
├── EmailService.java          # Interface
└── EmailServiceImpl.java       # Implémentation avec Thymeleaf

src/main/resources/templates/emails/
├── confirmation-inscription.html
├── kyc-valide.html
├── kyc-rejete.html
├── versement-notification.html
└── alerte-solde-faible.html
```

## 🚀 Utilisation

```java
@Autowired
private EmailService emailService;

// Envoi d'un email de confirmation
emailService.envoyerConfirmationInscription("user@example.com", "Jean Dupont");

// Confirmation KYC validée
emailService.envoyerKYCValide("user@example.com", "Jean Dupont");

// Rejet KYC
emailService.envoyerKYCRejete("user@example.com", "Jean Dupont", "Document non valide");

// Notification de versement
emailService.envoyerNotificationVersement("user@example.com", "Jean Dupont", "50,000 FCFA");

// Alerte solde faible
emailService.envoyerAlerteSoldeFaible("user@example.com", "Jean Dupont", "2,500 FCFA");
```

## ⚙️ Configuration dans `application.properties`

```properties
# Email Configuration (Gmail)
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

Les variables `${MAIL_USERNAME}` et `${MAIL_PASSWORD}` sont lues depuis l'environnement système ou le fichier `.env`.

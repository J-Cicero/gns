# 🌐 API ENDPOINTS - DOCUMENTATION COMPLÈTE

**Base URL**: `http://localhost:8080/api` ou `http://localhost:8080/users`  
**Format**: JSON  
**Authentification**: Bearer Token (JWT)

---

## 📋 TABLE DES MATIÈRES

1. [User Controller](#user-controller) - Authentification
2. [Student Controller](#student-controller) - Gestion étudiants
3. [Merchant Controller](#merchant-controller) - Gestion commerçants
4. [Boutique Controller](#boutique-controller) - Gestion boutiques
5. [Product Controller](#product-controller) - Gestion produits
6. [Commande Controller](#commande-controller) - Gestion commandes
7. [Wallet Controller](#wallet-controller) - Gestion portefeuilles
8. [Paiement Controller](#paiement-controller) - Gestion paiements
9. [Versement Controller](#versement-controller) - Gestion versements
10. [Admin Controller](#admin-controller) - Gestion admins
11. [Document Étudiant Controller](#document-étudiant-controller) - Gestion documents
12. [CommandeLigne Controller](#commandeligne-controller) - Gestion lignes commande
13. [Inscription Annuelle Controller](#inscription-annuelle-controller) - Gestion inscriptions

---

## 🔑 USER CONTROLLER
**Endpoint Base**: `/users`  
**Description**: Authentification et gestion utilisateurs générales

### 1. Register User
- **Method**: `POST`
- **URL**: `/users/register`
- **Description**: Enregistrer un nouvel utilisateur et envoyer email si nécessaire
- **Body**:
  ```json
  {
    "email": "user@email.com",
    "password": "securePassword123",
    "firstName": "John",
    "lastName": "Doe"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "trackingId": "uuid",
    "email": "user@email.com",
    "firstName": "John",
    "etat": true
  }
  ```
- **Erreurs**: 400 (Input invalide), 500 (Erreur serveur)

### 2. Get User by TrackingId
- **Method**: `GET`
- **URL**: `/users/get/{trackingId}`
- **Parameters**: `trackingId` (UUID)
- **Response**: `200 OK` (User found) ou `404 NOT_FOUND`

### 3. Update User Status
- **Method**: `PATCH`
- **URL**: `/users/etat/{trackingId}`
- **Parameters**: 
  - `trackingId` (UUID)
  - `etat` (boolean, query param)
- **Description**: Activer ou désactiver un utilisateur
- **Response**: `200 OK` ou `404 NOT_FOUND`

### 4. List All Users
- **Method**: `GET`
- **URL**: `/users/all`
- **Parameters**: 
  - `page` (int, default=0)
  - `size` (int, default=20)
- **Response**: `200 OK` avec Page<UserResponse>

### 5. Delete User
- **Method**: `DELETE`
- **URL**: `/users/delete/{trackingId}`
- **Parameters**: `trackingId` (UUID)
- **Response**: `200 OK` ou `404 NOT_FOUND`

---

## 👨‍🎓 STUDENT CONTROLLER
**Endpoint Base**: `/api/students`  
**Description**: Gestion complète des étudiants

### 1. Create Student
- **Method**: `POST`
- **URL**: `/api/students`
- **Body**:
  ```json
  {
    "firstName": "Ahmed",
    "lastName": "Diallo",
    "email": "ahmed@email.com",
    "niveauEtude": "L2",
    "numeroMatricule": "MAT2024001",
    "dateNaissance": "2002-03-15"
  }
  ```
- **Response**: `201 CREATED`

### 2. Get Student by ID
- **Method**: `GET`
- **URL**: `/api/students/{trackingId}`
- **Response**: `200 OK` ou `404 NOT_FOUND`

### 3. Update Student
- **Method**: `PUT`
- **URL**: `/api/students/{trackingId}`
- **Body**: Same as create
- **Response**: `200 OK`

### 4. Delete Student
- **Method**: `DELETE`
- **URL**: `/api/students/{trackingId}`
- **Response**: `204 NO_CONTENT`

### 5. Get Students by KYC Status (IMPORTANT)
- **Method**: `GET`
- **URL**: `/api/students/kyc/{statutKYC}`
- **Parameters**:
  - `statutKYC` (enum: ENCOURS, VALIDEE, REJETEE)
  - `page` (int, default=0)
  - `size` (int, default=10)
- **Description**: Filtrer étudiants par statut de vérification KYC
- **Response**: `200 OK` avec Page<StudentResponse> ou `404 NOT_FOUND`

### 6. List All Students
- **Method**: `GET`
- **URL**: `/api/students`
- **Parameters**: `page`, `size`
- **Response**: `200 OK` avec Page<StudentResponse>

---

## 🏪 MERCHANT CONTROLLER
**Endpoint Base**: `/api/merchants`  
**Description**: Gestion des commerçants/vendeurs

### 1. Create Merchant
- **Method**: `POST`
- **URL**: `/api/merchants`
- **Body**:
  ```json
  {
    "firstName": "Fatou",
    "lastName": "Ba",
    "email": "fatou@shop.com",
    "numeroIdentification": "ID123456",
    "adresse": "Dakar, Senegal"
  }
  ```
- **Response**: `201 CREATED`

### 2. Get Merchant by ID
- **Method**: `GET`
- **URL**: `/api/merchants/{trackingId}`
- **Response**: `200 OK` ou `404 NOT_FOUND`

### 3. Update Merchant
- **Method**: `PUT`
- **URL**: `/api/merchants/{trackingId}`
- **Response**: `200 OK`

### 4. Delete Merchant
- **Method**: `DELETE`
- **URL**: `/api/merchants/{trackingId}`
- **Response**: `204 NO_CONTENT`

### 5. List All Merchants
- **Method**: `GET`
- **URL**: `/api/merchants`
- **Parameters**: `page`, `size`
- **Response**: `200 OK` avec Page<MerchantResponse>

---

## 🛒 BOUTIQUE CONTROLLER
**Endpoint Base**: `/api/boutiques`  
**Description**: Gestion des boutiques (magasins)

### Endpoints disponibles:
- **POST** `/api/boutiques` - Créer boutique
- **GET** `/api/boutiques/{trackingId}` - Récupérer boutique
- **PUT** `/api/boutiques/{trackingId}` - Mettre à jour
- **DELETE** `/api/boutiques/{trackingId}` - Supprimer
- **GET** `/api/boutiques/merchant/{merchantTrackingId}` - Boutiques du commerçant
- **GET** `/api/boutiques` - Lister toutes

---

## 📦 PRODUCT CONTROLLER
**Endpoint Base**: `/api/products`  
**Description**: Gestion des produits

### 1. Create Product
- **Method**: `POST`
- **URL**: `/api/products`
- **Body**:
  ```json
  {
    "nom": "Chemise",
    "description": "Chemise bleue taille M",
    "prix": 15000.00,
    "quantite": 50,
    "boutiqueTrackingId": "uuid-boutique"
  }
  ```
- **Response**: `201 CREATED`

### 2. Get Product by ID
- **Method**: `GET`
- **URL**: `/api/products/{trackingId}`
- **Response**: `200 OK`

### 3. Update Product
- **Method**: `PUT`
- **URL**: `/api/products/{trackingId}`
- **Response**: `200 OK`

### 4. Delete Product
- **Method**: `DELETE`
- **URL**: `/api/products/{trackingId}`
- **Response**: `204 NO_CONTENT`

### 5. Get Products by Store
- **Method**: `GET`
- **URL**: `/api/products/boutique/{boutiqueTrackingId}`
- **Parameters**: `page`, `size`
- **Description**: Lister tous les produits d'une boutique
- **Response**: `200 OK`

### 6. Get Products by Availability
- **Method**: `GET`
- **URL**: `/api/products/disponible/{estDisponible}`
- **Parameters**: 
  - `estDisponible` (boolean)
  - `page`, `size`
- **Description**: Filtrer produits disponibles/indisponibles
- **Response**: `200 OK`

### 7. List All Products
- **Method**: `GET`
- **URL**: `/api/products`
- **Parameters**: `page`, `size`
- **Response**: `200 OK`

---

## 🛍️ COMMANDE CONTROLLER
**Endpoint Base**: `/api/commandes`  
**Description**: Gestion des commandes (très important!)

### 1. Create Order
- **Method**: `POST`
- **URL**: `/api/commandes`
- **Body**:
  ```json
  {
    "studentTrackingId": "uuid",
    "items": [
      {
        "productTrackingId": "uuid",
        "quantite": 2,
        "prixUnitaire": 15000
      }
    ]
  }
  ```
- **Response**: `201 CREATED`
- **Logique**: 
  - Crée la commande (statut: ENCOURS)
  - Crée les lignes CommandeLigne
  - Calcule le total

### 2. Get Order by ID
- **Method**: `GET`
- **URL**: `/api/commandes/{trackingId}`
- **Response**: `200 OK` avec détails complets

### 3. Update Order
- **Method**: `PUT`
- **URL**: `/api/commandes/{trackingId}`
- **Response**: `200 OK`

### 4. Delete Order
- **Method**: `DELETE`
- **URL**: `/api/commandes/{trackingId}`
- **Response**: `204 NO_CONTENT`

### 5. Get Orders by Status ⭐
- **Method**: `GET`
- **URL**: `/api/commandes/statut/{statut}`
- **Parameters**:
  - `statut` (enum: ENCOURS, VALIDEE, LIVREE, ANNULEE)
  - `page`, `size`
- **Description**: Filtrer commandes par statut
- **Response**: `200 OK` avec Page<CommandeResponse>

### 6. List All Orders
- **Method**: `GET`
- **URL**: `/api/commandes`
- **Parameters**: `page`, `size`
- **Response**: `200 OK`

### 7. Pay Order ⭐⭐⭐
- **Method**: `POST`
- **URL**: `/api/commandes/{trackingId}/payer`
- **Description**: Traiter le paiement d'une commande
- **Process**:
  1. Vérifie que commande existe
  2. Débite le portefeuille étudiant
  3. Crée transaction Paiement
  4. Change statut à VALIDEE
  5. Crée Versement pour commerçant
- **Response**: `200 OK` ou `400 BAD_REQUEST` (solde insuffisant)
- **Erreurs possibles**:
  - Solde insuffisant
  - Commande déjà payée
  - Portefeuille inexistant

---

## 💳 WALLET CONTROLLER
**Endpoint Base**: `/api/wallets`  
**Description**: Gestion des portefeuilles (création automatique)

### Note Importante
> Les portefeuilles sont créés **automatiquement** lors de la création d'un Student ou Boutique
> Les endpoints POST, PUT, DELETE ne sont pas exposés (commented out)

### 1. Get Wallet by ID
- **Method**: `GET`
- **URL**: `/api/wallets/{trackingId}`
- **Response**: `200 OK` ou `404 NOT_FOUND`

### 2. Get Wallets by Type
- **Method**: `GET`
- **URL**: `/api/wallets/type/{typeWallet}`
- **Parameters**:
  - `typeWallet` (enum: RELAIS, HORIZON)
  - `page`, `size`
- **Description**: Lister portefeuilles par type
- **Response**: `200 OK`

### 3. Get Wallets by Lock Status
- **Method**: `GET`
- **URL**: `/api/wallets/verrouille/{estVerrouille}`
- **Parameters**:
  - `estVerrouille` (boolean)
  - `page`, `size`
- **Description**: Lister portefeuilles verrouillés/déverrouillés
- **Response**: `200 OK`

### 4. List All Wallets
- **Method**: `GET`
- **URL**: `/api/wallets`
- **Parameters**: `page`, `size`
- **Response**: `200 OK`

---

## 💰 PAIEMENT CONTROLLER
**Endpoint Base**: `/api/paiements`  
**Description**: Gestion des paiements/transactions

### 1. Create Payment
- **Method**: `POST`
- **URL**: `/api/paiements`
- **Body**:
  ```json
  {
    "montant": 30000.00,
    "commandeTrackingId": "uuid",
    "walletTrackingId": "uuid",
    "typePaiement": "PAIEMENT_CLIENT"
  }
  ```
- **Response**: `201 CREATED`

### 2. Get Payment by ID
- **Method**: `GET`
- **URL**: `/api/paiements/{trackingId}`
- **Response**: `200 OK`

### 3. Update Payment
- **Method**: `PUT`
- **URL**: `/api/paiements/{trackingId}`
- **Response**: `200 OK`

### 4. Delete Payment
- **Method**: `DELETE`
- **URL**: `/api/paiements/{trackingId}`
- **Response**: `204 NO_CONTENT`

### 5. Get Payments by Status
- **Method**: `GET`
- **URL**: `/api/paiements/statut/{statutPaiement}`
- **Parameters**:
  - `statutPaiement` (enum: ENCOURS, COMPLETE, ECHUE, REMBOURSEE)
  - `page`, `size`
- **Response**: `200 OK`

### 6. Get Payments by Type
- **Method**: `GET`
- **URL**: `/api/paiements/type/{typePaiement}`
- **Parameters**:
  - `typePaiement` (enum: PAIEMENT_CLIENT, VERSEMENT_MERCHANT)
  - `page`, `size`
- **Response**: `200 OK`

### 7. Get Payments for Order
- **Method**: `GET`
- **URL**: `/api/paiements/commande/{commandeTrackingId}`
- **Parameters**: `page`, `size`
- **Response**: `200 OK`

### 8. Get Payments for Wallet
- **Method**: `GET`
- **URL**: `/api/paiements/wallet/{walletTrackingId}`
- **Parameters**: `page`, `size`
- **Response**: `200 OK`

### 9. List All Payments
- **Method**: `GET`
- **URL**: `/api/paiements`
- **Parameters**: `page`, `size`
- **Response**: `200 OK`

---

## 💸 VERSEMENT CONTROLLER
**Endpoint Base**: `/api/versements`  
**Description**: Gestion des versements (paiements aux commerçants)

### Endpoints disponibles:
- **POST** `/api/versements` - Créer versement
- **GET** `/api/versements/{trackingId}` - Récupérer
- **PUT** `/api/versements/{trackingId}` - Mettre à jour
- **DELETE** `/api/versements/{trackingId}` - Supprimer
- **GET** `/api/versements/statut/{statut}` - Par statut
- **GET** `/api/versements/type/{type}` - Par type
- **GET** `/api/versements` - Lister tous

---

## 👤 ADMIN CONTROLLER
**Endpoint Base**: `/api/admins`  
**Description**: Gestion des administrateurs système

### 1. Create Admin
- **Method**: `POST`
- **URL**: `/api/admins`
- **Body**:
  ```json
  {
    "firstName": "Admin",
    "lastName": "System",
    "email": "admin@studcash.com",
    "role": "SUPER_ADMIN"
  }
  ```
- **Response**: `201 CREATED`

### 2. Get Admin by ID
- **Method**: `GET`
- **URL**: `/api/admins/{trackingId}`
- **Response**: `200 OK`

### 3. Update Admin
- **Method**: `PUT`
- **URL**: `/api/admins/{trackingId}`
- **Response**: `200 OK`

### 4. Delete Admin
- **Method**: `DELETE`
- **URL**: `/api/admins/{trackingId}`
- **Response**: `204 NO_CONTENT`

### 5. List All Admins
- **Method**: `GET`
- **URL**: `/api/admins`
- **Parameters**: `page`, `size`
- **Response**: `200 OK`

---

## 📄 DOCUMENT ÉTUDIANT CONTROLLER
**Endpoint Base**: `/api/documents`  
**Description**: Gestion des documents pour KYC

### Endpoints disponibles:
- **POST** `/api/documents` - Uploader document
- **GET** `/api/documents/{trackingId}` - Récupérer
- **PUT** `/api/documents/{trackingId}` - Mettre à jour
- **DELETE** `/api/documents/{trackingId}` - Supprimer
- **GET** `/api/documents/etudiant/{studentTrackingId}` - Documents étudiant
- **GET** `/api/documents/statut/{statut}` - Par statut
- **GET** `/api/documents` - Lister tous

---

## 📋 COMMANDELIGNE CONTROLLER
**Endpoint Base**: `/api/commande-lignes`  
**Description**: Gestion des lignes de commande

### Endpoints disponibles:
- **POST** `/api/commande-lignes` - Créer ligne
- **GET** `/api/commande-lignes/{trackingId}` - Récupérer
- **PUT** `/api/commande-lignes/{trackingId}` - Mettre à jour
- **DELETE** `/api/commande-lignes/{trackingId}` - Supprimer
- **GET** `/api/commande-lignes/commande/{commandeTrackingId}` - Lignes commande
- **GET** `/api/commande-lignes` - Lister tous

---

## 🎓 INSCRIPTION ANNUELLE CONTROLLER
**Endpoint Base**: `/api/inscriptions`  
**Description**: Gestion des inscriptions annuelles

### Endpoints disponibles:
- **POST** `/api/inscriptions` - Créer inscription
- **GET** `/api/inscriptions/{trackingId}` - Récupérer
- **PUT** `/api/inscriptions/{trackingId}` - Mettre à jour
- **DELETE** `/api/inscriptions/{trackingId}` - Supprimer
- **GET** `/api/inscriptions/etudiant/{studentTrackingId}` - Inscriptions étudiant
- **GET** `/api/inscriptions/statut/{statut}` - Par statut
- **GET** `/api/inscriptions` - Lister tous

---

## 🔐 Statuts HTTP Standard

| Code | Signification |
|------|---------------|
| 200 | ✅ OK - Succès |
| 201 | ✅ CREATED - Création réussie |
| 204 | ✅ NO_CONTENT - Suppression réussie |
| 400 | ❌ BAD_REQUEST - Données invalides |
| 404 | ❌ NOT_FOUND - Ressource inexistante |
| 500 | ❌ INTERNAL_SERVER_ERROR - Erreur serveur |

---

## 🔑 Format d'Authentification

Tous les endpoints (sauf register) nécessitent:

```http
Authorization: Bearer <JWT_TOKEN>
```

Exemple:
```
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  http://localhost:8080/api/students
```

---

## 📝 Pagination

Tous les endpoints de liste supportent:
```
?page=0&size=10
```

Défauts:
- `page`: 0
- `size`: 10 ou 20

---

## ⭐ ENDPOINTS LES PLUS IMPORTANTS

### Pour les étudiants:
1. `POST /api/commandes` - Créer commande
2. `POST /api/commandes/{id}/payer` - Payer commande
3. `GET /api/students/kyc/{status}` - Voir statut KYC

### Pour les commerçants:
1. `POST /api/products` - Ajouter produit
2. `GET /api/paiements/type/VERSEMENT_MERCHANT` - Voir versements
3. `PUT /api/products/{id}` - Mettre à jour stock

### Pour les admins:
1. `GET /api/students/kyc/ENCOURS` - Vérifier KYC en attente
2. `GET /api/commandes/statut/ENCOURS` - Commandes non payées
3. `GET /api/paiements/statut/ECHUE` - Paiements échoués

---

**Généré**: 2026-05-06  
**Version**: 1.0  
**État**: Complet

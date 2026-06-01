# Comptes de Test - studCASH

Ces comptes sont générés automatiquement au démarrage de l'application Backend (si la base de données est vide) via la classe `DataSeeder`.

Toutes les adresses emails partagent le même mot de passe pour faciliter les tests.

| Rôle | Adresse Email de Connexion | Mot de passe | URL d'accès direct (Frontend) |
| :--- | :--- | :--- | :--- |
| **Administrateur GNS** | `admin@gns.com` | `password123` | `http://localhost:4200/admin-gns/dashboard` |
| **Administrateur DBS** | `admin@dbs.com` | `password123` | `http://localhost:4200/admin-dbs/dashboard` |
| **Administrateur Université** | `admin@university.com` | `password123` | `http://localhost:4200/admin-university/dashboard` |
| **Opérateur Banque** | `operator@bank.com` | `password123` | `http://localhost:4200/bank-portal/dashboard` |
| **Étudiant** | `student@test.com` | `password123` | `http://localhost:4200/student/dashboard` |
| **Commerçant** | `merchant@test.com` | `password123` | `http://localhost:4200/merchant/dashboard` |

> 💡 **Note :** 
> Si vous souhaitez ajouter ou modifier ces comptes par défaut, vous pouvez le faire dans le backend, au sein du fichier : 
> `/src/main/java/com/backend/gns/core/config/DataSeeder.java`

# 📄 Documentation Technique de la Base de Données GNS

Cette documentation décrit l'état actuel de la base de données après la refactorisation, incluant les tables, les relations et les modèles d'héritage.

---

## 🏗️ Architecture Globale
Le projet utilise une architecture **Modulith** avec une stratégie d'héritage `SINGLE_TABLE` pour les entités complexes (Users, Wallets). Toutes les entités héritent de `BaseEntity` pour l'audit (CreatedAt, UpdatedAt, CreatedBy, UpdatedBy).

... (contenu identique conservé) ...

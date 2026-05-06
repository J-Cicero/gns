# 🌿 BRANCHES & STRATÉGIE GIT

## État Actuel

**Branche courante**: `Jude` (locale)  
**Remote**: `origin/Jude` + autres remotes

```
* Jude (LOCAL - 17 commits ahead)
  develop (main)
  secure
  origin/HEAD -> origin/develop
  origin/Jude
  origin/develop
  origin/secure
  origin/security
```

---

## 📋 DESCRIPTION DES BRANCHES

### 🔴 Jude (Branche de travail actuelle)
- **Commits ahead**: 17
- **État**: Stable & Production Ready
- **Contient**:
  - ✅ Rapport documentation complet
  - ✅ Folder `/docs` avec 8 fichiers de guide
  - ✅ Tous les modèles et services fonctionnels
  - ✅ Corrections entités (dernier commit)
  - ✅ Fonctionnalités paiement complètes
- **Derniers commits**:
  ```
  e5ade58 correction de certiae entite
  c06778c ajoute de models pour les document a uploader
  6150950 suppresion de certian fonction nom utiliser
  9fac3ea mise a jour des controller + services
  05a8081 paiement + credit/debit wallet
  ```

### 🟢 develop (Branche principale)
- **État**: Production Ready
- **Rôle**: Main branch pour PRs
- **Divergence**: 0 commits (stable)
- **Contient**: Core features stables

### 🔵 security (Branche sécurité avancée)
- **Commits ahead vs develop**: 6
- **État**: Stable
- **Spécificité**: 
  - ✅ Implémentation complète JWT (`JwtService.java`)
  - ✅ SecurityConfig avancée
  - ✅ AuthEntryPointJwt
  - ✅ JwtAuthorizationToken filter
  - ✅ Gestion complète des exceptions sécurité
  - ✅ PROJECT_STATUS.md
  - ✅ REFACTORING_SUMMARY.md
- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)

### ⚫ secure (Ancienne branche)
- **État**: Legacy/Deprecated
- **Recommandation**: Ne pas utiliser

---

## 🔄 STRATÉGIE RECOMMANDÉE

### Situation actuelle
- Branche `Jude` = avancée avec features + docs
- Branche `security` = meilleure sécurité
- Branche `develop` = stable

### Options de merge

**Option 1: Merge Jude → develop** (Recommandé)
```bash
git checkout develop
git merge Jude
git push origin develop
```
✅ Avantages:
- Conserve tous les commits de Jude
- Mise à jour principale avec latest features
- Documentation incluse

**Option 2: Merge security → Jude** (Si sécurité avancée nécessaire)
```bash
git checkout Jude
git merge security
```
⚠️ À faire avec soin:
- Conflits potentiels sur SecurityConfig
- Mais meilleure implémentation JWT

**Option 3: Cherry-pick** (Plus conservateur)
- Prendre sélectivement les commits importants
- Plus contrôle, plus lent

---

## 📊 FICHIERS DIFFÉRENTS PAR BRANCHE

### Jude (uniquement)
```
✅ RAPPORT_STUDCASH_COMPLET.md
✅ docs/ (8 fichiers)
✅ GeminiExtractionService.java
✅ CloudinaryStorageService.java
✅ Models pour documents
```

### security (uniquement)
```
✅ PROJECT_STATUS.md
✅ REFACTORING_SUMMARY.md
✅ SecurityConfig complet
✅ JwtService avancé
✅ Exception handlers
✅ UserPrincipal
```

### Commun
```
✅ Tous les modèles (Student, Merchant, etc.)
✅ Tous les services
✅ Tous les repositories
✅ pom.xml (configs légèrement différentes)
```

---

## 🎯 RECOMMANDATIONS

1. **Court terme**: Rester sur `Jude` pour développement
2. **Avant production**: Merger security → Jude pour meilleure sécurité
3. **Avant release**: Merge Jude → develop
4. **Nettoyer**: Supprimer branche `secure` (legacy)

---

## 🚀 COMMANDES UTILES

```bash
# Voir différence entre branches
git diff Jude..security --stat

# Voir commits non mergés
git log develop..Jude --oneline

# Fusionner proprement
git merge --no-ff <branch-name>

# Voir historique
git log --graph --oneline --all
```

---

**État**: À jour  
**Dernière révision**: 2026-05-06

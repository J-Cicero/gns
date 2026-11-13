# DIRECTIVES DE GOUVERNANCE & MÉTA-MÉTHODE (BMAD + Graphify + UX Pilot + Motion)

Ce projet respecte strictement le cadre méthodologique défini dans le super-skill [meta-orchestrateur](file:///C:/Users/Eric/Downloads/jude/code/gns/.agents/skills/meta-orchestrateur/SKILL.md).

## RÈGLES FONDAMENTALES OBLIGATOIRES :
1. **Vérité Métier & Modèle Établi (BMAD)** :
   * Ne JAMAIS modifier un fonctionnement ou une règle financière déjà établie dans le mémoire StudCash (circuit fermé, avance EWA, mandat de prélèvement, rôles RBAC).
   * Respecter scrupuleusement l'architecture 3-tiers (Spring Boot 3.4 / PostgreSQL / Angular / Ionic).
2. **Analyse d'Impact Avant Action (Graphify)** :
   * Tout changement doit être précédé d'une cartographie d'impact (Entités -> Repositories -> DTOs -> Contrôleurs -> Services Frontend -> Vues).
   * Zéro régression : vérifier la compilation backend (`mvn compile`) et les types frontend à chaque étape.
3. **Expérience Utilisateur par Rôle (UX Pilot)** :
   * Maintenir l'étanchéité et l'ergonomie propre à chaque persona (Admin GNS, Banque, Étudiant, Marchand).
4. **Micro-interactions & Rigueur Visuelle (Motion)** :
   * Feedback clair sur chaque action transactionnelle, respect de la charte "Fintech Bleu Nuit".

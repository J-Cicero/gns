MINISTERE DE L'ENSEIGNEMENT
SUPÉRIEUR ET DE LA RECHERCHE
06/05/2026
REPUBLIQUE TOGOLAISE
Travail-Liberté-Patrie
PROJET POUR LE MÉMOIRE DE FIN DE FORMATION
PROJETConception et développement d'une plateforme de crédit
solidaire et de paiement numérique
NOM DU STAGIAIREWOROU Koffi Jude Prudencio
FILIÈRELicence professionnelle - Génie Logiciel
UNIVERSITÉÉcole Polytechnique de Lomé - UL
ENTREPRISEGlobal Network Solutions (GNS)
ANNÉE SCOLAIRE2025 - 2026
RESPONSABLES SUIVIEncadrant GNS
Directeur de Mémoire ​
​
​
​
​​
​​
​​
​​
​MAITRE DE STAGE
​
​
​
​
​
​
​
​
Aimé-Gérald Kandonou​
​​
​​
​​
​M. Aimé-Gerald KANDONOU
M. Aimé-Gérald KandonouM.
studCash — Cahier des Charges Technique et FonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
TABLE DES MATIÈRES
1. Contexte et problématique​
1.1. Contexte du projet​
1.2. Constats et impacts​
1.3. Problématique​
1.4. Notre réponse​
2. Acteurs et partenaires institutionnels​
2.1. Vue d'ensemble des acteurs​
2.2. La maîtrise d'œuvre - Global Network Solutions (GNS)​
2.3. Les utilisateurs directs​
2.4. Les partenaires institutionnels​
2.4.1. L'Université de Lomé (UL)​
2.4.2. Les Banques Partenaires​
2.4.3. La Direction des Bourses et des Stages (DBS)​
2.4.4. L'Admin UL (Percepteur)​
2.5. Tableau récapitulatif des conventions​
3. Glossaire et terminologie​
4. Besoins fonctionnels par module​
4.1. Vue d'ensemble des modules​
4.2. Module Étudiant​
4.3. Module Commerçant (Boutique)​
4.4. Module Administrateur GNS​
4.5. Modules Institutionnels (UL & DBS)​
4.6. Module Portail Partenaire Bancaire​
5. Spécifications techniques détaillées​
5.1. Choix technologiques (Tech Stack)​
5.2. Architecture logicielle​
5.3. Modèle de données​
5.3. Concepts Métiers Principaux​
5.4. Sécurité et confidentialité​
6. Règles métier et cas d'usage spéciaux​
6.1. Flux de l'étudiant L1 (Règle d'accès et déblocage)​
6.2. Cas n°1 - Retard prolongé de la DBS (Gestion de trésorerie)​
6.3. Cas n°2 - Épuisement du budget d'une boutique​
6.4. Cas n°3 - Échec technique en cours de transaction​
6.5. Cas n°4 - Perte du statut boursier​
6.6. Éligibilité annuelle et plafonds​
6.7. Gestion de fin de cycle et Réconciliation bancaire​
7. Planification et livrables​
7.1. Chronogramme des phases​
​
Studcash - cahier des charges technique et fonctionnel
3
3
4
4
5
5
5
5
5
5
6
6
6
6
6
7
8
8
8
8
9
9
9
9
9
10
10
10
11
11
11
12
12
12
12
12
12
13
13CAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
7.2. Liste des livrables clés​
8. Budget prévisionnel et viabilité​
8.1. Investissement initial (CAPEX)​
8.2. Coûts de fonctionnement mensuels (OPEX)​
8.3. Modèle de revenus et seuil de rentabilité​
​
Studcash - cahier des charges technique et fonctionnel
13
13
14
14
14CAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
1. Contexte et problématique
1.1. Contexte du projet
GNS est une entreprise leader dans la transformation numérique et les services innovants au
Togo. studCash se positionne comme une solution de Fintech sociale et une plateforme de
crédit solidaire spécifiquement conçue pour répondre aux besoins critiques de la communauté
estudiantine de l'Université de Lomé. Sa mission est de transformer la précarité financière des
étudiants en un levier de réussite académique grâce à la technologie.
1.2. Constats et impacts
L'environnement financier des étudiants à l'Université de Lomé est marqué par une instabilité
structurelle. Le système de soutien étatique repose sur l'octroi de bourses et secours dont les
montants varient entre 36 000 FCFA et 54 000 FCFA par trimestre, attribués selon les
résultats de l'année précédente. Cette infrastructure souffre de défaillances critiques qui
impactent directement la réussite académique :
●​ Retards administratifs chroniques : Les versements effectués par la Direction des
Bourses et des Stages (DBS) subissent des délais pouvant atteindre six mois.
●​ Solutions de fortune : Pour pallier l'absence de revenus, les étudiants se tournent vers
des systèmes informels et risqués (tontines WhatsApp) ou s'endettent auprès de leur
entourage.
●​ Exclusion bancaire structurelle : Le secteur bancaire traditionnel juge ce public non
rentable ou trop risqué, n'offrant aucun produit de découvert ou de crédit à court terme
adapté à la modicité des bourses.
Indicateur CléValeur / État Actuel
Population cibleÉtudiants boursiers de l'Université de Lomé
Revenu trimestriel36 000 FCFA (Passable) – 54 000 FCFA
(Assez Bien/Bien)
Délai de retard (DBS)Elle peut aller jusqu'à 6 mois
Alternatives actuellesTontines WhatsApp, dettes personnelles
1.3. Problématique
Cette situation crée un stress financier permanent qui impacte directement la concentration et
les performances académiques des apprenants. Face à ce constat, une question centrale guide
le développement de notre solution :
« Comment garantir aux étudiants un accès immédiat à leurs ressources financières pour
sécuriser leur parcours académique malgré les retards de versement des bourses ? »
Pour répondre à cette interrogation, studCash doit relever trois défis interconnectés :
●​ L'Accessibilité : Créer un mécanisme de financement qui ne nécessite pas les
garanties classiques exigées par les banques.
​
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
●​ La Solidarité : S'appuyer sur la force du collectif universitaire pour garantir la
viabilité du modèle.
●​ La Réactivité : Proposer un service numérique capable de débloquer des fonds
instantanément, comblant le vide laissé par les délais de la DBS.
1.4. Notre réponse
studCash propose à l'étudiant de transformer ce qu'il possède déjà c'est à dire sa bourse future
garantie par l'État en un pouvoir d'achat immédiat. L'innovation repose sur un mécanisme de
confiance technologique : puisque la bourse est une créance certaine, studCash avance à
l'étudiant une partie de sa prochaine tranche avant même son versement officiel (jusqu'aux
14/15 du montant trimestriel).ceci dit pour les début tous les étudiants seront considérés
comme ayant accès à une valeur de 36000 via la bourse .
Cette solution s'appuie sur trois piliers fondamentaux :
●​ L'immédiateté : Accès aux fonds dès le début du trimestre pour couvrir les frais de
scolarité et les supports de cours.
●​ L'automaticité : Le remboursement est prélevé directement sur le compte bancaire de
l'étudiant dès que la bourse est versée par la DBS, sans intervention manuelle.
●​ La sécurité : Un réseau de commerçants partenaires sur le campus accepte les
paiements via un système de double décrémentation garantissant l'équilibre financier
de chaque transaction.
2. Acteurs et partenaires institutionnels
L'étudiant signe un mandat de prélèvement physique au guichet de sa banque, puis doit
prendre en photo ce mandat comportant le cachet de la banque (la "souche tamponnée") pour
le téléverser dans l'application studCash. Les banques ne sont pas de simples exécuteurs
passifs, mais des partenaires actifs qui valident la réception de ces mandats physiques via des
échanges réguliers avec GNS.
2.1. Vue d'ensemble des acteurs
Le fonctionnement de studCash repose sur un écosystème collaboratif où chaque acteur
remplit une mission précise pour sécuriser l'avance boursière. Le système distingue la
maîtrise d'œuvre, les utilisateurs finaux et les partenaires institutionnels qui garantissent la
légitimité financière du projet.
2.2. La maîtrise d'œuvre - Global Network Solutions (GNS)
GNS est l'entreprise responsable de la conception, du développement et de l'exploitation
technique de la plateforme. Elle assure la sécurité de l'infrastructure, la gestion des flux de
données et sert d'interlocuteur central pour les conventions avec l'université et les banques
partenaires.
2.3. Les utilisateurs directs
Ce sont les acteurs qui interagissent quotidiennement avec l'application mobile responsive
(PWA) :
●​ L'Étudiant de l'Université de Lomé : Bénéficiaire de l'avance boursière. Il consulte
son solde , procède au paiement de ces achat
​
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
●​ Le Commerçant et la Boutique : Pour opérer sur la plateforme, le commerçant crée
une Boutique ou il procède à l'exposition de ces produits, la mise en place des
commandes et la consultation du Wallet Boutique.
2.4. Les partenaires institutionnels
2.4.1. L'Université de Lomé (UL)
L'UL agit comme le garant académique du système. Elle permet à StudCash de vérifier la
validité de l'inscription de l'étudiant et son éligibilité académique afin de définir le plafond de
l'avance. Deux niveaux d'intégration sont envisagés : vérification manuelle/OCR des
documents fournis par l'étudiant (version autonome), ou connexion directe via API au
système d'information de l'UL (version intégrée).
2.4.2. Les Banques Partenaires
Les banques sont les exécuteurs du remboursement automatique. En vertu du mandat de
prélèvement signé électroniquement par l'étudiant, elles récupèrent le montant dû à studCash
dès que la bourse est créditée. Les partenaires cibles sont : La Poste, IBbank, Banque Coris,
OraBank, EcoBank.
Nous envisageons que le meilleur procédure pour remplir ces procedure de maniere a etre
superviser serai à la création du compte étudiant à la banque à laquelle ce dernier a été affecté
2.4.3. La Direction des Bourses et des Stages (DBS)
La DBS conserve son rôle traditionnel de versement des allocations trimestrielles sur les
comptes bancaires. Dans le modèle studCash, son action est le déclencheur passif du
remboursement. Elle collabore en informant GNS des calendriers de versements
prévisionnels.
2.4.4. L'Admin UL (Percepteur)
Cet acteur dispose d'un Wallet Admin UL dédié. C'est sur ce portefeuille que sont versés tous
les frais de scolarité payés par les étudiants via l'application. L'UL peut ensuite procéder au
retrait de ces sommes directement à la banque.
2.5. Tableau récapitulatif des conventions
PartenaireType d'accord
Université de LoméConvention
données
Direction
(DBS)
​
des
Bourses
Convention
d'information
Urgence
institutionnelle
de
+
coopération
accèsHaute
etHaute
Banques PartenairesMandat de prélèvement automatiqueCritique
Admin ULProtocole de retrait et d'encaissementMoyenne
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
3. Glossaire et terminologie
Ce glossaire définit les termes techniques et métiers essentiels pour comprendre le
fonctionnement de studCash. Il sert de référence pour l'architecture logicielle et les règles
financières du système.
TermeDéfinition technique et métier
Portefeuille ÉtudiantCompte virtuel interne stockant le solde disponible pour les
achats. Ce n'est pas un compte bancaire.
Portefeuille BoutiqueCompte virtuel interne du commerçant, crédité lors d'un
paiement étudiant via Double Décrémentation et soumis au
Plafond de Versement.
Double Décrémentation
Wallet Admin UL
Versement
Disbursement)
Portefeuille dédié de l'Université de Lomé, réceptacle unique
de tous les frais de scolarité payés via studCash.
(Mass
Crédit Fléché
Fonctionnalité d'administration permettant d'injecter des fonds
sur les portefeuilles des Étudiants, des Boutiques ou de
l'Admin UL.
Protocole de sécurité financière majeure appliqué aux étudiants
de L1. Le portefeuille est verrouillé, garantissant que la bourse
est obligatoirement affectée au paiement des frais de scolarité
de l'UL avant toute autre dépense.
KYC
(Know
Customer)
​
Action atomique qui diminue le solde du Wallet Étudiant et
augmente simultanément le solde du Wallet Boutique (ou
Wallet Admin UL pour la scolarité).
Your
Étape de vérification d'identité et du statut académique (carte
étudiante ou relevé) requise avant l'activation du portefeuille.
OCRTechnologie utilisée pour lire automatiquement les relevés de
notes et extraire les mentions ou crédits validés.
Mandat de prélèvementAutorisation légale signée numériquement par l'étudiant
permettant à sa banque de rembourser studCash dès l'arrivée de
la bourse.
Portail
BancaireInterface web allégée destinée aux banques pour valider les
mandats et confirmer l'exécution des prélèvements par lots.
Partenaire
Souche TamponnéePreuve physique (papier avec cachet de la banque) attestant
que l'étudiant a bien déposé son mandat de prélèvement. Sa
photo est exigée lors du KYC.
Gel de portefeuilleStatut temporaire désactivant les achats pour un étudiant
pendant la période de transition entre le versement de la DBS
et la confirmation du prélèvement par la banque.
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
Identifiant
(UUID)
de
suivi
Identifiant unique universel exposé par l'API REST, remplaçant
les IDs internes pour renforcer la sécurité.
4. Besoins fonctionnels par module
4.1. Vue d'ensemble des modules
studCash est structuré autour de six modules distincts, chacun destiné à un acteur précis du
système. Chaque module expose uniquement les fonctionnalités dont son utilisateur a besoin,
garantissant ainsi la sécurité et la clarté de la plateforme. .
4.2. Module Étudiant
L'interface étudiante est conçue comme une Progressive Web App (PWA) pour faciliter
l'accès sur les campus sans nécessiter de téléchargement depuis un magasin d'applications.
●​ Inscription et validation (KYC) : Création de compte, téléversement des documents
académiques (relevé du BAC ou carte d'étudiant) et de la photo de la "souche
tamponnée", et signature électronique obligatoire du mandat de prélèvement pour
activer le portefeuille.
●​ Carte Physique (Post-KYC) : Après validation du KYC, l'étudiant reçoit une carte
physique en PVC dotée d'un QR code statique lié à son compte. L'application mobile
permet de déclarer la perte de cette carte, entraînant son blocage immédiat.
●​ Gestion des portefeuilles (Wallets) : Consultation en temps réel du solde disponible
correspondant à l'avance trimestrielle calculée selon les crédits validés.
●​ Génération de paiement (QR Code) : L'étudiant choisit son portefeuille et génère un
QR Code éphémère scannable par le commerçant. Une commission de service de 1%
est automatiquement ajoutée au prix de l'article.
●​ Crédit Fléché (L1) : Après validation par l'UL, l'étudiant de première année peut
uniquement payer sa scolarité via l'application. Le déblocage complet du portefeuille
s'effectue après confirmation du paiement et clôture de l'inscription.
●​ Éducation financière (UX) : Affichage d'une jauge de consommation avec code
couleur (vert, orange, rouge), des alertes de seuil, et l'affichage du "Reste à vivre"
prévisionnel.
4.3. Module Commerçant (Boutique)
L'interface commerçant est pensée pour la rapidité au comptoir et la gestion simplifiée du
catalogue.
●​ Encaissement sécurisé : Le commerçant utilise un scanner intégré pour lire le QR
code (mobile de l'étudiant) ou le QR code de la carte physique de l'étudiant.
L'encaissement nécessite une étape de sécurité obligatoire : la saisie par l'étudiant d'un
code PIN secret à 4 chiffres sur l'appareil du commerçant. En cas d'échec, les
messages affichés au commerçant doivent être rédigés avec tact (ex: 'Paiement non
autorisé, invitez l'étudiant à consulter son application'). L'opération repose sur la
Double Décrémentation atomique : débit simultané du portefeuille étudiant et crédit
du wallet boutique.
​
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
●​ Gestion du plafond de vente (Flux) : Chaque boutique se voit allouer un plafond de
reversement mensuel maximal. Si ce plafond est atteint, la boutique est
automatiquement bloquée pour les paiements studCash jusqu'au réapprovisionnement.
●​ Gestion du catalogue (Click & Collect) : Le commerçant peut ajouter, modifier ou
supprimer des produits (nom, prix, stock) visibles aux étudiants dans l'application.
4.4. Module Administrateur GNS
C'est le tableau de bord de supervision (Back-office) principal de la plateforme.
●​ Validation KYC : Examen des dossiers étudiants et commerçants pour valider ou
rejeter les comptes. La validation intègre un filtre IA (OCR) en première ligne de
défense pour rejeter automatiquement les photos floues ou illisibles avant la
vérification humaine.
●​ Gestion des flux : Allocation et approvisionnement mensuel des plafonds de
versement des boutiques partenaires (gestion des flux de sortie des avances).
●​ Suivi des remboursements : Enregistrement des retours bancaires suite aux
versements de la DBS, et réinitialisation des portefeuilles pour le trimestre suivant.
4.5. Modules Institutionnels (UL & DBS)
●​ Université de Lomé (UL) : Interface permettant de recevoir les notifications de
paiement de scolarité (pour débloquer les étudiants L1) et de vérifier les statuts
d'inscription.
●​ Direction des Bourses (DBS) : Accès en lecture seule à des statistiques agrégées
(nombre de bénéficiaires, montants totaux avancés) pour faciliter la planification des
versements officiels.
4.6. Module Portail Partenaire Bancaire
Cette interface Web sécurisée permet aux banques de valider les mandats papier des étudiants
et de confirmer à GNS l'exécution des prélèvements par lots.
5. Spécifications techniques détaillées
Cette section détaille les choix technologiques retenus, leurs justifications techniques et
l'architecture globale du système.
5.1. Choix technologiques (Tech Stack)
Pour garantir la fiabilité des transactions financières, les technologies suivantes ont été
sélectionnées selon des critères de robustesse, de performance et d'adéquation au contexte
africain :
​
ComposantTechnologie retenueJustification principale
BackendSpring Boot 3.0+ (Java 21
LTS)API REST robustes avec gestion
native des transactions ACID
Base de donnéesPostgreSQL 16Intégrité financière garantie
propriétés ACID strictes
Studcash - cahier des charges technique et fonctionnel
—CAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
Frontend / PWAAngular 17+ (Progressive
Web App)Installable
sans
magasin
d'applications, idéal pour appareils
d'entrée de gamme
AuthentificationSpring Security + JWT
(JSON Web Tokens)Sessions stateless sécurisées, sans
état serveur
HachageBCryptMots de passe jamais stockés en
clair
Identifiants APIUUID (Tracking ID)Masquage de la structure interne de
la base de données
ChiffrementHTTPS via Let's EncryptCommunications chiffrées de bout
en bout
5.2. Architecture logicielle
Le backend suit une architecture en couches strictes séparant clairement les responsabilités,
conformément aux bonnes pratiques de l'ingénierie logicielle :
●​ Couche Contrôleurs : Point d'entrée de l'API , il reçoit et valide les requêtes JSON
entrantes.
●​ Couche Services : Contient toute la logique métier (calcul des plafonds, Double
Décrémentation, règles de verrouillage).
●​ Couche Dépôts: Interface de communication avec la base de données via
JPA/Hibernate.
●​ Couche Modèles : Représentation objet des données en base de données.
Le frontend est développé sous forme de Progressive Web App (PWA) avec Angular,
installable directement sur smartphone sans passer par un magasin d'applications — ce qui est
idéal pour les appareils d'entrée de gamme courants dans le contexte togolais.
5.3. Modèle de données
5.3. Concepts Métiers Principaux
​
Objet FonctionnelRôle dans le système
UtilisateurReprésente l'identité et les droits d'accès (Étudiant, Commerçant,
Administrateur).
ÉtudiantGère le profil académique, la validation d'identité et le suivi du
mandat bancaire (photo de la souche tamponnée).
CommerçantDéfinit le partenaire marchand responsable d'un ou plusieurs
points de vente.
BoutiquePoint de rencontre entre l'offre (produits) et la demande
(étudiants) sur le campus.
WalletGère les soldes, le verrouillage L1 et le statut temporaire 'Gelé'
lors des réconciliations bancaires.
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
ProduitArticles disponibles
partenaires.
dans
les
rayons
des commerçants
CommandeMatérialise l'intention d'achat avant la validation finale du
paiement.
PaiementTrace de la transaction avec calcul automatique de la commission
de service.
VersementFlux de fonds injectant les avances ou reversant les gains aux
commerçants.
Carte studCashSupport physique en PVC contenant un QR code statique
(identifiant unique de l’étudiant). Précise que cette carte ne
stocke aucun solde de manière autonome et elle possède un statut
gérable (Active, Perdue, Remplacée).
5.4. Sécurité et confidentialité
La sécurité est intégrée « by design » à chaque niveau de l'architecture. Les mesures
suivantes garantissent la protection des données financières et personnelles des utilisateurs :
●​ Authentification JWT : Utilisation de tokens signés pour des sessions stateless
sécurisées, évitant le stockage d'état côté serveur.
●​ Hachage BCrypt : Les mots de passe ne sont jamais stockés en clair dans la base de
données.
●​ Tracking ID (UUID) : Seuls des identifiants uniques complexes circulent dans l'API
pour masquer la structure interne de la base de données.
●​ HTTPS (Let's Encrypt) : Toutes les communications entre le client et le serveur sont
chiffrées de bout en bout.
●​ Atomicité ACID : La Double Décrémentation est une transaction atomique
PostgreSQL , il est techniquement impossible qu'un étudiant soit débité sans que la
boutique soit créditée.
6. Règles métier et cas d'usage spéciaux
Un système financier robuste ne se juge pas uniquement sur son fonctionnement nominal,
mais sur sa capacité à gérer les situations atypiques de manière prévisible et automatisée.
Cette section documente les règles appliquées par studCash face aux imprévus.
6.1. Flux de l'étudiant L1 (Règle d'accès et déblocage)
L'accès au système est conditionné par une chaîne d'événements stricte garantissant
l'éligibilité académique avant tout accès au crédit :
●​ Pré-inscription : L'étudiant crée son compte sur studCash mais reste en attente de
validation.
●​ Notification UL : Dès que le système d'inscription de l'université aura validé
l'étudiant, studCash en est informé automatiquement.
●​ Accès restreint (Crédit Fléché) : Mise en œuvre du Protocole de Crédit Fléché :
l'étudiant peut uniquement payer sa scolarité via l'application. Cette restriction
​
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
garantit le remboursement de l'UL en priorité et constitue la seule fonctionnalité
active tant que l'inscription n'est pas finalisée à 100%.
●​ Déblocage complet : Une fois le paiement confirmé et l'inscription clôturée, le
portefeuille est ouvert pour les achats en boutique.
6.2. Cas n°1 - Retard prolongé de la DBS (Gestion de trésorerie)
C'est le scénario le plus critique. La créance étant garantie par l'État, le risque de
non-remboursement est quasi nul. Cependant, un retard excessif impacte la trésorerie
immédiate de GNS. Le système applique les règles suivantes :
●​ Maintien du service (trésorerie positive) : L'administrateur continue d'assurer la
Gestion des Reversements vers les Portefeuilles Boutique. L'étudiant conserve son
solde et peut effectuer ses achats normalement.
●​ Blocage préventif (changement de trimestre) : Si la DBS accuse un retard tel qu'un
nouveau trimestre commence sans remboursement du précédent, le système bloque
automatiquement la nouvelle avance jusqu'à l'exécution du prélèvement bancaire.
6.3. Cas n°2 - Épuisement du budget d'une boutique
●​ Blocage automatique : Si le plafond de reversement atteint zéro, la boutique est
instantanément désactivée pour les paiements studCash. Tout scan de QR Code
affiche un message d'indisponibilité temporaire.
●​ Réapprovisionnement : Le commerçant est notifié et peut solliciter l'administrateur
GNS pour un réapprovisionnement manuel du plafond de reversement.
6.4. Cas n°3 - Échec technique en cours de transaction
Si une erreur réseau ou serveur survient au moment exact du paiement, le mécanisme ACID
garantit une annulation totale (Rollback) de la transaction. Il est techniquement impossible
que l'étudiant soit débité si la boutique n'a pas été créditée. Le QR Code utilisé est
définitivement invalidé.
6.5. Cas n°4 - Perte du statut boursier
Dès réception de l'information par l'UL, le portefeuille de l'étudiant est immédiatement
verrouillé par l'administrateur. La dette existante reste due et sera prélevée sur le compte
bancaire lors du versement de la dernière tranche à laquelle l'étudiant avait droit. En cas
d'insuffisance de solde, une procédure de recouvrement manuel est activée.
6.6. Éligibilité annuelle et plafonds
À chaque nouvelle rentrée universitaire, le système procède à la réévaluation automatique des
critères académiques (crédits/mentions) de l'étudiant selon les règles de l'UL. Si l'étudiant ne
remplit plus les critères académiques, son accès aux nouveaux versements est
automatiquement bloqué.
●​ L1 (Mention BAC) : Passable → 36 000 FCFA ; Assez Bien / Bien → 54 000 FCFA.
●​ L2/L3 (Crédits ECTS) : Seuils de 25, 50, 54 ou 108 crédits selon l'année d'étude.
●​ Sanction : Si l'étudiant ne remplit plus les conditions (résultats insuffisants ou
dépassement de l'âge limite de 25 ans), le compte est bloqué pour les nouveaux
versements.
6.7. Gestion de fin de cycle et Réconciliation bancaire
​
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
Le renouvellement du solde trimestriel de l'étudiant n'est pas automatique au moment du
virement de la DBS. Pour garantir l'équilibre financier du système, studCash applique un flux
sécurisé en trois étapes :
●​ Gel temporaire : L'Admin GNS gèle les portefeuilles dès l'annonce des versements
par la DBS pour empêcher de nouvelles dettes.
●​ Réconciliation : GNS attend la confirmation des banques partenaires (via le portail
ou par fichier) que les prélèvements ont bien été effectués.
●​ Déblocage massif : L'Admin GNS utilise une fonctionnalité de traitement par lots
pour réinitialiser et débloquer les portefeuilles des étudiants dont le prélèvement a
réussi.
7. Planification et livrables
Le projet studCash se déroule sur une période de huit semaines, suivant une méthodologie
Agile pour garantir la livraison de composants testables à chaque étape. Le stage a
officiellement débuté le 7 avril 2026.
7.1. Chronogramme des phases
PhaseSemainesActivités principalesJalons
1. Initiation et conceptionS1 - S2Validation du CDC, modélisation
de la base de données
et
architecture UML.J1
:
Conception
2. Implémentation
backendduS3 - S4Développement de l'API Spring
Boot, sécurité JWT et logique de
Double Décrémentation.J2 : MVP
Backend
3.
Implémentation
FrontendS5 - S6Développement de l'application
PWA
Angular
(interfaces
Étudiant, Commerçant et Admin).J3 : Version
Bêta
4.
Validation
déploiementS7 - S8Tests d'intégration, déploiement
sur VPS Linux avec HTTPS et
rédaction du rapport final.J4 : Livraison
finale
et
7.2. Liste des livrables clés
●​ Livrable 1 : Cahier des Charges complet (le présent document).
●​ Livrable 2 : Code source documenté (Backend Spring Boot & Frontend Angular) sur
dépôt Git.
●​ Livrable 3 : API REST documentée via Swagger
●​ Livrable 4 : Application déployée et accessible via une URL sécurisée (HTTPS).
●​ Livrable 5 : Rapport de fin de cycle et Guide Utilisateur.
8. Budget prévisionnel et viabilité
​
Studcash - cahier des charges technique et fonctionnelCAHIER DES CHARGES TECHNIQUE ET FONCTIONNEL
Le budget est structuré en coûts d'investissement initiaux (CAPEX) et en frais de
fonctionnement récurrents (OPEX), conformément aux standards de gestion de projet.
8.1. Investissement initial (CAPEX)
Poste de dépenseDétailMontant
estimé
Nom de domaineRéservation annuelle d'une extension .tg78 000 FCFA
CommunicationImpression de supports physiques pour le
campus de l'UL100 000 FCFA
Ressources HumainesIndemnités de stage et supervision senior GNS300 000 FCFA
Matériel d'impressionImprimante thermique pour cartes PVC et lot
de badges plastiques vierges pour fabriquer les
cartes physiques studCash des étudiants362000 FCFA
TOTAL INITIAL
840000 FCFA
8.2. Coûts de fonctionnement mensuels (OPEX)
Pour maintenir la plateforme en ligne et opérationnelle, les frais fixes mensuels s'élèvent à :
PosteMontant
mensuel
Hébergement VPS (Serveur Linux 4 Go RAM)25 000 FCFA
Stockage Cloud (Sauvegardes et documents KYC)7 500 FCFA
TOTAL MENSUEL32 500 FCFA
8.3. Modèle de revenus et seuil de rentabilité
studCash génère des revenus via une commission de 1% sur chaque transaction effectuée en
boutique, supportée par l'étudiant. Ce modèle permet d'atteindre le seuil de rentabilité très
rapidement :
●​ Transaction moyenne estimée : 3 000 FCFA → génère 30 FCFA de commission.
●​ Nombre de transactions mensuelles nécessaires pour couvrir l'OPEX (32 500 FCFA) :
environ 1 084 transactions.
●​ Projection : Avec une base de quelques centaines d'étudiants actifs sur le campus, ce
seuil est atteint dès les premières semaines d'exploitation.
La solidité du modèle repose sur la garantie étatique de la bourse, qui élimine le risque de
créance irrécouvrable, cela différencie fondamentalement studCash des modèles de
microcrédit classiques.
​
Studcash - cahier des charges technique et fonctionnel
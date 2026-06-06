# Cycle de vie de l'année scolaire - StudCash (GNS)

Ce document définit les moments d'intervention de chaque acteur tout au long de l'année scolaire.

## Phase 1 : Initialisation & Paramétrage (Avant la rentrée)
*   **Acteur : Administrateur GNS**
    *   Action : Création des comptes Administrateurs (Univ, DBS, Banque). TOUT D'Abord 
    * l'admin creer l'adnne qui est a ferme au debut juste que i faut que c chaque utilisateur du web soit au courant que gns viend  de creer la nouvelle annner 

    *   Action :  Configuration des paramètres système (`ParametreGnsController`). oui 
    *   ACTION : grace au mesage de creation de l'anne scolaire on fait la  creation des parmater d'eligiilite par l'admin DBS c'est alui d eles metre en place ce ne sont pas ces datesender qui von tnous aier a la fin on ne dois tplsu utiliser ce fichier  
    *   Action : Ouverture de l'année scolaire (`ScolariteYearController`).( des con ouverure le sparametre de gns et ceu d'eligibilite de BDS ne seront plus utiliser ne doit pls etre modifier jusqu"a la fermeture de l'annee ) 
    a cette ouverture les wallet des universite et des etudiant doivent etre a 0 ceux des boutique non (elle ne doivent pas etre remise a zero )

## Phase 2 : Onboarding & Inscriptions (Début d'année)
*   **Acteur : Administrateur Université**
    *   Action : Import/Création des étudiants.
    *   Action : Validation des inscriptions (`InscriptionAnnuelleController`).
*   **Acteur : Étudiant**
    *   Action : Validation KYC (`DocumentEtudiantController`).
    *   Action : Activation du compte / Wallet. qui sont a 0 
    *   Action faire les paiement de scolarite 
    *   Remplir les information sur les banque des etudiant (sans cette inormation il faudrait que l'on ne consider pas le paiement de scolarite come reçu qu'est ce que tu en pense ou est ce que on pourrais la permett) a quele momentou onpurais aussi ne pas le limiter apres tout si le paiement n'et pas fais dans le bon delais l'universite peux le rejet vaut mei rester a 3 mois 

## Phase 3 : Vie quotidienne & Opérations (En cours d'année)
*   **Acteur : Étudiant**
    *   Action : Paiements QR (`PaiementController`).
    *   Action : Consultations de solde (`WalletController`).
*   **Acteur : Commerçant / Boutique/ Universite**
    *   Action : Gestion des produits et stocks (`ProductController`).
    *   Action : Encaisser les paiements.
    *   Action : Encaisser les paiement de scolarite (je ne saispa si on va le ger au backend ou au frontnen mais je pense qu'il serai bien de ne plsu parmettre les paiement de scolarite apres un certiane temps gerne 2 mois ou 3 mois  apres l'activation des compte wallet )
*   **Acteur : Opérateur Banque**
    *   Action : Surveillance des transactions (`BanqueEtudiantController`).
    *   Action : Validation/Suivi des flux financiers.

## Phase 4 : Clôture & Reporting (Fin d'année)
*   **Acteur : Administrateur DBS**
    *   Action : Analyse des statistiques (`ParametreDbsController`).
    *   Action je vueux quon au front 4 bouton qui signifif le versement des bourmsenent par la dbs a chque fois les univeriste la banque et gns doivent etr einformer et a c emoment la a chque fois que un versement est fais ou les deux a la fois  bien sur 1 versment versment represente la bourse de l'eudion 36000 ou 54000 si c'esr 2 versment en meme tant la some est ultillier  a pres ça GnS doit geler toute es action depaiement possibe e gelan le wallet il faudrai aussi gelle l'action de paiement scolariteuatan y ettre un boolean coe a ça regle auss notre probleme de temps que j'vais di a deniere fois  une fois les activite gele les banque reçisvent le mesage sur leur interfaces et commence a effectuer les triameent maitenant que l'argent es la il faudra determinier ce qui est depense par les tudiant pour des paiement cela ira sur le compte de GS que GNS AURA OUVERT SUR CETTE BANQUE il vera aussi les versement effectuer au univesite pour les paiement de scolarite et devaenvoyer cette donner argent dans le compte reel que ces univeriste n crer sur ces banque et on devrias versemet le reste de ssome restante au etudiant selon ce  qu'il ont reçu et ce qu'il on depense sera revrsme sur le compte relle et visible sur leur aplication les banque ne pourrons faire ces actio que sur les compte des univeriste des institution et des etudiant qui lui snt affilier don ces seuelement ces information la qu'i verons 

    **Au meme moment DES QUE L'UNIVERISTE RE__OIS E ESSAHE DE GELE DE COMPTE DE WALLET FAIS PAR l'admin GNS il devra aussi telecharger les reçus des somme qu'i doit recuperr a chque banque est ce que tu comprend la et a ce moment apres_a tut les compt sont remis a zero et si il ' ad'autre versement de GNS -- > Etudiant ou de l'DBS vers es etudant ça va fonctionner de la meme maniere bine sur lu='univeriste n' pas besoind'attendre jusqu'a ce que GNs GEE LES COMMPTE PUR TELECHARGER ÇA SI GNS REND IMPOSOBLE LES PAIEMENT DE SCOLARITE ALORSIL PEUT E FAIRE DES CE MOMENT VU QUE RIN NE VIENDR PLUS SUR SON WALLET EST  QUEC 'EST CLAIRE  LA 
*   **Acteur : Administrateur GNS**
    *   Action : Clôture de l'année scolaire.
    *   Action : Archivage des données.

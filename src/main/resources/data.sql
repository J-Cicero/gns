-- RÈGLES DE BOURSE DBS INITIALES
INSERT INTO REGLE_BOURSE_DBS (tracking_id, type_regle, valeur_critere, est_actif, description, created_at, updated_at) 
VALUES 
(gen_random_uuid(), 'AGE_MAX_LICENCE', 26, true, 'Âge maximum pour la Licence', now(), now()),
(gen_random_uuid(), 'MONTANT_TRANCHE_STANDARD', 36000, true, 'Montant de la bourse standard (FCFA)', now(), now()),
(gen_random_uuid(), 'MONTANT_TRANCHE_SUPERIEURE', 54000, true, 'Montant de la bourse supérieure (FCFA)', now(), now()),
(gen_random_uuid(), 'L1_MOYENNE_MIN_PASSABLE', 10, true, 'Moyenne BAC minimum pour bourse standard', now(), now()),
(gen_random_uuid(), 'L1_MOYENNE_MIN_MENTION_SUP', 12, true, 'Moyenne BAC minimum pour bourse supérieure', now(), now()),
(gen_random_uuid(), 'L2_CREDITS_MIN_STANDARD', 25, true, 'Crédits L1 minimum pour bourse standard en L2', now(), now()),
(gen_random_uuid(), 'L2_CREDITS_MIN_SUPERIEUR', 54, true, 'Crédits L1 minimum pour bourse supérieure en L2', now(), now()),
(gen_random_uuid(), 'L3_CREDITS_MIN_STANDARD', 50, true, 'Crédits cumulés minimum pour bourse standard en L3', now(), now()),
(gen_random_uuid(), 'L3_CREDITS_MIN_SUPERIEUR', 108, true, 'Crédits cumulés minimum pour bourse supérieure en L3', now(), now()),
(gen_random_uuid(), 'L4_CREDITS_MIN_STANDARD', 80, true, 'Crédits cumulés minimum pour recyclage L4', now(), now()),
(gen_random_uuid(), 'L5_CREDITS_MIN_STANDARD', 150, true, 'Crédits cumulés minimum pour recyclage L5', now(), now());

-- DOCUMENTS REQUIS PAR NIVEAU
INSERT INTO DOCUMENT_REQUIS (niveau, type_document, obligatoire, est_actif, created_at, updated_at)
VALUES
('L1_ANNEE', 'RELEVE_BAC', true, true, now(), now()),
('L1_ANNEE', 'FICHE_UE', true, true, now(), now()),
('L2_ANNEE', 'RELEVE_NOTES', true, true, now(), now()),
('L2_ANNEE', 'FICHE_UE', true, true, now(), now()),
('L3_ANNEE', 'RELEVE_NOTES', true, true, now(), now()),
('L3_ANNEE', 'FICHE_UE', true, true, now(), now()),
('L4_ANNEE', 'RELEVE_NOTES', true, true, now(), now()),
('L4_ANNEE', 'FICHE_UE', true, true, now(), now()),
('L5_ANNEE', 'RELEVE_NOTES', true, true, now(), now()),
('L5_ANNEE', 'FICHE_UE', true, true, now(), now());

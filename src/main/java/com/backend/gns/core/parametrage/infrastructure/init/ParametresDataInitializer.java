package com.backend.gns.core.parametrage.infrastructure.init;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.models.ParametreGns;
import com.backend.gns.core.parametrage.infrastructure.repositories.ParametreGnsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParametresDataInitializer implements CommandLineRunner {

    private final ParametreGnsRepository repository;

    @Override
    public void run(String... args) {
        initParam(
                TypeParametreGns.MONTANT_BOURSE_BASE,
                "25000",
                new BigDecimal("36000"),
                36000,
                "Montant de base prévisionnel d'une bourse étudiante pour l'exercice (en FCFA). Le montant réel est versé individuellement sur le wallet selon l'éligibilité annuelle de chaque étudiant."
        );

        initParam(
                TypeParametreGns.MONTANT_BOURSE_MAJORATION,
                "15000",
                new BigDecimal("54000"),
                54000,
                "Majoration ou bonus prévisionnel applicable aux bourses d'excellence ou sociales (en FCFA)."
        );

        initParam(
                TypeParametreGns.TAUX_COMMISSION_PAIEMENT,
                "0.02",
                new BigDecimal("0.02"),
                null,
                "Taux de commission prélevé sur chaque transaction StudCash (ex: 0.02 pour 2%)."
        );

        initParam(
                TypeParametreGns.PART_COMMISSION_GNS,
                "0.60",
                new BigDecimal("0.60"),
                null,
                "Part de la commission revenant à GNS (ex: 0.60 pour 60%, le solde de 40% revient à la banque partenaire)."
        );

        initParam(
                TypeParametreGns.FRAIS_CREATION_CARTE,
                "2000",
                new BigDecimal("2000"),
                2000,
                "Frais d'émission d'une carte physique NFC / QR Code StudCash (en FCFA)."
        );

        initParam(
                TypeParametreGns.MAJORATION_MONTANT_BOUTIQUE,
                "50000",
                new BigDecimal("50000"),
                50000,
                "Plafond journalier de dépenses autorisées par étudiant auprès d'une boutique (en FCFA)."
        );
    }

    private void initParam(
            TypeParametreGns type,
            String valeur,
            BigDecimal valeurBigDecimal,
            Integer valeurInteger,
            String description
    ) {
        if (repository.findByNomParametre(type).isEmpty()) {
            ParametreGns param = new ParametreGns();
            param.setTrackingId(UUID.randomUUID());
            param.setNomParametre(type);
            param.setValeurParametre(valeur);
            param.setValeurAsBigDecimal(valeurBigDecimal);
            param.setValeurAsInteger(valeurInteger);
            param.setDescription(description);
            repository.save(param);
            log.info("Paramètre système GNS initialisé : {} = {}", type, valeur);
        }
    }
}


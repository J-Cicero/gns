package com.backend.gns.student.domain.services;

import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import java.math.BigDecimal;

public interface EligibiliteService {

  EligibiliteResult verifierEligibilite(Student student, InscriptionAnnuelle inscription, BanqueEtudiant banque);

  class EligibiliteResult {
    public final boolean estEligible;
    public final BigDecimal plafondAccorde;
    public final String motifRejet;

    private EligibiliteResult(boolean estEligible, BigDecimal plafondAccorde, String motifRejet) {
      this.estEligible = estEligible;
      this.plafondAccorde = plafondAccorde;
      this.motifRejet = motifRejet;
    }

    public static EligibiliteResult eligible(BigDecimal plafond) {
      return new EligibiliteResult(true, plafond, null);
    }

    public static EligibiliteResult nonEligible(String motif) {
      return new EligibiliteResult(false, BigDecimal.ZERO, motif);
    }
  }
}

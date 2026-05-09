package com.backend.gns;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ArchitectureModulesTest {

    ApplicationModules modules = ApplicationModules.of(GnsApplication.class);

    @Test
    void verifierStructureDesModules() {
        modules.verify();
    }

    @Test
    void documenterLesModules() {
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }
}

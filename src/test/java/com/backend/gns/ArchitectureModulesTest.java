package com.backend.gns;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.boot.test.context.SpringBootTest; // Added import

@SpringBootTest(classes = GnsApplication.class) // Added annotation
class ArchitectureModulesTest {

  ApplicationModules modules = ApplicationModules.of(GnsApplication.class);

  @Test
  void verifierStructureDesModules() {
    modules.verify();
  }

  @Test
  void documenterLesModules() {
    new Documenter(modules).writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
  }
}

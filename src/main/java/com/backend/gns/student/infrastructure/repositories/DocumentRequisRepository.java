package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.models.DocumentRequis;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRequisRepository extends JpaRepository<DocumentRequis, Long> {

  List<DocumentRequis> findByNiveauAndEstActifTrue(StudentNiveau niveau);

  Page<DocumentRequis> findByNiveau(StudentNiveau niveau, Pageable pageable);

  boolean existsByNiveauAndTypeDocument(StudentNiveau niveau, TypeDocument typeDocument);
}

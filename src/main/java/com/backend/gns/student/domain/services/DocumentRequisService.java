package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.student.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.student.domain.enums.StudentNiveau;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentRequisService {

  DocumentRequisResponse create(DocumentRequisRequest request);

  DocumentRequisResponse update(Long id, DocumentRequisRequest request);

  void delete(Long id);

  DocumentRequisResponse findById(Long id);

  List<DocumentRequisResponse> findActiveByNiveau(StudentNiveau niveau);

  Page<DocumentRequisResponse> findAll(Pageable pageable);

  Page<DocumentRequisResponse> findByNiveau(StudentNiveau niveau, Pageable pageable);
}

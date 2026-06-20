package com.backend.gns.core.parametrage.domain.services.impl;

import com.backend.gns.core.parametrage.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.core.parametrage.application.mappers.DocumentRequisMapper;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.DocumentRequis;
import com.backend.gns.core.parametrage.domain.services.DocumentRequisService;
import com.backend.gns.core.parametrage.infrastructure.repositories.DocumentRequisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentRequisServiceImpl implements DocumentRequisService {

    private final DocumentRequisRepository documentRequisRepository;
    private final DocumentRequisMapper documentRequisMapper;

    @Override
    @Transactional
    public DocumentRequisResponse create(DocumentRequisRequest request) {
        DocumentRequis entity = documentRequisMapper.toEntity(request);
        return documentRequisMapper.toResponse(documentRequisRepository.save(entity));
    }

    @Override
    public Optional<DocumentRequisResponse> findByTrackingId(UUID trackingId) {
        return documentRequisRepository.findByTrackingId(trackingId)
                .map(documentRequisMapper::toResponse);
    }

    @Override
    public List<DocumentRequisResponse> findAll() {
        return documentRequisRepository.findAll().stream()
                .map(documentRequisMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentRequisResponse> findByNiveauRequis(com.backend.gns.student.domain.enums.StudentNiveau niveauRequis) {
        return documentRequisRepository.findByNiveauRequis(niveauRequis).stream()
                .map(documentRequisMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        DocumentRequis doc = documentRequisRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Document requis non trouvé"));
        documentRequisRepository.delete(doc);
    }
}

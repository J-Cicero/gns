package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.StudentRequest;
import com.backend.gns.application.dtos.responses.StudentResponse;
import com.backend.gns.application.mappers.StudentMapper;
import com.backend.gns.domain.models.Student;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import com.backend.gns.domain.services.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    @Transactional
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentResponse> findByTrackingId(UUID trackingId) {
        return studentRepository.findByTrackingId(trackingId)
                .map(studentMapper::toResponse);
    }

    @Override
    @Transactional
    public StudentResponse update(UUID trackingId, StudentRequest request) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé avec l'ID: " + trackingId));
        
        student.setEmail(request.email());
        student.setNom(request.nom());
        student.setPrenom(request.prenom());
        student.setTelephone(request.telephone());
        student.setDateNaissance(request.dateNaissance());
        student.setCreditsValides(request.creditsValides());
        student.setRIB(request.RIB());
        student.setCNI(request.CNI());
        student.setCheminReleve(request.cheminReleve());
        student.setStatutKYC(request.statutKYC());
        
        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé avec l'ID: " + trackingId));
        studentRepository.delete(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> findByStatutKYC(KycStatus statutKYC) {
        return studentRepository.findByStatutKYCOrderByCreatedAt(statutKYC).stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toResponse)
                .toList();
    }
}

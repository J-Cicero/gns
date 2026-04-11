package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.mappers.StudentMapper;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import com.backend.gns.domain.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {
        List<Student> students = studentRepository.findAll();
        return studentMapper.toResponseList(students);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getByTrackingId(UUID trackingId) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + trackingId));
        return studentMapper.toResponse(student);
    }

    @Override
    public StudentResponse update(UUID trackingId, StudentRequest request) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + trackingId));

        student.setNom(request.nom());
        student.setPrenom(request.prenom());
        student.setEmail(request.email());
        student.setMotDePasse(request.motDePasse());
        student.setTelephone(request.telephone());

        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toResponse(updatedStudent);
    }

    @Override
    public void delete(UUID trackingId) {
        Student student = studentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with trackingId: " + trackingId));
        studentRepository.delete(student);
    }
}

package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.enums.CardStatut;
import com.backend.gns.student.domain.models.Student;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.gns.student.domain.models.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

  Optional<Card> findByTrackingId(UUID trackingId);

  Optional<Card> findByQrCodeStatique(String qrCodeStatique);

  Optional<Card> findByStudentAndStatut(Student student, CardStatut statut);

  Page<Card> findByStudent(Student student, Pageable pageable);

  Page<Card> findByStatut(CardStatut statut, Pageable pageable);

  Long countByStudentAndStatut(Student student, CardStatut statut);
}

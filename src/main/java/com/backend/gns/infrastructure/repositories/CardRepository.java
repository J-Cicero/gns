package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.enums.CardStatus;
import com.backend.gns.domain.models.Card;
import com.backend.gns.domain.models.Student;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

  Optional<Card> findByTrackingId(UUID trackingId);

  Optional<Card> findByQrCodeStaticUuid(String qrCodeStaticUuid);

  Optional<Card> findByStudentAndCardStatus(Student student, CardStatus cardStatus);

  Page<Card> findByStudent(Student student, Pageable pageable);

  Page<Card> findByCardStatus(CardStatus cardStatus, Pageable pageable);

  Long countByStudentAndCardStatus(Student student, CardStatus cardStatus);
}

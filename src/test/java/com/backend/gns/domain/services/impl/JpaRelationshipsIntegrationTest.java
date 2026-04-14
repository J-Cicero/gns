package com.backend.gns.domain.services.impl;

import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.models.Student;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.services.StudentService;
import com.backend.gns.domain.services.WalletService;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JpaRelationshipsIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private WalletRepository walletRepository;

    private StudentResponse createdStudent;

    @BeforeEach
    public void setUp() {
        // Créer un student de test
        StudentRequest studentRequest = new StudentRequest(
                "Jean",
                "Dupont",
                "test@student.com",
                "password123",
                "0612345678",
                "MAT123456",
                "L1",
                "Scientifique",
                0,
                "RIB12345",
                "/path/to/carte",
                "/path/to/releve",
                false,
                null
        );
        createdStudent = studentService.create(studentRequest);
    }

    @Test
    public void testCreateStudentAndWallet() {
        // Given: Un student a été créé
        assertNotNull(createdStudent);
        assertNotNull(createdStudent.trackingId());

        // When: Créer un wallet HORIZON associé au student
        WalletRequest walletRequest = new WalletRequest(
                createdStudent.trackingId(),
                "HORIZON",
                0.0,
                36000.0
        );

        WalletResponse walletResponse = walletService.create(walletRequest);

        // Then: Vérifier la relation OneToMany
        assertNotNull(walletResponse);
        assertEquals(createdStudent.trackingId(), walletResponse.studentTrackingId());

        // Récupérer le student avec son wallet
        Student student = studentRepository.findByTrackingId(createdStudent.trackingId()).orElse(null);
        assertNotNull(student);
        assertNotNull(student.getWallets());
        assertEquals(1, student.getWallets().size());

        Wallet wallet = student.getWallets().get(0);
        assertEquals(student.getId(), wallet.getStudent().getId());
        assertEquals("HORIZON", wallet.getTypeWallet().name());
    }

    @Test
    public void testWalletManyToOneRelationship() {
        // Given: Un student existe
        assertNotNull(createdStudent);

        // When: Créer un wallet HORIZON avec plafond 36000
        WalletRequest walletRequest = new WalletRequest(
                createdStudent.trackingId(),
                "HORIZON",
                1000.0,
                36000.0
        );

        WalletResponse walletResponse = walletService.create(walletRequest);

        // Then: Vérifier le ManyToOne
        Wallet wallet = walletRepository.findByTrackingId(walletResponse.trackingId()).orElse(null);
        assertNotNull(wallet);
        assertNotNull(wallet.getStudent());
        assertEquals(createdStudent.trackingId(), wallet.getStudent().getTrackingId());
        assertEquals(36000.0, wallet.getPlafond());
    }

    @Test
    public void testSingleHorizonWalletPerStudent() {
        // Given: Un student existe
        assertNotNull(createdStudent);

        // When: Créer un wallet HORIZON unique pour l'étudiant
        WalletRequest walletRequest = new WalletRequest(
                createdStudent.trackingId(),
                "HORIZON",
                500.0,
                36000.0
        );

        walletService.create(walletRequest);

        // Then: Vérifier qu'il n'y a qu'un seul wallet HORIZON associé au student
        Student student = studentRepository.findByTrackingId(createdStudent.trackingId()).orElse(null);
        assertNotNull(student);
        assertNotNull(student.getWallets());
        assertEquals(1, student.getWallets().size());

        Wallet wallet = student.getWallets().get(0);
        assertEquals("HORIZON", wallet.getTypeWallet().name());
        assertEquals(36000.0, wallet.getPlafond());
    }
}

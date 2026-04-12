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

import java.time.LocalDate;
import java.util.UUID;

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
                "test@student.com",
                "password123",
                "Jean",
                "Dupont",
                "MAT123456",
                com.backend.gns.domain.enums.StudentNiveau.L1,
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

        // When: Créer un wallet associé au student
        WalletRequest walletRequest = new WalletRequest(
                createdStudent.trackingId(),
                "RELAIS",
                0.0,
                36000.0
        );

        WalletResponse walletResponse = walletService.create(walletRequest);

        // Then: Vérifier la relation OneToMany
        assertNotNull(walletResponse);
        assertEquals(createdStudent.trackingId(), walletResponse.studentTrackingId());

        // Récupérer le student avec ses wallets
        Student student = studentRepository.findByTrackingId(createdStudent.trackingId()).orElse(null);
        assertNotNull(student);
        assertNotNull(student.getWallets());
        assertEquals(1, student.getWallets().size());

        Wallet wallet = student.getWallets().get(0);
        assertEquals(student.getId(), wallet.getStudent().getId());
    }

    @Test
    public void testWalletManyToOneRelationship() {
        // Given: Un student existe
        assertNotNull(createdStudent);

        // When: Créer un wallet
        WalletRequest walletRequest = new WalletRequest(
                createdStudent.trackingId(),
                "HORIZON",
                1000.0,
                54000.0
        );

        WalletResponse walletResponse = walletService.create(walletRequest);

        // Then: Vérifier le ManyToOne
        Wallet wallet = walletRepository.findByTrackingId(walletResponse.trackingId()).orElse(null);
        assertNotNull(wallet);
        assertNotNull(wallet.getStudent());
        assertEquals(createdStudent.trackingId(), wallet.getStudent().getTrackingId());
    }

    @Test
    public void testMultipleWalletsPerStudent() {
        // Given: Un student existe
        assertNotNull(createdStudent);

        // When: Créer deux wallets pour le même student
        WalletRequest wallet1Request = new WalletRequest(
                createdStudent.trackingId(),
                "RELAIS",
                0.0,
                36000.0
        );

        WalletRequest wallet2Request = new WalletRequest(
                createdStudent.trackingId(),
                "HORIZON",
                500.0,
                54000.0
        );

        WalletResponse wallet1 = walletService.create(wallet1Request);
        WalletResponse wallet2 = walletService.create(wallet2Request);

        // Then: Vérifier que les deux wallets sont associés au student
        Student student = studentRepository.findByTrackingId(createdStudent.trackingId()).orElse(null);
        assertNotNull(student);
        assertNotNull(student.getWallets());
        assertEquals(2, student.getWallets().size());

        boolean hasRelais = student.getWallets().stream()
                .anyMatch(w -> w.getTypeWallet().name().equals("RELAIS"));
        boolean hasHorizon = student.getWallets().stream()
                .anyMatch(w -> w.getTypeWallet().name().equals("HORIZON"));

        assertTrue(hasRelais && hasHorizon);
    }
}

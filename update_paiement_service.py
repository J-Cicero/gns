import os

filepath = 'src/main/java/com/backend/gns/paiement/domain/services/impl/PaiementServiceImpl.java'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Add imports
imports = """
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.paiement.application.dtos.requests.QrPaymentRequest;
import com.backend.gns.paiement.domain.enums.CommandeStatut;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.domain.services.WalletService;
import java.time.LocalDateTime;
"""

# Inject before class definition
content = content.replace('import org.springframework.transaction.annotation.Transactional;', 
                          'import org.springframework.transaction.annotation.Transactional;' + imports)

# Add dependencies
dependencies = """
  private final StudentRepository studentRepository;
  private final BoutiqueRepository boutiqueRepository;
  private final WalletService walletService;
"""

content = content.replace('private final ParametreGnsService parametreGnsService;',
                          'private final ParametreGnsService parametreGnsService;' + dependencies)

# Update constructor
old_constructor = """  public PaiementServiceImpl(
      PaiementRepository paiementRepository,
      CommandeRepository commandeRepository,
      WalletRepository walletRepository,
      PaiementMapper paiementMapper,
      ParametreGnsService parametreGnsService) {
    this.paiementRepository = paiementRepository;
    this.commandeRepository = commandeRepository;
    this.walletRepository = walletRepository;
    this.paiementMapper = paiementMapper;
    this.parametreGnsService = parametreGnsService;
  }"""

new_constructor = """  public PaiementServiceImpl(
      PaiementRepository paiementRepository,
      CommandeRepository commandeRepository,
      WalletRepository walletRepository,
      PaiementMapper paiementMapper,
      ParametreGnsService parametreGnsService,
      StudentRepository studentRepository,
      BoutiqueRepository boutiqueRepository,
      WalletService walletService) {
    this.paiementRepository = paiementRepository;
    this.commandeRepository = commandeRepository;
    this.walletRepository = walletRepository;
    this.paiementMapper = paiementMapper;
    this.parametreGnsService = parametreGnsService;
    this.studentRepository = studentRepository;
    this.boutiqueRepository = boutiqueRepository;
    this.walletService = walletService;
  }"""

content = content.replace(old_constructor, new_constructor)

# Add method
method = """
  @Override
  @Transactional
  public PaiementResponse processQrPayment(QrPaymentRequest request) {
    // 1. Récupérer la boutique
    Boutique boutique = boutiqueRepository.findByTrackingId(request.boutiqueTrackingId())
        .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée avec l'ID: " + request.boutiqueTrackingId()));

    // 2. Récupérer l'étudiant via le QR Token (ici on suppose que c'est le trackingId pour l'instant)
    UUID studentId;
    try {
        studentId = UUID.fromString(request.studentQrToken());
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Token QR invalide");
    }
    
    Student student = studentRepository.findByTrackingId(studentId)
        .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé avec ce QR Code"));

    if (student.getWallet() == null) {
        throw new IllegalStateException("Le wallet de l'étudiant est introuvable");
    }

    if (boutique.getWallet() == null) {
        throw new IllegalStateException("Le wallet de la boutique est introuvable");
    }

    BigDecimal montantTotal = request.montantTotal();

    // 3. Effectuer la transaction financière (Wallet)
    try {
        // Débiter l'étudiant (vérifie le solde automatiquement)
        walletService.debiter(student.getWallet().getTrackingId(), montantTotal);
        
        // Débiter la boutique (Consommation quota comme demandé)
        walletService.debiter(boutique.getWallet().getTrackingId(), montantTotal);
    } catch (Exception e) {
        throw new RuntimeException("Paiement échoué : " + e.getMessage());
    }

    // 4. Créer la Commande (Validation automatique)
    Commande commande = new Commande();
    commande.setTrackingId(UUID.randomUUID());
    commande.setReference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    commande.setStudent(student);
    commande.setBoutique(boutique);
    commande.setMontantTotal(montantTotal);
    commande.setDateCommande(LocalDateTime.now());
    commande.setStatut(CommandeStatut.VALIDEE);
    commande = commandeRepository.save(commande);

    // 5. Créer l'entité Paiement
    BigDecimal taux = parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.TAUX_COMMISSION_PAIEMENT);
    BigDecimal commission = montantTotal.multiply(taux);
    BigDecimal montantNetBoutique = montantTotal.subtract(commission);

    Paiement paiement = new Paiement();
    paiement.setTrackingId(UUID.randomUUID());
    paiement.setCommande(commande);
    paiement.setStudent(student);
    paiement.setWallet(student.getWallet());
    paiement.setMontantDebite(montantTotal);
    paiement.setCommission(commission);
    paiement.setMontantNetBoutique(montantNetBoutique);
    paiement.setDate(LocalDateTime.now());
    paiement.setTypePaiement(PaiementType.ACHAT);
    paiement.setStatutPaiement(PaiementStatut.EFFECTUE);
    
    Paiement savedPaiement = paiementRepository.save(paiement);

    return paiementMapper.toResponse(savedPaiement);
  }
"""

content = content.replace('  public PaiementResponse create(PaiementRequest request) {', method + '\n  @Override\n  @Transactional\n  public PaiementResponse create(PaiementRequest request) {')

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated PaiementServiceImpl.java")

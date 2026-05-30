package com.backend.gns.core.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private static final String SENDER = "noreply@studcash.tg";

  @Override
  public void envoyerConfirmationInscription(String email, String nom) {
    Context context = new Context();
    context.setVariable("nom", nom);
    String htmlContent = templateEngine.process("emails/confirmation-inscription", context);
    envoyer(email, "studCash — Compte créé", htmlContent);
  }

  @Override
  public void envoyerKYCValide(String email, String nom) {
    Context context = new Context();
    context.setVariable("nom", nom);
    String htmlContent = templateEngine.process("emails/kyc-valide", context);
    envoyer(email, "studCash — Dossier validé ✓", htmlContent);
  }

  @Override
  public void envoyerKYCRejete(String email, String nom, String motif) {
    Context context = new Context();
    context.setVariable("nom", nom);
    context.setVariable("motif", motif);
    String htmlContent = templateEngine.process("emails/kyc-rejete", context);
    envoyer(email, "studCash — Action requise", htmlContent);
  }

  @Override
  public void envoyerNotificationVersement(String email, String nom, String montant) {
    Context context = new Context();
    context.setVariable("nom", nom);
    context.setVariable("montant", montant);
    String htmlContent = templateEngine.process("emails/versement-notification", context);
    envoyer(email, "studCash — Avance disponible", htmlContent);
  }

  @Override
  public void envoyerAlerteSoldeFaible(String email, String nom, String solde) {
    Context context = new Context();
    context.setVariable("nom", nom);
    context.setVariable("solde", solde);
    String htmlContent = templateEngine.process("emails/alerte-solde-faible", context);
    envoyer(email, "studCash — Solde faible", htmlContent);
  }

  private void envoyer(String destinataire, String sujet, String contenuHtml) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(SENDER);
      helper.setTo(destinataire);
      helper.setSubject(sujet);
      helper.setText(contenuHtml, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Erreur lors de l'envoi de l'email à " + destinataire, e);
    }
  }
}

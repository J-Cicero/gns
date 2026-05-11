package com.backend.gns.Shared.email;

public interface EmailService {
  void envoyerConfirmationInscription(String email, String nom);

  void envoyerKYCValide(String email, String nom);

  void envoyerKYCRejete(String email, String nom, String motif);

  void envoyerNotificationVersement(String email, String nom, String montant);

  void envoyerAlerteSoldeFaible(String email, String nom, String solde);
}

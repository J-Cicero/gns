package com.backend.gns.Shared.security.utils;

import com.backend.gns.Shared.security.adapters.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utilitaires de sécurité pour accéder aux informations de l'utilisateur courant.
 */
public class SecurityUtils {

    private SecurityUtils() {
        // Classe utilitaire, pas d'instanciation
    }

    /**
     * Récupère le trackingId de l'utilisateur actuellement authentifié.
     *
     * @return UUID du l'utilisateur courant
     * @throws AccessDeniedException si aucun utilisateur n'est authentifié
     */
    public static UUID getCurrentUserTrackingId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Utilisateur non authentifié");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal)) {
            throw new AccessDeniedException("Impossible de récupérer le trackingId de l'utilisateur");
        }

        return ((UserPrincipal) principal).getTrackingId();
    }

    /**
     * Récupère l'adresse email de l'utilisateur actuellement authentifié.
     *
     * @return Email de l'utilisateur courant
     * @throws AccessDeniedException si aucun utilisateur n'est authentifié
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Utilisateur non authentifié");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal)) {
            throw new AccessDeniedException("Impossible de récupérer l'email de l'utilisateur");
        }

        return ((UserPrincipal) principal).getEmail();
    }

    /**
     * Vérifie que l'utilisateur courant est le propriétaire de la ressource.
     *
     * @param resourceTrackingId UUID de la ressource
     * @throws AccessDeniedException si l'utilisateur n'est pas le propriétaire
     */
    public static void verifyResourceOwnership(UUID resourceTrackingId) {
        UUID currentUserTrackingId = getCurrentUserTrackingId();

        if (!resourceTrackingId.equals(currentUserTrackingId)) {
            throw new AccessDeniedException("Accès refusé : vous n'êtes pas autorisé à accéder cette ressource");
        }
    }

    /**
     * Récupère le UserPrincipal courant.
     *
     * @return UserPrincipal courant
     * @throws AccessDeniedException si aucun utilisateur n'est authentifié
     */
    public static UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Utilisateur non authentifié");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserPrincipal)) {
            throw new AccessDeniedException("Impossible de récupérer les informations de l'utilisateur");
        }

        return (UserPrincipal) principal;
    }
}

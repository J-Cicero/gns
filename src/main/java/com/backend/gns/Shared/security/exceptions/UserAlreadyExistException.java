package com.backend.gns.Shared.security.exceptions;

/**
 * Exception levée lorsqu'on tente de créer un utilisateur avec un email/identifiant déjà existant
 * Étend AlreadyExistException pour respecter la hiérarchie d'exceptions métier
 */
public class UserAlreadyExistException extends AlreadyExistException {
    public UserAlreadyExistException(String message) {
        super(message);
    }

    public UserAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
